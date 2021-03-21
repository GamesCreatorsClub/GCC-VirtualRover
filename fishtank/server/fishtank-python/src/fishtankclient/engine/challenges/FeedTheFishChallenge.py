import random

from fishtankclient.engine.challenges.AbstractChallenge import AbstractChallenge
from fishtankclient.engine.simulation.GameMessageSimObject import GameMessageSimObject
from fishtankclient.engine.simulation.PiWarsSimObjectTypes import PiWarsSimObjectTypes
from fishtankclient.engine.simulation.objects import BarrelColour, ToyCubeColour
from fishtankclient.engine.simulation.rovers.AbstractRoverSimObject import AbstractRoverSimObject
from fishtankclient.engine.utils.Shapes import polygon_from_box


def distance2(x1, y1, x2, y2):
    return (x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2)


class FeedTheFishChallenge(AbstractChallenge):
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
        super(FeedTheFishChallenge, self).__init__("FeedTheFish")
        self.wall_polygons = FeedTheFishChallenge.WALL_POLYGONS
        self.floor_polygons = [FeedTheFishChallenge.FLOOR_POLYGON]
        self.fish_tower = None
        self.golf_balls = []
        self.reset_golf_balls()

    def process(self, timestamp):
        super(FeedTheFishChallenge, self).process(timestamp)

        game_message_object = self.get_game_message_object()
        if timestamp < 5:
            game_message_object.message = "Starting Eco Challenge"
        else:
            game_message_object.message = ""
        game_message_object.set_timer_tens((300 - timestamp) * 10, self)

    def reset_golf_balls(self):
        if self.fish_tower is None:
            self.fish_tower = self._sim_object_factory.obtain(PiWarsSimObjectTypes.FishTowerObject)
            self.fish_tower.set_id(self.new_id())
            self.add_new_sim_object_immediately(self.fish_tower)

        self.fish_tower.set_position_2(0,  350)

        while len(self.golf_balls) < 5:
            golf_ball = self._sim_object_factory.obtain(PiWarsSimObjectTypes.GolfBallObject)
            golf_ball.set_id(self.new_id())
            self.add_new_sim_object_immediately(golf_ball)
            self.golf_balls.append(golf_ball)

        for i in range(len(self.golf_balls)):
            golf_ball = self.golf_balls[i]
            golf_ball.set_position_2(-500 + i * 250, -200)
