from fishtankclient.engine.challenges.AbstractChallenge import AbstractChallenge
from fishtankclient.engine.simulation.PiWarsSimObjectTypes import PiWarsSimObjectTypes
from fishtankclient.engine.simulation.rovers.AbstractRoverSimObject import AbstractRoverSimObject
from fishtankclient.engine.utils.Shapes import Polygon, polygon_from_box


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
        self.wall_polygons = StraightLineSpeedTestChallenge.WALL_POLYGONS + StraightLineSpeedTestChallenge.CHICANES_POLYGONS
        self.floor_polygons = [StraightLineSpeedTestChallenge.FLOOR_POLYGON]
