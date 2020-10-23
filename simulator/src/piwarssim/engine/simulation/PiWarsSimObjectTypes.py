from enum import Enum

from piwarssim.engine.simulation.GameMessageSimObject import GameMessageSimObject
from piwarssim.engine.simulation.MineSweeperStateObject import MineSweeperStateObject
from piwarssim.engine.simulation.objects.BarrelSimObject import BarrelSimObject
from piwarssim.engine.simulation.attachments.PiNoonAttachemntObject import PiNoonAttachmentObject
from piwarssim.engine.simulation.attachments.CameraAttachemntObject import CameraAttachmentObject
from piwarssim.engine.simulation.objects.FishTowerSimObject import FishTowerSimObject
from piwarssim.engine.simulation.objects.GolfBallSimObject import GolfBallSimObject
from piwarssim.engine.simulation.objects.ToyCubeSimObject import ToyCubeSimObject
from piwarssim.engine.simulation.rovers.GCCRoverM16 import GCCRoverM16
from piwarssim.engine.simulation.rovers.GCCRoverM18 import GCCRoverM18
from piwarssim.engine.simulation.rovers.CBISRover import CBISRover
from piwarssim.engine.simulation.rovers.WaitingPlayerSimObject import WaitingPlayerSimObject


class PiWarsSimObjectTypes(Enum):
    WaitingPlayerObject = (lambda factory, sim_object_id, sim_object_type: WaitingPlayerSimObject(factory, sim_object_id, sim_object_type),)

    GameMessageObject = (lambda factory, sim_object_id, sim_object_type: GameMessageSimObject(factory, sim_object_id, sim_object_type),)

    BarrelObject = (lambda factory, sim_object_id, sim_object_type: BarrelSimObject(factory, sim_object_id, sim_object_type),)

    ToyCubeObject = (lambda factory, sim_object_id, sim_object_type: ToyCubeSimObject(factory, sim_object_id, sim_object_type),)

    GolfBallObject = (lambda factory, sim_object_id, sim_object_type: GolfBallSimObject(factory, sim_object_id, sim_object_type),)

    FishTowerObject = (lambda factory, sim_object_id, sim_object_type: FishTowerSimObject(factory, sim_object_id, sim_object_type),)

    MineSweeperStateObject = (lambda factory, sim_object_id, sim_object_type: MineSweeperStateObject(factory, sim_object_id, sim_object_type),)

    PiNoonAttachment = (lambda factory, sim_object_id, sim_object_type: PiNoonAttachmentObject(factory, sim_object_id, sim_object_type),)

    CameraAttachment = (lambda factory, sim_object_id, sim_object_type: CameraAttachmentObject(factory, sim_object_id, sim_object_type),)

    GCCRoverM16 = (lambda factory, sim_object_id, sim_object_type: GCCRoverM16(factory, sim_object_id, sim_object_type),)

    GCCRoverM18 = (lambda factory, sim_object_id, sim_object_type: GCCRoverM18(factory, sim_object_id, sim_object_type),)

    CBISRover = (lambda factory, sim_object_id, sim_object_type: CBISRover(factory, sim_object_id, sim_object_type),)

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
