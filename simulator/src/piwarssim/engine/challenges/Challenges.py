from enum import Enum

from piwarssim.engine.challenges import PiNoonChallenge
from piwarssim.engine.challenges.EcoDisasterChallenge import EcoDisasterChallenge
from piwarssim.engine.message.NopMessage import NopMessage
from piwarssim.engine.message.ErrorContainerMessage import ErrorContainerMessage
from piwarssim.engine.message.MultiObjectUpdateMessage import MultiObjectUpdateMessage
from piwarssim.engine.message.MultiObjectRemovedMessage import MultiObjectRemovedMessage
from piwarssim.engine.message.MultiObjectRequestForFullUpdateMessage import MultiObjectRequestForFullUpdateMessage
from piwarssim.engine.message.ClientInternalMessage import ClientInternalMessage
from piwarssim.engine.message.ServerInternalMessage import ServerInternalMessage
from piwarssim.engine.message.ServerClientAuthenticatedMessage import ServerClientAuthenticatedMessage
from piwarssim.engine.message.ClientAuthenticateMessage import ClientAuthenticateMessage
from piwarssim.engine.message.ClientRegisterMessage import ClientRegisterMessage
from piwarssim.engine.message.ChatMessage import ChatMessage
from piwarssim.engine.message.PlayerInputMessage import PlayerInputMessage
from piwarssim.engine.message.ServerRequestScreenshotMessage import ServerRequestScreenshotMessage
from piwarssim.engine.message.ClientScreenshotMessage import ClientScreenshotMessage


def raise_not_implemented():
    raise NotImplemented


class Challenges(Enum):
    PiNoon = (lambda : PiNoonChallenge(),)
    EcoDisaster = (lambda : EcoDisasterChallenge(),)

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
