import math
import time

from piwarssim.engine.Engine import Engine
from piwarssim.engine.input import PlayerInputs
from piwarssim.engine.message.MessageCode import MessageCode
from piwarssim.engine.message.ClientInternalMessage import ClientInternalMessage, ClientInternalState
from piwarssim.engine.message.PlayerInputMessage import PlayerInputMessage
from piwarssim.engine.message.MultiObjectRequestForFullUpdateMessage import MultiObjectRequestForFullUpdateMessage
from piwarssim.engine.message.MessageFactory import MessageFactory
from piwarssim.engine.message.ServerInternalMessage import ServerInternalState


class ServerEngine(Engine):
    def __init__(self, challenge):
        super(ServerEngine, self).__init__(challenge)
        self._message_factory = MessageFactory()
        self._serializer_factory = None
        self._sender = None
        self._player_inputs = PlayerInputs()
        self._send_full_update = True
        self._client_ready = False

    def register_sender(self, sender, serializer_factory):
        self._sender = sender
        self._serializer_factory = serializer_factory

    def receive_message(self, message):
        # self._received_messages.append(message)
        if isinstance(message, ClientInternalMessage):
            if message.get_state() == ClientInternalState.RequestServerDetails:
                self._client_ready = False

                authenticated_message = self._message_factory.obtain(MessageCode.ServerClientAuthenticated)
                authenticated_message.set_session_id(1)
                self.send_message(authenticated_message)

                server_internal_message = self._message_factory.obtain(MessageCode.ServerInternal)
                server_internal_message.set_state(ServerInternalState.GameMap)
                server_internal_message.set_message(self.challenge.get_challenge_id())
                self.send_message(server_internal_message)
            elif message.get_state() == ClientInternalState.ClientReady:
                self._client_ready = True
                self._send_full_update = True
        elif isinstance(message, MultiObjectRequestForFullUpdateMessage):
            # self.send_full_update()
            self._send_full_update = True
        elif isinstance(message, PlayerInputMessage):
            self._player_inputs.merge_inputs(self.challenge.get_frame_id(), message.get_player_inputs())

    def clear_client_ready(self):
        self._client_ready = False

    def is_client_ready(self):
        return self._client_ready

    def get_player_inputs(self):
        return self._player_inputs

    def main_loop(self):
        while True:
            time.sleep(1)

    def calculate_updates(self, simulation_state):
        pass

    def send_message(self, message):
        serialiser = self._serializer_factory.obtain()
        serialiser.setup()
        message.serialize(serialiser)
        self._sender(serialiser.get_buffer())

    def send_full_update(self):
        current_state = self.challenge.get_current_sim_state()  # TODO shouldn't it be get_previous_sim_state() ?

        message = self._message_factory.obtain(MessageCode.MultiObjectUpdate)
        message.set_frame_no(current_state.get_frame_no())
        for sim_object_id in current_state:
            message.add_new_sim_object(current_state[sim_object_id])

        self.send_message(message)

    def send_update(self):
        current_state = self.challenge.get_current_sim_state()  # TODO shouldn't it be get_previous_sim_state() ?

        message = self._message_factory.obtain(MessageCode.MultiObjectUpdate)
        message.set_frame_no(current_state.get_frame_no())
        for sim_object_id in current_state:
            if self._send_full_update:
                message.add_new_sim_object(current_state[sim_object_id])
            else:
                message.add_updated_sim_object(current_state[sim_object_id])

        if self._send_full_update:
            self._send_full_update = False

        self.send_message(message)


if __name__ == '__main__':
    from threading import Thread

    from piwarssim.engine.transfer.UDPServerModule import UDPServerModule
    from piwarssim.engine.transfer.ByteSerializerFactory import ByteSerializerFactory
    from piwarssim.engine.simulation.rovers.RoverType import RoverType
    from piwarssim.engine.challenges.PiNoonChallenge import PiNoonChallenge

    pi_noon_challenge = PiNoonChallenge()

    server_engine = ServerEngine(pi_noon_challenge)
    server_engine.challenge = pi_noon_challenge

    # pi_noon_challenge.process(0) # 1 second into the game
    rover = pi_noon_challenge.spawn_rover(RoverType.GCC)

    t = 0
    server_engine.process(t)

    serializer_factory = ByteSerializerFactory()
    message_factory = MessageFactory()

    udpServerModule = UDPServerModule(server_engine, serializer_factory, message_factory)

    print(str(rover))

    thread = Thread(target=udpServerModule.process, daemon=True)
    thread.start()
    # server_engine.main_loop()
    d = 0
    while True:
        time.sleep(0.02)
        d += 1
        if d > 359:
            d = 0

        position = rover.get_position()
        position[0] = math.sin(d * math.pi / 180) * 500
        position[1] = math.cos(d * math.pi / 180) * 500

        rover.set_bearing(-d)

        server_engine.process(t)
        server_engine.send_update()
        t += 1
