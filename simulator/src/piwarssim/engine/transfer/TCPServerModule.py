
import socket
import struct
import traceback
import time
import threading

from socket import timeout


class TCPServerModule:
    MAGIC = 0xAA

    def __init__(self, server_engine, serializer_factory, message_factory, address="0.0.0.0", port=7454):
        self._server_engine = server_engine
        self._serializer_factory = serializer_factory
        self._message_factory = message_factory
        self._address = address
        self._port = port
        self._server_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        self._server_socket.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
        self._server_socket.bind((self._address, self._port))
        self._server_socket.listen(3)
        self._client_address = None
        self._client_port = 0
        self._client_socket = None

        self._server_engine.register_sender(self.send, self._serializer_factory)

        self._server_socket_thread = threading.Thread(target=self.accept_clients, daemon=True)
        self._server_socket_thread.start()

    def is_connected(self):
        return self._client_socket is not None

    def send(self, packet):
        if self._client_socket is not None:
            packet = self.add_packet_header(packet)
            try:
                self._client_socket.sendall(packet)
            except:
                pass

    @staticmethod
    def add_packet_header(packet):
        l = len(packet)
        packet[0:0] = struct.pack('B', TCPServerModule.MAGIC + (l >> 8))
        packet[1:1] = struct.pack('B', l & 0xFF)
        return packet

    def accept_clients(self):
        self._server_socket.settimeout(10)
        while True:
            try:
                conn, addr = self._server_socket.accept()
                self._client_address = addr
                self._client_socket = conn
            except timeout:
                pass
            except Exception as ex:
                print("Error receiving and processing message: " + str(ex) + "\n" + ''.join(traceback.format_tb(ex.__traceback__)))

    def process(self):
        while True:
            self.receive_message()

    def receive_message(self):
        if self._client_socket is not None:
            try:
                data = self._client_socket.recv(2)
                if len(data) == 0:
                    self._client_socket = None
                else:
                    if data[0] & 0xfe == TCPServerModule.MAGIC:
                        expected_size = (data[0] & 1) * 256 + data[1]
                        data = self._client_socket.recv(expected_size)

                        deserializer = self._serializer_factory.obtain()
                        deserializer.setup()
                        buf = deserializer.get_buffer()
                        buf += data

                        message = self._message_factory.create_message(deserializer)
                        self._server_engine.receive_message(message)
                    else:
                        print("Expected MAGIC " + hex(TCPServerModule.MAGIC) + " but got " + hex(data[0]))
            except timeout:
                pass
            except ConnectionResetError:
                self._client_socket = None
            except Exception as ex:
                print("Error receiving and processing message: " + str(ex) + "\n" + ''.join(traceback.format_tb(ex.__traceback__)))
        else:
            time.sleep(1)


if __name__ == '__main__':
    from piwarssim.engine.transfer.ByteSerializerFactory import ByteSerializerFactory
    from piwarssim.engine.message.MessageFactory import MessageFactory
    from piwarssim.engine.message.MessageCode import MessageCode

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

    tcpServerModule = TCPServerModule(engine, serializer_factory, message_factory, port=7777)

    with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as client_socket:
        client_socket.connect(('127.0.0.1', 7777))

        serializer = serializer_factory.obtain()
        serializer.setup()

        message = message_factory.obtain(MessageCode.Chat)
        message.set_line("Hello")
        message.set_origin("Me")
        message.serialize(serializer)
        message_data = TCPServerModule.add_packet_header(serializer.get_buffer())
        client_socket.send(message_data)

        started = time.time()
        while time.time() - started < 2 and engine.message is None:
            tcpServerModule.receive_message()

        received_message = engine.message
        print("Got on server '" + str(received_message.get_origin()) + ": " + str(received_message.get_line() + "'"))

        engine.send_method(message_data)

        client_data = client_socket.recv(len(message_data))
        print("Got on client " + str(len(client_data)) + " bytes that are " + ("same" if client_data == message_data else "different") + " to what is sent")
