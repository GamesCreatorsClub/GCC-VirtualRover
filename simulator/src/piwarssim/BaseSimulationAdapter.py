
class BaseSimulationAdapter:
    def __init__(self):
        self.simulation_runner = None
        self.server_engine = None
        self.challenge = None
        self.sim_rover_id = None
        self.game_message_object_id = None

    def set_simulation_runner(self, simulation_runner):
        self.simulation_runner = simulation_runner

    def set_server_engine(self, server_engine):
        self.server_engine = server_engine
        self.challenge = server_engine.challenge

    def set_sim_rover_id(self, sim_rover_id):
        self.sim_rover_id = sim_rover_id

    def set_game_message_object_id(self, game_message_object_id):
        self.game_message_object_id = game_message_object_id

    def get_sim_rover_id(self):
        """
        Returns id of rover in simulation.
        :return: id of simulation object representing rover.
        """
        return self.sim_rover_id

    def get_game_message_object_id(self):
        """
        Returns id of game message object in simulation.
        :return: id of simulation object representing game message object.
        """
        return self.game_message_object_id

    def define_arguments(self, args_parser):
        """
        This is invoked before any other method on adapter allowing adapter to set up command line parameter definitions
        it will use.
        :param args_parser: argument parser (argparse.ArgumentParser)
        """
        pass

    def process_arguments(self, args):
        """
        Invoked after arguments were parsed and available for the adapter.
        :param args: arguments parsed by parser passed in 'define_arguments' method
        """
        pass

    def update(self, timestamp, delta):
        """
        Invoked for every tick of the loop with current timestamp.

        :param timestamp: absolute simulation timestamp
        :param delta: delta time passed since last call
        """
        pass

    def draw(self, screen, screen_world_rect):
        """
        This method is invoked regularly to draw simulation world on python side (if any)
        :param screen: surface to draw the world representation
        :param screen_world_rect: rectangle representing where it should be drawn on
        """
        pass

    def get_challenge_name(self):
        """
        Optional method to implement. If '--challenge' command line argument is not supplied
        then this method might be called to obtain name of challenge that will be simulated.
        :return: name of the challenge (from piwarsim.challenges.Challenges enum)
        """
        return None
