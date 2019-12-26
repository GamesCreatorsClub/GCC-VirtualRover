from enum import Enum

from piwarssim.engine.simulation.MovingSimulationObjectWithPositionAndOrientation import MovingSimulationObjectWithPositionAndOrientation
from piwarssim.engine.simulation.rovers.RoverType import RoverType


class RoverColor(Enum):
    White = ()
    Green = ()
    Blue = ()

    def __new__(cls):
        value = len(cls.__members__)
        obj = object.__new__(cls)
        obj._value_ = value
        return obj

    def ordinal(self):
        return self.value

    @staticmethod
    def from_ordinal(ordinal):
        for enum_obj in RoverColor:
            if enum_obj.value == ordinal:
                return enum_obj


class AbstractRoverSimObject(MovingSimulationObjectWithPositionAndOrientation):
    def __init__(self, factory, sim_object_id, sim_object_type, rover_type):
        super(AbstractRoverSimObject, self).__init__(factory, sim_object_id, sim_object_type)
        self._rover_type = rover_type
        self._rover_name = rover_type.get_name()
        self._rover_colour = RoverColor.White

    def free(self):
        super(AbstractRoverSimObject, self).free()

    def get_rover_colour(self):
        return self._rover_colour

    def set_rover_colour(self, rover_colour):
        self._rover_colour = rover_colour

    def serialize(self, full, serializer):
        super(AbstractRoverSimObject, self).serialize(full, serializer)

        if full:
            serializer.serialize_short_string(self._rover_name)

        serializer.serialize_unsigned_byte(self._rover_colour.value)

    def deserialize(self, full, serializer):
        super(AbstractRoverSimObject, self).deserialize(full, serializer)

        if full:
            self._rover_name = serializer.deserialize_short_string()

        colour_value = serializer.deserialize_unsigned_byte()
        self._rover_colour = RoverColor.from_ordinal(colour_value)

    def size(self, full):
        return super(AbstractRoverSimObject, self).size(full) + (1 + len(self._rover_name) if full else 0) + 1

    def copy_internal(self, new_object):
        super(AbstractRoverSimObject, self).copy_internal(new_object)

        new_object._rover_name = self._rover_name
        new_object._rover_colour = self._rover_colour

        return new_object

    def __repr__(self):
        return self._rover_name + ", " + str(self._rover_colour) + ", " + super(AbstractRoverSimObject, self).__repr__()
