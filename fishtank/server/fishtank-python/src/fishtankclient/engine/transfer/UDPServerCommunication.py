
import socket
import struct
import threading
import traceback

from socket import timeout


class UDPServerModule:
    MAGIC = 0xAA

    def __init__(self, serializer_factory, message_factory, address="0.0.0.0", port=7454):
        self._serializer_factory = serializer_factory
        self._message_factory = message_factory
        self._address = address
        self._port = port
        self._socket = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
        self._socket.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)

        self._receiving_thread = threading.Thread(target=self.receiving_loop, daemon=True)
        self.callback = None
        self.connected = False

    def is_connected(self):
        return self.connected

    def connect(self, server_connection_callback):
        self._socket.connect((self._address, self._port))
        self._socket.settimeout(10)
        self.connected = True
        self.callback = server_connection_callback
        self._receiving_thread.start()

    def send(self, packet):
        if self.connected:
            l = len(packet)
            packet[0:0] = struct.pack('B', UDPServerModule.MAGIC + (l >> 8))
            packet[1:1] = struct.pack('B', l & 0xFF)
            self._socket.sendto(packet, (self._client_address, self._client_port))

    def receive_message(self):
        if self.connected:
            try:
                data, (addr, port) = self._socket.recvfrom(1024)
                # print("Received '" + str(data) + "' from " + str(addr) + ":" + str(port))

                self._client_address = addr
                self._client_port = port

                deserializer = self._serializer_factory.obtain()
                deserializer.setup()
                buf = deserializer.get_buffer()
                buf += data

                try:
                    b1 = deserializer.deserialize_unsigned_byte()
                    if deserializer.get_total_size() > 0:
                        b2 = deserializer.deserialize_unsigned_byte()
                        if b1 & 0xfe == UDPServerModule.MAGIC:
                            expected_size = (b1 & 1) * 256 + b2
                            if expected_size == deserializer.get_total_size():
                                message = self._message_factory.create_message(deserializer)
                                self.callback.receive_message(message)
                            else:
                                print("Expected size " + str((b1 & 1) * 256 + b2) + " but got " + deserializer.get_total_size())
                        else:
                            print("Expected MAGIC " + hex(UDPServerModule.MAGIC) + " but got " + hex(b1))
                    else:
                        print("Received only one byte but expected at least 2")
                finally:
                    deserializer.free()
            except timeout:
                pass
            except Exception as ex:
                print("Error receiving and processing message: " + str(ex) + "\n" + ''.join(traceback.format_tb(ex.__traceback__)))

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

    class Callback:
        def connected(self):
            print("Connected to server")

        def receive_message(self, message):
            print(f"Received message {message}")

    serializer_factory = ByteSerializerFactory()
    message_factory = MessageFactory()

    udpServerModule = UDPServerModule(serializer_factory, message_factory, port=7454)
    udpServerModule.connect(Callback())

    # # message = message_factory.obtain(MessageCode.Nop)
    # message = message_factory.obtain(MessageCode.PlayerServerUpdate)
    # message.set_values(1, 2, [3, 4, 5], [0, 0, 0], [6, 7, 8, 9], 10, 0)
    # serializer = serializer_factory.obtain()
    # serializer.setup()
    #
    # message.serialize(serializer)
    # udpServerModule.send(serializer.get_buffer())
