from enum import Enum

from piwarssim.engine.simulation.MovingSimulationObjectWithPositionAndOrientation import MovingSimulationObjectWithPositionAndOrientation
from piwarssim.engine.utils.Shapes import Polygon


class ToyCubeColour(Enum):
    Green = ()
    Red = ()
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
        for enum_obj in ToyCubeColour:
            if enum_obj.value == ordinal:
                return enum_obj


class ToyCubeSimObject(MovingSimulationObjectWithPositionAndOrientation):

    def __init__(self, factory, sim_object_id, sim_object_type):
        super(ToyCubeSimObject, self).__init__(factory, sim_object_id, sim_object_type)
        self._cube_colour = ToyCubeColour.Green

    def free(self):
        super(ToyCubeSimObject, self).free()

    def get_cube_colour(self):
        return self._cube_colour

    def set_cube_colour(self, cubel_colour):
        self.changed = self.changed or self._cube_colour != cubel_colour
        self._cube_colour = cubel_colour

    def serialize(self, full, serializer):
        super(ToyCubeSimObject, self).serialize(full, serializer)
        if full:
            serializer.serialize_unsigned_byte(self._cube_colour.ordinal())

    def deserialize(self, full, serializer):
        super(ToyCubeSimObject, self).deserialize(full, serializer)
        if full:
            cube_colour_ordinal = serializer.deserialize_unsigned_byte()
            cube_colour = ToyCubeColour.from_ordinal(cube_colour_ordinal)
            if cube_colour is None:
                cube_colour = ToyCubeColour.Green

            self._cube_colour = cube_colour

    def size(self, full):
        return super(ToyCubeSimObject, self).size(full) + (1 if full else 0)

    def copy_internal(self, new_object):
        super(ToyCubeSimObject, self).copy_internal(new_object)
        new_object._cube_colour = self._cube_colour

        return new_object

    def __repr__(self):
        return "ToyCube[" + super(ToyCubeSimObject, self).__repr__() + "]"

    def get_shape(self):
        return Polygon.box(50)
