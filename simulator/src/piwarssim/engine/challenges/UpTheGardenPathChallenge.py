import random

from piwarssim.engine.challenges.AbstractChallenge import AbstractChallenge
from piwarssim.engine.simulation.GameMessageSimObject import GameMessageSimObject
from piwarssim.engine.simulation.PiWarsSimObjectTypes import PiWarsSimObjectTypes
from piwarssim.engine.simulation.objects import BarrelColour, ToyCubeColour
from piwarssim.engine.simulation.rovers.AbstractRoverSimObject import AbstractRoverSimObject
from piwarssim.engine.utils.Shapes import polygon_from_box


def distance2(x1, y1, x2, y2):
    return (x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2)


class UpTheGardenPathChallenge(AbstractChallenge):
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
        super(UpTheGardenPathChallenge, self).__init__("UpTheGardenPath")
        self.wall_polygons = UpTheGardenPathChallenge.WALL_POLYGONS
        self.floor_polygons = [UpTheGardenPathChallenge.FLOOR_POLYGON]

    def process(self, timestamp):
        super(UpTheGardenPathChallenge, self).process(timestamp)

        game_message_object = self.get_game_message_object()
        if timestamp < 5:
            game_message_object.message = "Starting Eco Challenge"
        else:
            game_message_object.message = ""
        game_message_object.set_timer_tens((300 - timestamp) * 10, self)
