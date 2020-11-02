from enum import Enum

from piwarssim.engine.simulation.GameMessageSimObject import GameMessageSimObject
from piwarssim.engine.simulation.MineSweeperStateObject import MineSweeperStateObject
from piwarssim.engine.simulation.objects.BarrelSimObject import BarrelSimObject
from piwarssim.engine.simulation.attachments.PiNoonAttachemntObject import PiNoonAttachmentObject
from piwarssim.engine.simulation.attachments.CameraAttachemntObject import CameraAttachmentObject
from piwarssim.engine.simulation.objects.FishTowerSimObject import FishTowerSimObject
from piwarssim.engine.simulation.objects.GolfBallSimObject import GolfBallSimObject
from piwarssim.engine.simulation.objects.ToyCubeSimObject import ToyCubeSimObject
from piwarssim.engine.simulation.rovers import MacFeegle
from piwarssim.engine.simulation.rovers.GCCRoverM16 import GCCRoverM16
from piwarssim.engine.simulation.rovers.GCCRoverM18 import GCCRoverM18
from piwarssim.engine.simulation.rovers.CBISRover import CBISRover
from piwarssim.engine.simulation.rovers.WaitingPlayerSimObject import WaitingPlayerSimObject


class PiWarsSimObjectTypes(Enum):
    WaitingPlayerObject = (False, lambda factory, sim_object_id, sim_object_type: WaitingPlayerSimObject(factory, sim_object_id, sim_object_type),)

    GameMessageObject = (False, lambda factory, sim_object_id, sim_object_type: GameMessageSimObject(factory, sim_object_id, sim_object_type),)

    BarrelObject = (False, lambda factory, sim_object_id, sim_object_type: BarrelSimObject(factory, sim_object_id, sim_object_type),)

    ToyCubeObject = (False, lambda factory, sim_object_id, sim_object_type: ToyCubeSimObject(factory, sim_object_id, sim_object_type),)

    GolfBallObject = (False, lambda factory, sim_object_id, sim_object_type: GolfBallSimObject(factory, sim_object_id, sim_object_type),)

    FishTowerObject = (False, lambda factory, sim_object_id, sim_object_type: FishTowerSimObject(factory, sim_object_id, sim_object_type),)

    MineSweeperStateObject = (False, lambda factory, sim_object_id, sim_object_type: MineSweeperStateObject(factory, sim_object_id, sim_object_type),)

    PiNoonAttachment = (False, lambda factory, sim_object_id, sim_object_type: PiNoonAttachmentObject(factory, sim_object_id, sim_object_type),)

    CameraAttachment = (False, lambda factory, sim_object_id, sim_object_type: CameraAttachmentObject(factory, sim_object_id, sim_object_type),)

    GCCRoverM16 = (True, lambda factory, sim_object_id, sim_object_type: GCCRoverM16(factory, sim_object_id, sim_object_type),)

    GCCRoverM18 = (True, lambda factory, sim_object_id, sim_object_type: GCCRoverM18(factory, sim_object_id, sim_object_type),)

    CBISRover = (True, lambda factory, sim_object_id, sim_object_type: CBISRover(factory, sim_object_id, sim_object_type),)

    MacFeegle = (True, lambda factory, sim_object_id, sim_object_type: MacFeegle(factory, sim_object_id, sim_object_type),)

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