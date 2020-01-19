import math

from piwarssim.engine.challenges.AbstractChallenge import AbstractChallenge
from piwarssim.engine.simulation.PiWarsSimObjectTypes import PiWarsSimObjectTypes
from piwarssim.engine.simulation.rovers.AbstractRoverSimObject import AbstractRoverSimObject
from piwarssim.engine.utils import Polygon


def polygon_for_wall1(y, width):
    return Polygon([
        - BlastOffChallenge.COURSE_LENGTH / 2, y + width / 2,
        - BlastOffChallenge.COURSE_LENGTH / 6, y + width / 2,
        - BlastOffChallenge.COURSE_LENGTH / 6, y - width / 2,
        - BlastOffChallenge.COURSE_LENGTH / 2, y - width / 2,
    ])


def polygon_for_wall2(y, width):
    return Polygon([
        - BlastOffChallenge.COURSE_LENGTH / 6, y + width / 2,
        - BlastOffChallenge.COURSE_LENGTH / 6 + BlastOffChallenge.COURSE_WIDTH, y + width / 2 + BlastOffChallenge.COURSE_WIDTH,
        - BlastOffChallenge.COURSE_LENGTH / 6 + BlastOffChallenge.COURSE_WIDTH, y - width / 2 + BlastOffChallenge.COURSE_WIDTH,
        - BlastOffChallenge.COURSE_LENGTH / 6, y - width / 2,
    ])


def polygon_for_wall3(y, width):
    return Polygon([
        - BlastOffChallenge.COURSE_LENGTH / 6 + BlastOffChallenge.COURSE_WIDTH, y + width / 2 + BlastOffChallenge.COURSE_WIDTH,
        BlastOffChallenge.COURSE_LENGTH / 6 - BlastOffChallenge.COURSE_WIDTH, y + width / 2 + BlastOffChallenge.COURSE_WIDTH,
        BlastOffChallenge.COURSE_LENGTH / 6 - BlastOffChallenge.COURSE_WIDTH, y - width / 2 + BlastOffChallenge.COURSE_WIDTH,
        - BlastOffChallenge.COURSE_LENGTH / 6 + BlastOffChallenge.COURSE_WIDTH, y - width / 2 + BlastOffChallenge.COURSE_WIDTH,
    ])


def polygon_for_wall4(y, width):
    adjust = (BlastOffChallenge.BOTTOM_WALL_ADJUST / 2)

    return Polygon([
        BlastOffChallenge. COURSE_LENGTH / 6 - BlastOffChallenge.COURSE_WIDTH, y + width / 2 + BlastOffChallenge.COURSE_WIDTH,
        BlastOffChallenge.COURSE_LENGTH / 6, y + width / 2,
        BlastOffChallenge.COURSE_LENGTH / 6, y - width / 2,
        BlastOffChallenge.COURSE_LENGTH / 6 - BlastOffChallenge.COURSE_WIDTH, y - width / 2 + BlastOffChallenge.COURSE_WIDTH,
    ])


def polygon_for_wall5(y, width):
    return Polygon([
        BlastOffChallenge.COURSE_LENGTH / 6, y + width / 2,
        BlastOffChallenge.COURSE_LENGTH / 2, y + width / 2,
        BlastOffChallenge.COURSE_LENGTH / 2, y - width / 2,
        BlastOffChallenge.COURSE_LENGTH / 6, y - width / 2,
    ])


class BlastOffChallenge(AbstractChallenge):
    SQRT2 = math.sqrt(2)

    COURSE_LENGTH = 7200  # 7200
    COURSE_WIDTH = 550
    WALL_HEIGHT = 70

    BOTTOM_WALL_ADJUST = (SQRT2 - 1.0) * COURSE_WIDTH

    FLOOR_POLYGONS = [
            polygon_for_wall1(0, COURSE_WIDTH),
            polygon_for_wall2(0, COURSE_WIDTH),
            polygon_for_wall3(0, COURSE_WIDTH),
            polygon_for_wall4(0, COURSE_WIDTH),
            polygon_for_wall5(0, COURSE_WIDTH)
    ]

    LINE_POLYGONS = [
            polygon_for_wall1(0, 19),
            polygon_for_wall2(0, 19),
            polygon_for_wall3(0, 19),
            polygon_for_wall4(0, 19),
            polygon_for_wall5(0, 19)
    ]

    WALL_POLYGONS = [
            polygon_for_wall1(COURSE_WIDTH / 2, 10),
            polygon_for_wall2(COURSE_WIDTH / 2, 10),
            polygon_for_wall3(COURSE_WIDTH / 2, 10),
            polygon_for_wall4(COURSE_WIDTH / 2, 10),
            polygon_for_wall5(COURSE_WIDTH / 2, 10),
            polygon_for_wall1(-COURSE_WIDTH / 2, 10),
            polygon_for_wall2(-COURSE_WIDTH / 2, 10),
            polygon_for_wall3(-COURSE_WIDTH / 2, 10),
            polygon_for_wall4(-COURSE_WIDTH / 2, 10),
            polygon_for_wall5(-COURSE_WIDTH / 2, 10)
    ]

    def __init__(self):
        super(BlastOffChallenge, self).__init__("BlastOff")
        self.camera_id = 0
        self.rover_id = 0

    def after_sim_object_added(self, sim_object):
        super(BlastOffChallenge, self).after_sim_object_added(sim_object)
        if isinstance(sim_object, AbstractRoverSimObject):
            camera_attachment = self._sim_object_factory.obtain(PiWarsSimObjectTypes.CameraAttachment)
            camera_attachment.set_id(self.new_id())
            camera_attachment.attach_to_rover(sim_object)
            self.add_new_sim_object_immediately(camera_attachment)
            self.camera_id = camera_attachment.get_id()

            self.rover_id = sim_object.get_id()
            # reset_rover()
