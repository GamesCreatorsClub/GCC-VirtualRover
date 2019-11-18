from enum import Enum

from piwarssim.engine.message.NopMessage import NopMessage
from piwarssim.engine.message.PlayerDetailsMessage import PlayerDetailsMessage
from piwarssim.engine.message.ServerUpdateMessage import ServerUpdateMessage
from piwarssim.engine.message.MultiObjectUpdateMessage import MultiObjectUpdateMessage
from piwarssim.engine.message.MultiObjectRemovedMessage import MultiObjectRemovedMessage
from piwarssim.engine.message.ClientInternalMessage import ClientInternalMessage
from piwarssim.engine.message.ServerInternalMessage import ServerInternalMessage
from piwarssim.engine.message.ServerClientAuthenticatedMessage import ServerClientAuthenticatedMessage
from piwarssim.engine.message.ClientAuthenticateMessage import ClientAuthenticateMessage
from piwarssim.engine.message.ClientRegisterMessage import ClientRegisterMessage
from piwarssim.engine.message.RemovedMessage import RemovedMessage
from piwarssim.engine.message.ChatMessage import ChatMessage
from piwarssim.engine.message.PlayerInputMessage import PlayerInputMessage


def raise_not_implemented():
    raise NotImplemented


class MessageCode(Enum):
    Nop = (lambda factory: NopMessage(factory, MessageCode.Nop),)
    Player = (lambda factory: PlayerDetailsMessage(factory, MessageCode.Player),)
    ServerUpdate = (lambda factory: ServerUpdateMessage(factory, MessageCode.ServerUpdate),)
    MultiObjectUpdate = (lambda factory: MultiObjectUpdateMessage(factory, MessageCode.MultiObjectUpdate),)
    MultiObjectRemoved = (lambda factory: MultiObjectRemovedMessage(factory, MessageCode.MultiObjectRemoved),)
    ClientInternal = (lambda factory: ClientInternalMessage(factory, MessageCode.ClientInternal),)
    ServerInternal = (lambda factory: ServerInternalMessage(factory, MessageCode.ServerInternal),)
    ServerClientAuthenticated = (lambda factory: ServerClientAuthenticatedMessage(factory, MessageCode.ServerClientAuthenticated),)
    ClientAuthenticate = (lambda factory: ClientAuthenticateMessage(factory, MessageCode.ClientAuthenticate),)
    ClientRegister = (lambda factory: ClientRegisterMessage(factory, MessageCode.ClientRegister),)
    Removed = (lambda factory: RemovedMessage(factory, MessageCode.Removed),)
    PlayerServerUpdate = (lambda factory: ServerUpdateMessage(factory, MessageCode.PlayerServerUpdate),)
    Chat = (lambda factory: ChatMessage(factory, MessageCode.Chat),)
    PlayerInput = (lambda factory: PlayerInputMessage(factory,MessageCode.PlayerInput), )

    def __new__(cls, new_object_function):
        value = len(cls.__members__)
        obj = object.__new__(cls)
        obj._value_ = value
        return obj

    def __init__(self, new_object_function):
        self._new_object_function = new_object_function

    def ordinal(self):
        return self.value

    def new_object(self, factory):
        return self._new_object_function(factory)

    @staticmethod
    def from_ordinal(ordinal):
        for enum_obj in MessageCode:
            if enum_obj.value == ordinal:
                return enum_obj


if __name__ == '__main__':

    for message_type in MessageCode:
        if message_type != MessageCode.PlayerInput:
            message = message_type.new_object(None)
            print(str(message_type) + "'s ordinal " + str(message_type.ordinal()) + " and type: " + str(message.get_type()))
