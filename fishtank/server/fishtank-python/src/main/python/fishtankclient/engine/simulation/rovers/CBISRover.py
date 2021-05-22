
from enum import Enum

from fishtankclient.engine.simulation.rovers.AbstractRoverSimObject import AbstractRoverSimObject
from fishtankclient.engine.simulation.rovers.RoverType import RoverType
from fishtankclient.engine.utils import Polygon


class CBISRover(AbstractRoverSimObject):

    def __init__(self, factory, sim_object_id, sim_object_type):
        super(CBISRover, self).__init__(factory, sim_object_id, sim_object_type, RoverType.CBISRover)
        self.attachment_position = (100.0, 0.0)
        self.camera_position = (100.0, 0.0, 15.0)
        self.camera_orientation = [0.0, 0.0, 0.0, 1.0]
        self.camera_angle = 45.0

    def __repr__(self):
        return "CBISRover[" + super(CBISRover, self).__repr__() + "]"

    def get_shape(self):
        return Polygon.box(200, 150)