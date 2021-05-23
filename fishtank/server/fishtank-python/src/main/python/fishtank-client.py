import math
import time

from fishtankclient.WiiMote import WiiMote
from fishtankclient.engine.input.PlayerInputs import PlayerInputFactory
from fishtankclient.engine.message.ClientInternalMessage import ClientInternalState
from fishtankclient.engine.transfer import UDPServerCommunication
from fishtankclient.engine.transfer.ByteSerializerFactory import ByteSerializerFactory
from fishtankclient.engine.message.MessageFactory import MessageFactory
from fishtankclient.engine.message.MessageCode import MessageCode
from vl53l1x_handler import VL53L1X_Handler


serializer_factory = ByteSerializerFactory()
message_factory = MessageFactory()

udpServerModule = UDPServerCommunication(serializer_factory, message_factory, port=7453)

player_input_factory = PlayerInputFactory()

player_input_message = message_factory.obtain(MessageCode.PlayerInput)


class Handler:
    def __init__(self):
        self.session_id = 0
        self.is_connected = False
        self.trigger = False

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
                self.is_connected = True
                print(f"Connected {self.is_connected}")
            else:
                print(f"Server: {message}")

    def send_cam_pos(self, x, y, z):
        if self.is_connected:
            player_inputs = player_input_message.get_player_inputs()
            player_input = player_input_factory.create_new()
            player_input.cam_x(x)
            player_input.cam_y(y)
            player_input.cam_z(z)
            player_input.trigger(self.trigger)
            player_inputs.clear()
            player_inputs.add(player_input)
            udpServerModule.send_message(player_input_message)

    def trigger_callback(self, trigger):
        self.trigger = trigger


wiimote = WiiMote()
wiimote.start_connecting()


handler = Handler()
udpServerModule.connect(handler)

vl53l1x = VL53L1X_Handler(handler.trigger_callback)
vl53l1x.start()

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
    if handler.is_connected:
        if wiimote.initialised:
            wx = - (wiimote.ir_list[0][1] - 512) / 10.0 # 32.0 #64.0
            wy = -(wiimote.ir_list[0][2] - 512) / 10.0  # 32.0 - 15 # 33.0 # 64.0 - 33
            x = wx
            y = wx + 64.0
            z = -wy + 32.0
            # y = y - 32.0
            handler.send_cam_pos(x, z, y)
            # print(f"{x} x {y}")
        else:
            if wiimote.connected and not wiimote.initialising:
                wiimote.init_wiimote()
            x = 4 * math.sin(a)  # + 4.0
            y = 4 * math.cos(a) + 4.0
            z = 4 * math.sin(a)
            a = a + math.pi / 50.0
            if a > math.pi * 2.0:
                a = a - math.pi * 2.0
            handler.send_cam_pos(x, -z, y)
            # print(f"{x} x {y}")
    # else:
    #     print(f"handler.is_connected {handler.is_connected}")
