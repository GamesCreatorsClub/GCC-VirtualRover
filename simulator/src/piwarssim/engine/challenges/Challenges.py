from enum import Enum

from piwarssim.engine.challenges.PiNoonChallenge import PiNoonChallenge
from piwarssim.engine.challenges.BlastOffChallenge import BlastOffChallenge
from piwarssim.engine.challenges.CanyonsOfMarsChallenge import CanyonsOfMarsChallenge
from piwarssim.engine.challenges.EcoDisasterChallenge import EcoDisasterChallenge
from piwarssim.engine.challenges.MineSweeperChallenge import MineSweeperChallenge
from piwarssim.engine.challenges.StraightLineSpeedTestChallenge import StraightLineSpeedTestChallenge


def raise_not_implemented():
    raise NotImplemented


class Challenges(Enum):
    PiNoon = (lambda : PiNoonChallenge(),)
    EcoDisaster = (lambda : EcoDisasterChallenge(),)
    CanyonsOfMars = (lambda : CanyonsOfMarsChallenge(),)
    StraightLineSpeedTest = (lambda : StraightLineSpeedTestChallenge(),)
    BlastOff = (lambda : BlastOffChallenge(),)
    MineSweeper = (lambda : MineSweeperChallenge(),)

    def __new__(cls, new_object_function):
        value = len(cls.__members__)
        obj = object.__new__(cls)
        obj._value_ = value
        return obj

    def __init__(self, new_object_function):
        self._new_object_function = new_object_function

    def ordinal(self):
        return self.value

    def new_object(self):
        return self._new_object_function()

    @staticmethod
    def from_ordinal(ordinal):
        for enum_obj in Challenges:
            if enum_obj.value == ordinal:
                return enum_obj

    @staticmethod
    def from_name(name):
        for enum_obj in Challenges:
            if enum_obj.name == name:
                return enum_obj
