
from enum import Enum

from fishtankclient.engine.simulation.rovers.AbstractRoverSimObject import AbstractRoverSimObject
from fishtankclient.engine.simulation.rovers.RoverType import RoverType
from fishtankclient.engine.utils import Polygon


class GCCRoverM18(AbstractRoverSimObject):

    def __init__(self, factory, sim_object_id, sim_object_type):
        super(GCCRoverM18, self).__init__(factory, sim_object_id, sim_object_type, RoverType.GCCRoverM18)
        self.attachment_position = (80.0, 0.0)
        self.camera_position = (75.0, 0.0, 20.0)
        self.camera_orientation = AbstractRoverSimObject.from_yaw_pitch_roll(10.0, 0.0, 0.0) # positive yaw is down
        self.camera_angle = 45.0

    def __repr__(self):
        return "GCCRover[" + super(GCCRoverM18, self).__repr__() + "]"

    def get_shape(self):
        return Polygon.box(200, 200)
