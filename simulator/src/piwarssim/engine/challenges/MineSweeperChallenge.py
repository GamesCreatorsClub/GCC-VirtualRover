
import random

from piwarssim.engine.challenges.AbstractChallenge import AbstractChallenge
from piwarssim.engine.simulation.PiWarsSimObjectTypes import PiWarsSimObjectTypes
from piwarssim.engine.simulation.rovers.AbstractRoverSimObject import AbstractRoverSimObject
from piwarssim.engine.utils.Shapes import polygon_from_box


class MineSweeperChallenge(AbstractChallenge):

    COURSE_WIDTH = 1600
    WALL_HEIGHT = 200

    MINE_POLYGONS = [
            polygon_from_box(-COURSE_WIDTH / 2, -COURSE_WIDTH / 2,  -COURSE_WIDTH / 4, -COURSE_WIDTH / 4),
            polygon_from_box(-COURSE_WIDTH / 4, -COURSE_WIDTH / 2,  0, -COURSE_WIDTH / 4),
            polygon_from_box(0, -COURSE_WIDTH / 2,  COURSE_WIDTH / 4, -COURSE_WIDTH / 4),
            polygon_from_box(COURSE_WIDTH / 4, -COURSE_WIDTH / 2,  COURSE_WIDTH / 2, -COURSE_WIDTH / 4),

            polygon_from_box(-COURSE_WIDTH / 2, -COURSE_WIDTH / 4,  -COURSE_WIDTH / 4, 0),
            polygon_from_box(-COURSE_WIDTH / 4, -COURSE_WIDTH / 4,  0, 0),
            polygon_from_box(0, -COURSE_WIDTH / 4,  COURSE_WIDTH / 4, 0),
            polygon_from_box(COURSE_WIDTH / 4, -COURSE_WIDTH / 4,  COURSE_WIDTH / 2, 0),

            polygon_from_box(-COURSE_WIDTH / 2, 0,  -COURSE_WIDTH / 4, COURSE_WIDTH / 4),
            polygon_from_box(-COURSE_WIDTH / 4, 0,  0, COURSE_WIDTH / 4),
            polygon_from_box(0, 0,  COURSE_WIDTH / 4, COURSE_WIDTH / 4),
            polygon_from_box(COURSE_WIDTH / 4, 0,  COURSE_WIDTH / 2, COURSE_WIDTH / 4),

            polygon_from_box(-COURSE_WIDTH / 2, COURSE_WIDTH / 4,  -COURSE_WIDTH / 4, COURSE_WIDTH / 2),
            polygon_from_box(-COURSE_WIDTH / 4, COURSE_WIDTH / 4,  0, COURSE_WIDTH / 2),
            polygon_from_box(0, COURSE_WIDTH / 4,  COURSE_WIDTH / 4, COURSE_WIDTH / 2),
            polygon_from_box(COURSE_WIDTH / 4, COURSE_WIDTH / 4,  COURSE_WIDTH / 2, COURSE_WIDTH / 2)
        ]

    WALL_POLYGONS = [
            polygon_from_box(-COURSE_WIDTH / 2, -COURSE_WIDTH / 2 - 1,  COURSE_WIDTH / 2, -COURSE_WIDTH / 2),
            polygon_from_box(-COURSE_WIDTH / 2 - 1, -COURSE_WIDTH / 2, -COURSE_WIDTH / 2,  COURSE_WIDTH / 2),
            polygon_from_box(-COURSE_WIDTH / 2,  COURSE_WIDTH / 2,  COURSE_WIDTH / 2,  COURSE_WIDTH / 2 + 1),
            polygon_from_box( COURSE_WIDTH / 2, -COURSE_WIDTH / 2,  COURSE_WIDTH / 2 + 1,  COURSE_WIDTH / 2)
    ]

    FLOOR_POLYGON = polygon_from_box(-COURSE_WIDTH / 2, -COURSE_WIDTH / 2, COURSE_WIDTH / 2, COURSE_WIDTH / 2)

    def __init__(self):
        super(MineSweeperChallenge, self).__init__("MineSweeper")
        self.mine_sweeper_status_id = 0
        self._next_event = 0
        self.wall_polygons = MineSweeperChallenge.WALL_POLYGONS
        self.floor_polygons = [MineSweeperChallenge.FLOOR_POLYGON]
        self.register_after_sim_object_added_listener(self._after_sim_object_added_mine_sweeper_listener)

    def _after_sim_object_added_mine_sweeper_listener(self, challenge, sim_object):
        if isinstance(sim_object, AbstractRoverSimObject):
            mine_sweeper_state_object = self._sim_object_factory.obtain(PiWarsSimObjectTypes.MineSweeperStateObject)
            mine_sweeper_state_object.set_id(self.new_id())
            self.add_new_sim_object_immediately(mine_sweeper_state_object)
            mine_sweeper_state_object.set_state_bits(0)

    def process(self, timestamp):
        if timestamp > self._next_event:
            mine_sweeper_status = self.get_mine_sweeper_status()
            if mine_sweeper_status is not None:
                next_light = random.randint(0, 15)
                bit = 1 << next_light
                mine_sweeper_status.set_state_bits(bit)

                self._next_event = timestamp + 2

        super(MineSweeperChallenge, self).process(timestamp)

    def get_mine_sweeper_status(self):
        sim_state = self.get_current_sim_state()
        for game_object_id in sim_state:
            sim_object = sim_state[game_object_id]
            if sim_object.get_type() == PiWarsSimObjectTypes.MineSweeperStateObject:
                return sim_object
        return None
