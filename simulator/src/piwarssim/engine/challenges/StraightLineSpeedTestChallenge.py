from piwarssim.engine.challenges.AbstractChallenge import AbstractChallenge
from piwarssim.engine.simulation.PiWarsSimObjectTypes import PiWarsSimObjectTypes
from piwarssim.engine.simulation.rovers.AbstractRoverSimObject import AbstractRoverSimObject
from piwarssim.engine.utils.Shapes import Polygon, polygon_from_box


def polygon_for_chicane(chicane_length, chicane_width, cut_modifier, x, y):
    if y > 0:
        return Polygon([
            (x - chicane_length / 2, y - chicane_width),
            (x - chicane_length / 2 - chicane_width * cut_modifier, y),
            (x + chicane_length / 2 + chicane_width * cut_modifier, y),
            (x + chicane_length / 2, y - chicane_width)
        ])
    else:
        return Polygon([
            (x - chicane_length / 2 - chicane_width * cut_modifier, y),
            (x - chicane_length / 2, y + chicane_width),
            (x + chicane_length / 2, y + chicane_width),
            (x + chicane_length / 2 + chicane_width * cut_modifier, y)
        ])


class StraightLineSpeedTestChallenge(AbstractChallenge):
    COURSE_WIDTH = 630 * 2  # 7200
    COURSE_LENGTH = 4800  # 7200
    CHICANE_LENGTH = 800
    CHICANE_WIDTH = 38
    CUT_MODIFIER = 1.5
    WALL_HEIGHT = 64
    FLOOR_POLYGON = polygon_from_box(-COURSE_LENGTH / 2, -315, COURSE_LENGTH / 2, 315)
    CHICANES_POLYGONS = [
        polygon_for_chicane(CHICANE_LENGTH, CHICANE_WIDTH, CUT_MODIFIER, -COURSE_LENGTH / 4, -305),
        polygon_for_chicane(CHICANE_LENGTH, CHICANE_WIDTH, CUT_MODIFIER, -COURSE_LENGTH / 4, 305),
        polygon_for_chicane(CHICANE_LENGTH, CHICANE_WIDTH, CUT_MODIFIER, COURSE_LENGTH / 4, -305),
        polygon_for_chicane(CHICANE_LENGTH, CHICANE_WIDTH, CUT_MODIFIER, COURSE_LENGTH / 4, 305)
    ]
    WALL_POLYGONS = [
        polygon_from_box(-COURSE_LENGTH / 2, -315, COURSE_LENGTH / 2, -305),
        polygon_from_box(-COURSE_LENGTH / 2, 305, COURSE_LENGTH / 2, 315)
    ]

    START_POLIGON = polygon_from_box(-COURSE_LENGTH / 2 - 10, -315, -COURSE_LENGTH / 2, 315)
    END_POLIGON = polygon_from_box(COURSE_LENGTH / 2, -315, COURSE_LENGTH / 2 + 10, 315)

    def __init__(self):
        super(StraightLineSpeedTestChallenge, self).__init__("StraightLineSpeedTest")
        self.camera_id = 0
        self.rover_id = 0
        self.wall_polygons = StraightLineSpeedTestChallenge.WALL_POLYGONS + StraightLineSpeedTestChallenge.CHICANES_POLYGONS
        self.floor_polygons = [StraightLineSpeedTestChallenge.FLOOR_POLYGON]

    def after_sim_object_added(self, sim_object):
        super(StraightLineSpeedTestChallenge, self).after_sim_object_added(sim_object)
        if isinstance(sim_object, AbstractRoverSimObject):
            camera_attachment = self._sim_object_factory.obtain(PiWarsSimObjectTypes.CameraAttachment)
            camera_attachment.set_id(self.new_id())
            camera_attachment.attach_to_rover(sim_object)
            self.add_new_sim_object_immediately(camera_attachment)
            self.camera_id = camera_attachment.get_id()

            self.rover_id = sim_object.get_id()
            # reset_rover()
