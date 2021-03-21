import math
import socket
import struct
import time
import threading
import traceback

from socket import timeout

from fishtankclient.engine.input import PlayerInput
from fishtankclient.engine.input.PlayerInputs import PlayerInputFactory
from fishtankclient.engine.message.ClientInternalMessage import ClientInternalState


class UDPServerCommunication:
    MAGIC = 0xAA

    def __init__(self, serializer_factory, message_factory, address="127.0.0.1", port=7453):
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
        # self._listening_socket.connect((self._address, self._port))
        # self._listening_socket.connect()
        self._socket.settimeout(10)
        self.connected = True
        self.callback = server_connection_callback
        self._receiving_thread.start()

    def send_message(self, message):
        serialiser = serializer_factory.obtain()
        try:
            serialiser.setup()
            message.serialize(serialiser)
            self.send_packet(serialiser.get_buffer())
        finally:
            serialiser.free()

    def send_packet(self, packet):
        if self.connected:
            l = len(packet)
            packet[0:0] = struct.pack('B', UDPServerCommunication.MAGIC + (l >> 8))
            packet[1:1] = struct.pack('B', l & 0xFF)
            # self._socket.sendto(packet, (self._address, self._port))
            self._socket.sendto(packet, (self._address, self._port))

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
                        if b1 & 0xfe == UDPServerCommunication.MAGIC:
                            expected_size = (b1 & 1) * 256 + b2
                            if expected_size == deserializer.get_total_size():
                                message = self._message_factory.create_message(deserializer)
                                self.callback.receive_message(message)
                            else:
                                print("Expected size " + str((b1 & 1) * 256 + b2) + " but got " + deserializer.get_total_size())
                        else:
                            print("Expected MAGIC " + hex(UDPServerCommunication.MAGIC) + " but got " + hex(b1))
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

    serializer_factory = ByteSerializerFactory()
    message_factory = MessageFactory()

    udpServerModule = UDPServerCommunication(serializer_factory, message_factory, port=7453)

    player_input_factory = PlayerInputFactory()

    player_input_message = message_factory.obtain(MessageCode.PlayerInput)


    class Handler:
        session_id = 0
        connected = False

        def connected(self):
            print("Connected to server")
            with message_factory.obtain(MessageCode.ClientInternal) as message:
                message.set_state(ClientInternalState.RequestServerDetails)
                udpServerModule.send_message(message)

        def receive_message(self, message):
            with message:
                if message.get_type() == MessageCode.Chat:
                    print(f"Chat: {message.get_line()}")
                elif message.get_type() == MessageCode.ServerInternal:
                    print(f"Received message {message.get_state()}, message '{message.get_message()}'")
                    with message_factory.obtain(MessageCode.ClientAuthenticate) as outbound_message:
                        udpServerModule.send_message(outbound_message)
                elif message.get_type() == MessageCode.ServerClientAuthenticated:
                    print(f"Received ServerClientAuthenticated: session id '{message.get_session_id()}'")
                    self.session_id = message.get_session_id()
                    player_input_message.set_session_id(self.session_id)
                    connected = True
                else:
                    print(f"Server: {message}")

        def send_cam_pos(self, x, y, z):
            if self.connected:
                player_inputs = player_input_message.get_player_inputs()
                player_input = player_input_factory.create_new()
                player_input.cam_x(x)
                player_input.cam_y(y)
                player_input.cam_z(z)
                player_inputs.clear()
                player_inputs.add(player_input)
                udpServerModule.send_message(player_input_message)


    handler = Handler()
    udpServerModule.connect(Handler())

    # # message = message_factory.obtain(MessageCode.Nop)
    # message = message_factory.obtain(MessageCode.PlayerServerUpdate)
    # message.set_values(1, 2, [3, 4, 5], [0, 0, 0], [6, 7, 8, 9], 10, 0)
    # serializer = serializer_factory.obtain()
    # serializer.setup()
    #
    # message.serialize(serializer)
    # udpServerModule.send(serializer.get_buffer())

    a = 0
    while True:
        time.sleep(0.025)
        if handler.connected:
            x = 4 * math.sin(a)  # + 4.0
            y = 4 * math.cos(a) + 4.0
            z = 4 * math.sin(a)
            a = a + math.pi / 50.0
            if a > math.pi * 2.0:
                a = a - math.pi * 2.0
            handler.send_cam_pos(x, -z, y)
            # print(f"{x} x {y}")
