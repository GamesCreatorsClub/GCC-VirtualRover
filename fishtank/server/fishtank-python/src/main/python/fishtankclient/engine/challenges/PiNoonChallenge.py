from fishtankclient.engine.challenges.AbstractChallenge import AbstractChallenge
from fishtankclient.engine.utils.Shapes import polygon_from_box


class PiNoonChallenge(AbstractChallenge):
    CHALLENGE_WIDTH = 2000

    WALL_POLYGONS = [
            polygon_from_box(-CHALLENGE_WIDTH / 2, -CHALLENGE_WIDTH / 2 - 1,  CHALLENGE_WIDTH / 2, -CHALLENGE_WIDTH / 2),
            polygon_from_box(-CHALLENGE_WIDTH / 2 - 1, -CHALLENGE_WIDTH / 2, -CHALLENGE_WIDTH / 2,  CHALLENGE_WIDTH / 2),
            polygon_from_box(-CHALLENGE_WIDTH / 2,  CHALLENGE_WIDTH / 2,  CHALLENGE_WIDTH / 2,  CHALLENGE_WIDTH / 2 + 1),
            polygon_from_box( CHALLENGE_WIDTH / 2, -CHALLENGE_WIDTH / 2,  CHALLENGE_WIDTH / 2 + 1,  CHALLENGE_WIDTH / 2)
    ]

    FLOOR_POLYGON = polygon_from_box(-CHALLENGE_WIDTH / 2, -CHALLENGE_WIDTH / 2,  CHALLENGE_WIDTH / 2, CHALLENGE_WIDTH / 2)

    def __init__(self):
        super(PiNoonChallenge, self).__init__("PiNoon")
        self.wall_polygons = PiNoonChallenge.WALL_POLYGONS
        self.floor_polygons = [PiNoonChallenge.FLOOR_POLYGON]
