from enum import Enum

from piwarssim.engine.simulation.MovingSimulationObjectWithPositionAndOrientation import MovingSimulationObjectWithPositionAndOrientation
from piwarssim.engine.utils.Shapes import Circle


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
        self.circle = Circle(0, 0, 25)

    def free(self):
        super(ToyCubeSimObject, self).free()

    def get_cube_colour(self):
        return self._cube_colour

    def set_cube_colour(self, barrel_colour):
        self.changed = self.changed or self._cube_colour != barrel_colour
        self._cube_colour = barrel_colour

    def get_circle(self):
        position = self.get_position()
        self.circle.x = position[0]
        self.circle.y = position[1]
        return self.circle

    # def set_position_v(self, position):
    #     super(BarrelSimObject, self).set_position_v(position)
    #
    # def set_position_2(self, x, y):
    #     super(BarrelSimObject, self).set_position_2(x, y)
    #
    def serialize(self, full, serializer):
        super(ToyCubeSimObject, self).serialize(full, serializer)
        if full:
            serializer.serialize_unsigned_byte(self._cube_colour.ordinal())

    def deserialize(self, full, serializer):
        super(ToyCubeSimObject, self).deserialize(full, serializer)
        if full:
            barrel_colour_ordinal = serializer.deserialize_unsigned_byte()
            barrel_colour = ToyCubeColour.from_ordinal(barrel_colour_ordinal)
            if barrel_colour is None:
                barrel_colour = ToyCubeColour.Green

            self._cube_colour = barrel_colour

    def size(self, full):
        return super(ToyCubeSimObject, self).size(full) + (1 if full else 0)

    def copy_internal(self, new_object):
        super(ToyCubeSimObject, self).copy_internal(new_object)
        new_object._barrel_colour = self._cube_colour

        return new_object

    def __repr__(self):
        return "ToyCube[" + super(ToyCubeSimObject, self).__repr__() + "]"
