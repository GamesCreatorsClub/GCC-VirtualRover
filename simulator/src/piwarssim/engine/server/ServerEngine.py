from piwarssim.engine.Engine import Engine
from piwarssim.engine.message.MessageFactory import MessageFactory


class ServerEngine(Engine):
    def __init__(self):
        super(ServerEngine, self).__init__()
        self.message_factory = MessageFactory()

    def main_loop(self):
        pass

    def updated_sessions(self):
        pass

    def calculate_updates(self, simulation_state):
        pass


if __name__ == '__main__':
    from piwarssim.engine.transfer.UDPServerModule import UDPServerModule
    from piwarssim.engine.transfer.ByteSerializerFactory import ByteSerializerFactory
    from piwarssim.engine.simulation.rovers.RoverType import RoverType
    from piwarssim.engine.challenges.PiNoonChallenge import PiNoonChallenge

    pi_noon_challenge = PiNoonChallenge()

    server_engine = ServerEngine()
    server_engine.challenge = pi_noon_challenge

    pi_noon_challenge.process(1) # 1 second into the game
    rover = pi_noon_challenge.spawn_rover(RoverType.GCC)

    serializer_factory = ByteSerializerFactory()
    message_factory = MessageFactory()

    udpServerModule = UDPServerModule(serializer_factory, message_factory)

    print(str(rover))

    udpServerModule.process()
