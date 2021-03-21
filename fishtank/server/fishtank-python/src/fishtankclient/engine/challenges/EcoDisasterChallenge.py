import random

from fishtankclient.engine.challenges.AbstractChallenge import AbstractChallenge
from fishtankclient.engine.simulation.GameMessageSimObject import GameMessageSimObject
from fishtankclient.engine.simulation.PiWarsSimObjectTypes import PiWarsSimObjectTypes
from fishtankclient.engine.simulation.objects import BarrelColour
from fishtankclient.engine.simulation.rovers.AbstractRoverSimObject import AbstractRoverSimObject
from fishtankclient.engine.utils.Shapes import polygon_from_box


def distance2(x1, y1, x2, y2):
    return (x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2)


class EcoDisasterChallenge(AbstractChallenge):
    CHALLENGE_WIDTH = 2200
    BARRELS_AREA = 1600
    WALL_THICKNESS = 100

    WALL_POLYGONS = [
            polygon_from_box(-CHALLENGE_WIDTH / 2, -CHALLENGE_WIDTH / 2 - WALL_THICKNESS,  CHALLENGE_WIDTH / 2, -CHALLENGE_WIDTH / 2),
            polygon_from_box(-CHALLENGE_WIDTH / 2 - WALL_THICKNESS, -CHALLENGE_WIDTH / 2, -CHALLENGE_WIDTH / 2,  CHALLENGE_WIDTH / 2),
            polygon_from_box(-CHALLENGE_WIDTH / 2,  CHALLENGE_WIDTH / 2,  CHALLENGE_WIDTH / 2,  CHALLENGE_WIDTH / 2 + WALL_THICKNESS),
            polygon_from_box( CHALLENGE_WIDTH / 2, -CHALLENGE_WIDTH / 2,  CHALLENGE_WIDTH / 2 + WALL_THICKNESS,  CHALLENGE_WIDTH / 2)
    ]

    FLOOR_POLYGON = polygon_from_box(-CHALLENGE_WIDTH / 2, -CHALLENGE_WIDTH / 2, CHALLENGE_WIDTH / 2, CHALLENGE_WIDTH / 2)

    def __init__(self):
        super(EcoDisasterChallenge, self).__init__("EcoDisaster")
        self.wall_polygons = EcoDisasterChallenge.WALL_POLYGONS
        self.floor_polygons = [EcoDisasterChallenge.FLOOR_POLYGON]
        self.barrels = []
        self.numbe_of_barrels = 12
        self.reset_barrels()

    def process(self, timestamp):
        # if timestamp > self._next_event:
        #     mine_sweeper_status = self.get_mine_sweeper_status()
        #     if mine_sweeper_status is not None:
        #         next_light = random.randint(0, 15)
        #         bit = 1 << next_light
        #         mine_sweeper_status.set_state_bits(bit)
        #
        #         self._next_event = timestamp + 2

        super(EcoDisasterChallenge, self).process(timestamp)

        game_message_object = self.get_game_message_object()
        if timestamp < 5:
            game_message_object.message = "Starting Eco Challenge"
        else:
            game_message_object.message = ""
        game_message_object.set_timer_tens((300 - timestamp) * 10, self)

    def reset_barrels(self):
        odd = True
        while len(self.barrels) < self.numbe_of_barrels:
            barrel = self._sim_object_factory.obtain(PiWarsSimObjectTypes.BarrelObject)
            barrel.set_id(self.new_id())
            if odd:
                barrel.set_barrel_colour(BarrelColour.Green)
            else:
                barrel.set_barrel_colour(BarrelColour.Red)

            x = random.randint(-EcoDisasterChallenge.BARRELS_AREA // 2, EcoDisasterChallenge.BARRELS_AREA // 2)
            y = random.randint(-EcoDisasterChallenge.BARRELS_AREA // 2, EcoDisasterChallenge.BARRELS_AREA // 2)

            while self.overlaps_other_barrles(x, y, barrel.get_circle().radius * 3):
                x = random.randint(-EcoDisasterChallenge.BARRELS_AREA // 2, EcoDisasterChallenge.BARRELS_AREA // 2)
                y = random.randint(-EcoDisasterChallenge.BARRELS_AREA // 2, EcoDisasterChallenge.BARRELS_AREA // 2)

            barrel.set_position_2(x, y)
            # check overlaping barrels

            self.add_new_sim_object_immediately(barrel)
            self.barrels.append(barrel.get_id())
            odd = not odd

        for i in range(0, 10):
            barrel = self._sim_object_factory.obtain(PiWarsSimObjectTypes.BarrelObject)
            barrel.set_id(self.new_id())
            if odd:
                barrel.set_barrel_colour(BarrelColour.Green)
            else:
                barrel.set_barrel_colour(BarrelColour.Red)
            barrel.set_position_2(0 + (i - 5) * 60, -550)
            self.add_new_sim_object_immediately(barrel)
            self.barrels.append(barrel.get_id())
            odd = not odd

    def overlaps_other_barrles(self, x, y, distance):
        if len(self.barrels) > 0:
            for i in range(0, len(self.barrels)):
                barrel = self.get_sim_object(self.barrels[i])
                barrel_circle = barrel.get_circle()
                if distance2(x, y, barrel_circle.x, barrel_circle.y) < distance * distance:
                    return True

        return False
