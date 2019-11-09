from enum import Enum

from piwarssim.engine.simulation.rovers.GCCRoverDefinition import GCCRoverDefinition


class RoverType(Enum):
    GCC = (0, "GCC Rover", GCCRoverDefinition())
    CBIS = (1, "CBiS-Education", GCCRoverDefinition())

    def __new__(cls, type_id, name, definition):
        value = len(cls.__members__)
        obj = object.__new__(cls)
        obj._value_ = value
        return obj

    def __init__(self, type_id, name, definition):
        self._type_id = type_id
        self._name = name
        self._definition = definition

    def ordinal(self):
        return self.value

    @staticmethod
    def from_ordinal(ordinal):
        for enum_obj in RoverType:
            if enum_obj.value == ordinal:
                return enum_obj

    def get_id(self):
        return self._type_id

    def get_name(self):
        return self._name

    def get_definition(self):
        return self._definition


if __name__ == '__main__':

    for rover in RoverType:
        print(rover.get_name() + "(" + str(rover.get_id()) + ") has definition " + str(rover.get_definition()))
