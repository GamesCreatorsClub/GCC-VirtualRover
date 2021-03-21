import math

from fishtankclient.engine.challenges.AbstractChallenge import AbstractChallenge
from fishtankclient.engine.simulation.PiWarsSimObjectTypes import PiWarsSimObjectTypes
from fishtankclient.engine.simulation.rovers.AbstractRoverSimObject import AbstractRoverSimObject
from fishtankclient.engine.utils.Shapes import Polygon


def polygon_for_wall1(course_length, y, width):
    return Polygon([
        (- course_length / 2, y + width / 2),
        (- course_length / 6, y + width / 2),
        (- course_length / 6, y - width / 2),
        (- course_length / 2, y - width / 2),
    ])


def polygon_for_wall2(course_length, course_width, y, width):
    return Polygon([
        (- course_length / 6, y + width / 2),
        (- course_length / 6 + course_width, y + width / 2 + course_width),
        (- course_length / 6 + course_width, y - width / 2 + course_width),
        (- course_length / 6, y - width / 2),
    ])


def polygon_for_wall3(course_length, course_width, y, width):
    return Polygon([
        (- course_length / 6 + course_width, y + width / 2 + course_width),
        (course_length / 6 - course_width, y + width / 2 + course_width),
        (course_length / 6 - course_width, y - width / 2 + course_width),
        (- course_length / 6 + course_width, y - width / 2 + course_width),
    ])


def polygon_for_wall4(course_length, course_width, y, width):
    return Polygon([
        (course_length / 6 - course_width, y + width / 2 + course_width),
        (course_length / 6, y + width / 2),
        (course_length / 6, y - width / 2),
        (course_length / 6 - course_width, y - width / 2 + course_width),
    ])


def polygon_for_wall5(course_length, y, width):
    return Polygon([
        (course_length / 6, y + width / 2),
        (course_length / 2, y + width / 2),
        (course_length / 2, y - width / 2),
        (course_length / 6, y - width / 2),
    ])


class BlastOffChallenge(AbstractChallenge):
    SQRT2 = math.sqrt(2)

    COURSE_LENGTH = 7200  # 7200
    COURSE_WIDTH = 550
    WALL_HEIGHT = 70

    BOTTOM_WALL_ADJUST = (SQRT2 - 1.0) * COURSE_WIDTH

    FLOOR_POLYGONS = [
            polygon_for_wall1(COURSE_LENGTH, 0, COURSE_WIDTH),
            polygon_for_wall2(COURSE_LENGTH, COURSE_WIDTH, 0, COURSE_WIDTH),
            polygon_for_wall3(COURSE_LENGTH, COURSE_WIDTH, 0, COURSE_WIDTH),
            polygon_for_wall4(COURSE_LENGTH, COURSE_WIDTH, 0, COURSE_WIDTH),
            polygon_for_wall5(COURSE_LENGTH, 0, COURSE_WIDTH)
    ]

    LINE_POLYGONS = [
            polygon_for_wall1(COURSE_LENGTH, 0, 19),
            polygon_for_wall2(COURSE_LENGTH, COURSE_WIDTH, 0, 19),
            polygon_for_wall3(COURSE_LENGTH, COURSE_WIDTH, 0, 19),
            polygon_for_wall4(COURSE_LENGTH, COURSE_WIDTH, 0, 19),
            polygon_for_wall5(COURSE_LENGTH, 0, 19)
    ]

    WALL_POLYGONS = [
            polygon_for_wall1(COURSE_LENGTH, COURSE_WIDTH / 2, 10),
            polygon_for_wall2(COURSE_LENGTH, COURSE_WIDTH, COURSE_WIDTH / 2, 10),
            polygon_for_wall3(COURSE_LENGTH, COURSE_WIDTH, COURSE_WIDTH / 2, 10),
            polygon_for_wall4(COURSE_LENGTH, COURSE_WIDTH, COURSE_WIDTH / 2, 10),
            polygon_for_wall5(COURSE_LENGTH, COURSE_WIDTH / 2, 10),
            polygon_for_wall1(COURSE_LENGTH, -COURSE_WIDTH / 2, 10),
            polygon_for_wall2(COURSE_LENGTH, COURSE_WIDTH, -COURSE_WIDTH / 2, 10),
            polygon_for_wall3(COURSE_LENGTH, COURSE_WIDTH, -COURSE_WIDTH / 2, 10),
            polygon_for_wall4(COURSE_LENGTH, COURSE_WIDTH, -COURSE_WIDTH / 2, 10),
            polygon_for_wall5(COURSE_LENGTH, -COURSE_WIDTH / 2, 10)
    ]

    def __init__(self):
        super(BlastOffChallenge, self).__init__("BlastOff")
        self.wall_polygons = BlastOffChallenge.WALL_POLYGONS
        self.floor_polygons = BlastOffChallenge.FLOOR_POLYGONS
