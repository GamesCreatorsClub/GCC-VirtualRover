
from enum import Enum

from piwarssim.engine.simulation.rovers.AbstractRoverSimObject import AbstractRoverSimObject
from piwarssim.engine.simulation.rovers.RoverType import RoverType


class GCCRoverM16(AbstractRoverSimObject):

    def __init__(self, factory, sim_object_id, sim_object_type):
        super(GCCRoverM16, self).__init__(factory, sim_object_id, sim_object_type, RoverType.GCCRoverM16)
        self.attachment_position = (80.0, 0.0)
        self.camera_position = (75.0, 0.0, 20.0)
        self.camera_orientation = AbstractRoverSimObject.from_yaw_pitch_roll(10.0, 0.0, 0.0) # positive yaw is down
        self.camera_angle = 45.0

    def __repr__(self):
        return "GCCRover[" + super(GCCRoverM16, self).__repr__() + "]"
