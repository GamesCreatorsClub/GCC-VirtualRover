from enum import Enum

from piwarssim.engine.simulation.MovingSimulationObjectWithPositionAndOrientation import MovingSimulationObjectWithPositionAndOrientation


class BarrelColour(Enum):
    Green = ()
    Red = ()

    def __new__(cls):
        value = len(cls.__members__)
        obj = object.__new__(cls)
        obj._value_ = value
        return obj

    def ordinal(self):
        return self.value

    @staticmethod
    def from_ordinal(ordinal):
        for enum_obj in BarrelColour:
            if enum_obj.value == ordinal:
                return enum_obj


class BarrelSimObject(MovingSimulationObjectWithPositionAndOrientation):

    def __init__(self, factory, sim_object_id, sim_object_type):
        super(BarrelSimObject, self).__init__(factory, sim_object_id, sim_object_type)
        self._barrel_colour = BarrelColour.Green

    def free(self):
        super(BarrelSimObject, self).free()

    def get_barrel_colour(self):
        return self._barrel_colour

    def set_barrel_colour(self, barrel_colour):
        self.changed = self.changed or self._barrel_colour != barrel_colour
        self._barrel_colour = barrel_colour

    def serialize(self, full, serializer):
        super(BarrelSimObject, self).serialize(full, serializer)
        if full:
            serializer.serialize_unsigned_byte(self._barrel_colour.ordinal())

    def deserialize(self, full, serializer):
        super(BarrelSimObject, self).deserialize(full, serializer)
        if full:
            barrel_colour_ordinal = serializer.deserialize_unsigned_byte()
            barrel_colour = BarrelColour.from_ordinal(barrel_colour_ordinal)
            if barrel_colour is None:
                barrel_colour = BarrelColour.Green

            self._barrel_colour = barrel_colour

    def size(self, full):
        return super(BarrelSimObject, self).size(full) + (1 if full else 0)

    def copy_internal(self, new_object):
        super(BarrelSimObject, self).copy_internal(new_object)
        new_object._barrel_colour = self._barrel_colour

        return new_object

    def __repr__(self):
        return "Barrel[" + super(BarrelSimObject, self).__repr__() + "]"
