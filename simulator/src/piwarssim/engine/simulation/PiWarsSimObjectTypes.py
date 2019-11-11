from enum import Enum

from piwarssim.engine.simulation.RoverSimObject import RoverSimObject


class PiWarsSimObjectTypes(Enum):
    Rover = (lambda factory, sim_object_id, sim_object_type: RoverSimObject(factory, sim_object_id, sim_object_type),)

    def __new__(cls, new_object_function):
        value = len(cls.__members__)
        obj = object.__new__(cls)
        obj._value_ = value
        return obj

    def __init__(self, new_object_function):
        self._new_object_function = new_object_function

    def ordinal(self):
        return self.value

    def new_object(self, factory, sim_object_id):
        return self._new_object_function(factory, sim_object_id, self)

    @staticmethod
    def from_ordinal(ordinal):
        for enum_obj in PiWarsSimObjectTypes:
            if enum_obj.value == ordinal:
                return enum_obj
