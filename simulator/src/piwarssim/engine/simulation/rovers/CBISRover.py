
from enum import Enum

from piwarssim.engine.simulation.AbstractRoverSimObject import AbstractRoverSimObject
from piwarssim.engine.simulation.rovers.RoverType import RoverType


class CBISRover(AbstractRoverSimObject):

    def __init__(self, factory, sim_object_id, sim_object_type):
        super(CBISRover, self).__init__(factory, sim_object_id, sim_object_type, RoverType.GCC)

    def __repr__(self):
        return "CBISRover[" + super(CBISRover, self).__repr__() + "]"
