
import socket
import struct
import traceback
import time
import threading

from socket import timeout


class TCPServerCommunication:
    MAGIC = 0xA8

    def __init__(self, serializer_factory, message_factory, address="127.0.0.1", port=7454):
        self._serializer_factory = serializer_factory
        self._message_factory = message_factory
        self._address = address
        self._port = port
        self._socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        self._socket.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)

        self._receiving_thread = threading.Thread(target=self.receiving_loop, daemon=True)
        self.callback = None
        self.connected = False

    def is_connected(self):
        return self.connected

    def connect(self, server_connection_callback):
        self._socket.connect((self._address, self._port))
        self.connected = True
        self.callback = server_connection_callback
        self._receiving_thread.start()

    def send(self, packet):
        if self.connected:
            packet = self.add_packet_header(packet)
            try:
                self._socket.sendall(packet)
            except:
                pass

    @staticmethod
    def add_packet_header(packet):
        l = len(packet)
        packet[0:0] = struct.pack('B', TCPServerCommunication.MAGIC + (l >> 8))
        packet[1:1] = struct.pack('B', l & 0xFF)
        return packet

    def receive_message(self):
        if self.connected:
            try:
                data = self._client_socket.recv(2)
                if len(data) == 0:
                    self._client_socket = None
                else:
                    if data[0] & 0xf8 == TCPServerCommunication.MAGIC:
                        expected_size = (data[0] & 7) * 256 + data[1]
                        data = self._client_socket.recv(expected_size)

                        deserializer = self._serializer_factory.obtain()
                        deserializer.setup()
                        buf = deserializer.get_buffer()
                        buf += data

                        message = self._message_factory.create_message(deserializer)
                        self.callback.receive_message(message)
                    else:
                        print("Expected MAGIC " + hex(TCPServerCommunication.MAGIC) + " but got " + hex(data[0]))
            except timeout:
                time.sleep(0.05)
                pass
            except ConnectionResetError:
                time.sleep(0.05)
                self._client_socket = None
            except Exception as ex:
                time.sleep(0.05)
                print("Error receiving and processing message: " + str(ex) + "\n" + ''.join(traceback.format_tb(ex.__traceback__)))
        else:
            time.sleep(0.05)

    def receiving_loop(self):
        try:
            self._connected = True
            self.callback.connected()

            while True:
                self.receive_message()
        except Exception as ex:
            print("Error receiving and processing message: " + str(ex) + "\n" + ''.join(traceback.format_tb(ex.__traceback__)))


if __name__ == '__main__':
    from fishtankclient.engine.transfer.ByteSerializerFactory import ByteSerializerFactory
    from fishtankclient.engine.message.MessageFactory import MessageFactory
    from fishtankclient.engine.message.MessageCode import MessageCode

    serializer_factory = ByteSerializerFactory()
    message_factory = MessageFactory()

    class DummyEngine:
        def __init__(self):
            self.send_method = None
            self.serialiser_factory = None
            self.message = None

        def register_sender(self, send_method, serialiser_factory):
            self.send_method = send_method
            self.serialiser_factory = serialiser_factory

        def receive_message(self, message):
            self.message = message

    engine = DummyEngine()

    tcpServerModule = TCPServerCommunication(serializer_factory, message_factory, port=7777)

    with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as client_socket:
        client_socket.connect(('127.0.0.1', 7777))

        serializer = serializer_factory.obtain()
        serializer.setup()

        message = message_factory.obtain(MessageCode.Chat)
        message.set_line("Hello")
        message.set_origin("Me")
        message.serialize(serializer)
        message_data = TCPServerCommunication.add_packet_header(serializer.get_buffer())
        client_socket.send(message_data)

        started = time.time()
        while time.time() - started < 2 and engine.message is None:
            tcpServerModule.receive_message()

        received_message = engine.message
        print("Got on server '" + str(received_message.get_origin()) + ": " + str(received_message.get_line() + "'"))

        engine.send_method(message_data)

        client_data = client_socket.recv(len(message_data))
        print("Got on client " + str(len(client_data)) + " bytes that are " + ("same" if client_data == message_data else "different") + " to what is sent")
