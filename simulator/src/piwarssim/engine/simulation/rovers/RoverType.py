from enum import Enum


class RoverType(Enum):
    GCC = (0, "GCC Rover")
    CBIS = (1, "CBiS-Education")

    def __new__(cls, type_id, name):
        value = len(cls.__members__)
        obj = object.__new__(cls)
        obj._value_ = value
        return obj

    def __init__(self, type_id, name):
        self._type_id = type_id
        self._name = name

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
