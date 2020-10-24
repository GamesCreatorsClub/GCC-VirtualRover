import random

from piwarssim.engine.challenges.AbstractChallenge import AbstractChallenge
from piwarssim.engine.simulation.GameMessageSimObject import GameMessageSimObject
from piwarssim.engine.simulation.PiWarsSimObjectTypes import PiWarsSimObjectTypes
from piwarssim.engine.simulation.objects import BarrelColour, ToyCubeColour
from piwarssim.engine.simulation.rovers.AbstractRoverSimObject import AbstractRoverSimObject
from piwarssim.engine.utils.Shapes import polygon_from_box


def distance2(x1, y1, x2, y2):
    return (x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2)


class TidyUpTheToysChallenge(AbstractChallenge):
    CHALLENGE_WIDTH = 1500

    WALL_POLYGONS = [
            polygon_from_box(-CHALLENGE_WIDTH / 2, -CHALLENGE_WIDTH / 2 - 1,  CHALLENGE_WIDTH / 2, -CHALLENGE_WIDTH / 2),
            polygon_from_box(-CHALLENGE_WIDTH / 2 - 1, -CHALLENGE_WIDTH / 2, -CHALLENGE_WIDTH / 2,  CHALLENGE_WIDTH / 2),
            polygon_from_box(-CHALLENGE_WIDTH / 2,  CHALLENGE_WIDTH / 2,  CHALLENGE_WIDTH / 2,  CHALLENGE_WIDTH / 2 + 1),
            polygon_from_box( CHALLENGE_WIDTH / 2, -CHALLENGE_WIDTH / 2,  CHALLENGE_WIDTH / 2 + 1,  CHALLENGE_WIDTH / 2)
    ]

    FLOOR_POLYGON = polygon_from_box(-CHALLENGE_WIDTH / 2, -CHALLENGE_WIDTH / 2, CHALLENGE_WIDTH / 2, CHALLENGE_WIDTH / 2)

    def __init__(self):
        super(TidyUpTheToysChallenge, self).__init__("TidyUpTheToys")
        self.camera_id = 0
        self.rover_id = 0
        self.wall_polygons = TidyUpTheToysChallenge.WALL_POLYGONS
        self.floor_polygons = [TidyUpTheToysChallenge.FLOOR_POLYGON]
        self.green_cube = None
        self.red_cube = None
        self.blue_cube = None
        self.reset_toy_cubes()

    def after_sim_object_added(self, sim_object):
        super(TidyUpTheToysChallenge, self).after_sim_object_added(sim_object)
        if isinstance(sim_object, AbstractRoverSimObject):
            camera_attachment = self._sim_object_factory.obtain(PiWarsSimObjectTypes.CameraAttachment)
            camera_attachment.set_id(self.new_id())
            camera_attachment.attach_to_rover(sim_object)
            self.add_new_sim_object_immediately(camera_attachment)
            self.camera_id = camera_attachment.get_id()

            self.rover_id = sim_object.get_id()
            # reset_rover()
        if isinstance(sim_object, GameMessageSimObject):
            game_message_object = self.get_game_message_object()
            game_message_object.has_timer = True
            game_message_object.timer_stopped = False
            game_message_object.set_timer_tens(3000, self)

    def process(self, timestamp):
        super(TidyUpTheToysChallenge, self).process(timestamp)

        game_message_object = self.get_game_message_object()
        if timestamp < 5:
            game_message_object.message = "Starting Eco Challenge"
        else:
            game_message_object.message = ""
        game_message_object.set_timer_tens((300 - timestamp) * 10, self)

    def reset_toy_cubes(self):

        self.red_cube = self._sim_object_factory.obtain(PiWarsSimObjectTypes.ToyCubeObject)
        self.green_cube = self._sim_object_factory.obtain(PiWarsSimObjectTypes.ToyCubeObject)
        self.blue_cube = self._sim_object_factory.obtain(PiWarsSimObjectTypes.ToyCubeObject)

        self.red_cube.set_id(self.new_id())
        self.green_cube.set_id(self.new_id())
        self.blue_cube.set_id(self.new_id())

        self.red_cube.set_position_2(0,  350)
        self.green_cube.set_position_2(-400,  350)
        self.blue_cube.set_position_2(400,  350)

        self.red_cube.set_cube_colour(ToyCubeColour.Red)
        self.green_cube.set_cube_colour(ToyCubeColour.Green)
        self.blue_cube.set_cube_colour(ToyCubeColour.Blue)

        self.add_new_sim_object_immediately(self.red_cube)
        self.add_new_sim_object_immediately(self.green_cube)
        self.add_new_sim_object_immediately(self.blue_cube)
