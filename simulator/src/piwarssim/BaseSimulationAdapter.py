
class BaseSimulationAdapter:
    def __init__(self):
        self.simulation_runner = None
        self.server_engine = None
        self.challenge = None
        self.sim_rover_id = None

    def set_simulation_runner(self, simulation_runner):
        self.simulation_runner = simulation_runner

    def set_server_engine(self, server_engine):
        self.server_engine = server_engine
        self.challenge = server_engine.challenge

    def set_sim_rover_id(self, sim_rover_id):
        self.sim_rover_id = sim_rover_id

    def define_arguments(self, args_parser):
        pass

    def process_arguments(self, args):
        pass

    def update(self):
        pass