from piwarssim.engine.challenges.AbstractChallenge import AbstractChallenge
from piwarssim.engine.simulation.PiWarsSimObjectTypes import PiWarsSimObjectTypes
from piwarssim.engine.simulation.rovers.AbstractRoverSimObject import AbstractRoverSimObject
from piwarssim.engine.utils.Shapes import polygon_from_box


class CanyonsOfMarsChallenge(AbstractChallenge):
    CHALLENGE_WIDTH = 3400
    CHALLENGE_HEIGHT = 1830

    WALL_HEIGHT = 200

    FLOOR_POLYGON = polygon_from_box(-CHALLENGE_WIDTH / 2, -CHALLENGE_HEIGHT / 2, CHALLENGE_WIDTH / 2, CHALLENGE_HEIGHT / 2)

    WALL_POLYGONS = [
        polygon_from_box(1690, -915, 1700, 915),
        polygon_from_box(-1700, 905, 1700, 915),
        polygon_from_box(-1700, -915, -1690, 915),
        polygon_from_box(-1700, -915, 340, -905),

        polygon_from_box(1010, -915, 1020, 315),
        polygon_from_box(340, 305, 1020, 315),
        polygon_from_box(330, -315, 340, 315),
        polygon_from_box(-1020, -315, 340, -305),
        polygon_from_box(-1020, -315, -1010, 315),

        polygon_from_box(-330, 305, -340, 915)
    ]

    START_POLIGON = polygon_from_box(1015, -920, 1700, -915)
    END_POLIGON = polygon_from_box(300, -920, 1015, -915)

    def __init__(self):
        super(CanyonsOfMarsChallenge, self).__init__("CanyonsOfMars")
        self.wall_polygons = CanyonsOfMarsChallenge.WALL_POLYGONS
        self.floor_polygons = [CanyonsOfMarsChallenge.FLOOR_POLYGON]
