from piwarssim.engine import Engine
from piwarssim.engine.challenges import PiNoonChallenge
from piwarssim.engine.message import MessageFactory


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

    pi_noon_challenge = PiNoonChallenge()

    server_engine = ServerEngine()
    server_engine.challenge = pi_noon_challenge

    pi_noon_challenge.process(1) # 1 second into the game
    pi_noon_challenge.add_new_sim_object()
    