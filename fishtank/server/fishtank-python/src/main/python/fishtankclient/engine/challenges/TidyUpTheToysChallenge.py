import random

from fishtankclient.engine.challenges.AbstractChallenge import AbstractChallenge
from fishtankclient.engine.simulation.GameMessageSimObject import GameMessageSimObject
from fishtankclient.engine.simulation.PiWarsSimObjectTypes import PiWarsSimObjectTypes
from fishtankclient.engine.simulation.objects import BarrelColour, ToyCubeColour
from fishtankclient.engine.simulation.rovers.AbstractRoverSimObject import AbstractRoverSimObject
from fishtankclient.engine.utils.Shapes import polygon_from_box


def distance2(x1, y1, x2, y2):
    return (x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2)


class TidyUpTheToysChallenge(AbstractChallenge):
    CHALLENGE_WIDTH = 1500
    WALL_THICKNESS = 100

    WALL_POLYGONS = [
            polygon_from_box(-CHALLENGE_WIDTH / 2, -CHALLENGE_WIDTH / 2 - WALL_THICKNESS,  CHALLENGE_WIDTH / 2, -CHALLENGE_WIDTH / 2),
            polygon_from_box(-CHALLENGE_WIDTH / 2 - WALL_THICKNESS, -CHALLENGE_WIDTH / 2, -CHALLENGE_WIDTH / 2,  CHALLENGE_WIDTH / 2),
            polygon_from_box(-CHALLENGE_WIDTH / 2,  CHALLENGE_WIDTH / 2,  CHALLENGE_WIDTH / 2,  CHALLENGE_WIDTH / 2 + WALL_THICKNESS),
            polygon_from_box( CHALLENGE_WIDTH / 2, -CHALLENGE_WIDTH / 2,  CHALLENGE_WIDTH / 2 + WALL_THICKNESS,  CHALLENGE_WIDTH / 2)
    ]

    FLOOR_POLYGON = polygon_from_box(-CHALLENGE_WIDTH / 2, -CHALLENGE_WIDTH / 2, CHALLENGE_WIDTH / 2, CHALLENGE_WIDTH / 2)

    def __init__(self):
        super(TidyUpTheToysChallenge, self).__init__("TidyUpTheToys")
        self.wall_polygons = TidyUpTheToysChallenge.WALL_POLYGONS
        self.floor_polygons = [TidyUpTheToysChallenge.FLOOR_POLYGON]
        self.green_cube = None
        self.red_cube = None
        self.blue_cube = None
        self.reset_toy_cubes()

    def process(self, timestamp):
        super(TidyUpTheToysChallenge, self).process(timestamp)

        game_message_object = self.get_game_message_object()
        if timestamp < 5:
            game_message_object.message = "Starting Eco Challenge"
        else:
            game_message_object.message = ""
        game_message_object.set_timer_tens((300 - timestamp) * 10, self)

    def reset_toy_cubes(self):

        if self.red_cube is None:
            self.red_cube = self._sim_object_factory.obtain(PiWarsSimObjectTypes.ToyCubeObject)
            self.red_cube.set_id(self.new_id())
            self.red_cube.set_cube_colour(ToyCubeColour.Red)
            self.add_new_sim_object_immediately(self.red_cube)
        if self.green_cube is None:
            self.green_cube = self._sim_object_factory.obtain(PiWarsSimObjectTypes.ToyCubeObject)
            self.green_cube.set_id(self.new_id())
            self.green_cube.set_cube_colour(ToyCubeColour.Green)
            self.add_new_sim_object_immediately(self.green_cube)
        if self.blue_cube is None:
            self.blue_cube = self._sim_object_factory.obtain(PiWarsSimObjectTypes.ToyCubeObject)
            self.blue_cube.set_id(self.new_id())
            self.blue_cube.set_cube_colour(ToyCubeColour.Blue)
            self.add_new_sim_object_immediately(self.blue_cube)

        self.red_cube.set_position_2(0,  350)
        self.green_cube.set_position_2(-400,  350)
        self.blue_cube.set_position_2(400,  350)

