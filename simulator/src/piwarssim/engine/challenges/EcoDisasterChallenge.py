import random

from piwarssim.engine.challenges.AbstractChallenge import AbstractChallenge
from piwarssim.engine.simulation.PiWarsSimObjectTypes import PiWarsSimObjectTypes
from piwarssim.engine.simulation.objects import BarrelColour
from piwarssim.engine.simulation.rovers.AbstractRoverSimObject import AbstractRoverSimObject
from piwarssim.engine.utils.Shapes import polygon_from_box


def distance2(x1, y1, x2, y2):
    return (x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2)


class EcoDisasterChallenge(AbstractChallenge):
    CHALLENGE_WIDTH = 2200
    BARRELS_AREA = 1600

    WALL_POLYGONS = [
            polygon_from_box(-CHALLENGE_WIDTH / 2, -CHALLENGE_WIDTH / 2 - 1,  CHALLENGE_WIDTH / 2, -CHALLENGE_WIDTH / 2),
            polygon_from_box(-CHALLENGE_WIDTH / 2 - 1, -CHALLENGE_WIDTH / 2, -CHALLENGE_WIDTH / 2,  CHALLENGE_WIDTH / 2),
            polygon_from_box(-CHALLENGE_WIDTH / 2,  CHALLENGE_WIDTH / 2,  CHALLENGE_WIDTH / 2,  CHALLENGE_WIDTH / 2 + 1),
            polygon_from_box( CHALLENGE_WIDTH / 2, -CHALLENGE_WIDTH / 2,  CHALLENGE_WIDTH / 2 + 1,  CHALLENGE_WIDTH / 2)
    ]

    FLOOR_POLYGON = polygon_from_box(-CHALLENGE_WIDTH / 2, -CHALLENGE_WIDTH / 2, CHALLENGE_WIDTH / 2, CHALLENGE_WIDTH / 2)

    def __init__(self):
        super(EcoDisasterChallenge, self).__init__("EcoDisaster")
        self.camera_id = 0
        self.rover_id = 0
        self.wall_polygons = EcoDisasterChallenge.WALL_POLYGONS
        self.floor_polygons = [EcoDisasterChallenge.FLOOR_POLYGON]
        self.barrels = []
        self.numbe_of_barrels = 12
        self.reset_barrels()

    def after_sim_object_added(self, sim_object):
        super(EcoDisasterChallenge, self).after_sim_object_added(sim_object)
        if isinstance(sim_object, AbstractRoverSimObject):
            camera_attachment = self._sim_object_factory.obtain(PiWarsSimObjectTypes.CameraAttachment)
            camera_attachment.set_id(self.new_id())
            camera_attachment.attach_to_rover(sim_object)
            self.add_new_sim_object_immediately(camera_attachment)
            self.camera_id = camera_attachment.get_id()

            self.rover_id = sim_object.get_id()
            # reset_rover()

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
