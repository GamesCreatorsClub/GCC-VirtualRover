from enum import Enum

from piwarssim.engine.simulation.GameMessageSimObject import GameMessageSimObject
from piwarssim.engine.simulation.MineSweeperStateObject import MineSweeperStateObject
from piwarssim.engine.simulation.objects.BarrelSimObject import BarrelSimObject
from piwarssim.engine.simulation.attachments.PiNoonAttachemntObject import PiNoonAttachmentObject
from piwarssim.engine.simulation.attachments.CameraAttachemntObject import CameraAttachmentObject
from piwarssim.engine.simulation.rovers.GCCRover import GCCRover
from piwarssim.engine.simulation.rovers.CBISRover import CBISRover


class PiWarsSimObjectTypes(Enum):
    GameMessageObject = (lambda factory, sim_object_id, sim_object_type: GameMessageSimObject(factory, sim_object_id, sim_object_type),)

    BarrelObject = (lambda factory, sim_object_id, sim_object_type: BarrelSimObject(factory, sim_object_id, sim_object_type),)

    MineSweeperStateObject = (lambda factory, sim_object_id, sim_object_type: MineSweeperStateObject(factory, sim_object_id, sim_object_type),)

    PiNoonAttachment = (lambda factory, sim_object_id, sim_object_type: PiNoonAttachmentObject(factory, sim_object_id, sim_object_type),)

    CameraAttachment = (lambda factory, sim_object_id, sim_object_type: CameraAttachmentObject(factory, sim_object_id, sim_object_type),)

    GCCRover = (lambda factory, sim_object_id, sim_object_type: GCCRover(factory, sim_object_id, sim_object_type),)

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
