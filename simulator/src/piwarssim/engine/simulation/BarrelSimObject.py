from enum import Enum

from piwarssim.engine.simulation.MovingSimulationObjectWithPositionAndOrientation import MovingSimulationObjectWithPositionAndOrientation
from piwarssim.engine.simulation.rovers.RoverType import RoverType


class BarrelSimObject(MovingSimulationObjectWithPositionAndOrientation):
    def __init__(self, factory, sim_object_id, sim_object_type):
        super(BarrelSimObject, self).__init__(factory, sim_object_id, sim_object_type)

    def free(self):
        super(BarrelSimObject, self).free()

    def serialize(self, full, serializer):
        super(BarrelSimObject, self).serialize(full, serializer)

    def deserialize(self, full, serializer):
        super(BarrelSimObject, self).deserialize(full, serializer)

    def size(self, full):
        return super(BarrelSimObject, self).size(full)

    def copy_internal(self, new_object):
        super(BarrelSimObject, self).copy_internal(new_object)

        return new_object

    def __repr__(self):
        return "Barrel[" + super(BarrelSimObject, self).__repr__() + "]"
