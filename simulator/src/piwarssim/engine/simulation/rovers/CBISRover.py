
from enum import Enum

from piwarssim.engine.simulation.AbstractRoverSimObject import AbstractRoverSimObject
from piwarssim.engine.simulation.rovers.RoverType import RoverType


class GCCRover(AbstractRoverSimObject):

    def __init__(self, factory, sim_object_id, sim_object_type):
        super(GCCRover, self).__init__(factory, sim_object_id, RoverType.GCC)

    def __repr__(self):
        return "GCCRoverSimObject[" + super(GCCRover, self).__repr__() + "]"
