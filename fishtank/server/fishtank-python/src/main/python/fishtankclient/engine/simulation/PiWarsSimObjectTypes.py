from enum import Enum

from fishtankclient.engine.simulation.objects.CameraPositionObject import CameraPositionObject
from fishtankclient.engine.simulation.objects.SpadefishFish import SpadefishFish
from fishtankclient.engine.simulation.objects.TetraFish import TetraFish
from fishtankclient.engine.simulation.rovers.WaitingPlayerSimObject import WaitingPlayerSimObject


class PiWarsSimObjectTypes(Enum):
    WaitingPlayerObject = (False, lambda factory, sim_object_id, sim_object_type: WaitingPlayerSimObject(factory, sim_object_id, sim_object_type),)

    CameraPositionObject = (False, lambda factory, sim_object_id, sim_object_type: CameraPositionObject(factory, sim_object_id, sim_object_type),)

    SpadefishObject = (False, lambda factory, sim_object_id, sim_object_type: SpadefishFish(factory, sim_object_id, sim_object_type),)

    TetrafishObject = (False, lambda factory, sim_object_id, sim_object_type: TetraFish(factory, sim_object_id, sim_object_type),)

    def __new__(cls, is_rover, new_object_function):
        value = len(cls.__members__)
        obj = object.__new__(cls)
        obj._value_ = value
        return obj

    def __init__(self, is_rover, new_object_function):
        self._is_rover = is_rover
        self._new_object_function = new_object_function

    def ordinal(self):
        return self.value

    def new_object(self, factory, sim_object_id):
        return self._new_object_function(factory, sim_object_id, self)

    def is_rover(self):
        return self._is_rover

    @staticmethod
    def from_ordinal(ordinal):
        for enum_obj in PiWarsSimObjectTypes:
            if enum_obj.value == ordinal:
                return enum_obj

    @staticmethod
    def from_name(name):
        for enum_obj in PiWarsSimObjectTypes:
            if enum_obj.name == name:
                return enum_obj


if __name__ == '__main__':
    rover = PiWarsSimObjectTypes.from_name("GCCRoverM18")
    print(f"GCCRoverM18={rover}")