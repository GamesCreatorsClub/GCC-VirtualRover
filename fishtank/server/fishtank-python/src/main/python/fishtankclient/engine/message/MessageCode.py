from enum import Enum

from fishtankclient.engine.message.ClientJoinGameMessage import ClientJoinGameMessage
from fishtankclient.engine.message.NopMessage import NopMessage
from fishtankclient.engine.message.ErrorContainerMessage import ErrorContainerMessage
from fishtankclient.engine.message.MultiObjectUpdateMessage import MultiObjectUpdateMessage
from fishtankclient.engine.message.MultiObjectRemovedMessage import MultiObjectRemovedMessage
from fishtankclient.engine.message.MultiObjectRequestForFullUpdateMessage import MultiObjectRequestForFullUpdateMessage
from fishtankclient.engine.message.ClientInternalMessage import ClientInternalMessage
from fishtankclient.engine.message.ServerGameDetailsMessage import ServerGameDetailsMessage
from fishtankclient.engine.message.ServerGameListMessage import ServerGameListMessage
from fishtankclient.engine.message.ServerInternalMessage import ServerInternalMessage
from fishtankclient.engine.message.ServerClientAuthenticatedMessage import ServerClientAuthenticatedMessage
from fishtankclient.engine.message.ClientAuthenticateMessage import ClientAuthenticateMessage
from fishtankclient.engine.message.ClientRegisterMessage import ClientRegisterMessage
from fishtankclient.engine.message.ChatMessage import ChatMessage
from fishtankclient.engine.message.PlayerInputMessage import PlayerInputMessage
from fishtankclient.engine.message.ServerRequestScreenshotMessage import ServerRequestScreenshotMessage
from fishtankclient.engine.message.ClientScreenshotMessage import ClientScreenshotMessage


def raise_not_implemented():
    raise NotImplemented


class MessageCode(Enum):
    Nop = (lambda factory: NopMessage(factory, MessageCode.Nop),)
    ErrorContainerMessage = (lambda factory: ErrorContainerMessage(factory, MessageCode.ErrorContainerMessage),)
    MultiObjectUpdate = (lambda factory: MultiObjectUpdateMessage(factory, MessageCode.MultiObjectUpdate),)
    MultiObjectRemoved = (lambda factory: MultiObjectRemovedMessage(factory, MessageCode.MultiObjectRemoved),)
    MultiObjectRequestForFullUpdate = (lambda factory: MultiObjectRequestForFullUpdateMessage(factory, MessageCode.MultiObjectRequestForFullUpdate),)
    ServerInternal = (lambda factory: ServerInternalMessage(factory, MessageCode.ServerInternal),)
    ClientInternal = (lambda factory: ClientInternalMessage(factory, MessageCode.ClientInternal),)
    ClientJoinGame = (lambda factory: ClientJoinGameMessage(factory, MessageCode.ClientJoinGame),)
    ServerClientAuthenticated = (lambda factory: ServerClientAuthenticatedMessage(factory, MessageCode.ServerClientAuthenticated),)
    ServerGameDetails = (lambda factory: ServerGameDetailsMessage(factory, MessageCode.ServerGameDetails),)
    ServerGameList = (lambda factory: ServerGameListMessage(factory, MessageCode.ServerGameList),)
    ClientAuthenticate = (lambda factory: ClientAuthenticateMessage(factory, MessageCode.ClientAuthenticate),)
    ClientRegister = (lambda factory: ClientRegisterMessage(factory, MessageCode.ClientRegister),)
    PlayerInput = (lambda factory: PlayerInputMessage(factory,MessageCode.PlayerInput), )
    Chat = (lambda factory: ChatMessage(factory, MessageCode.Chat),)
    ServerRequestScreenshot = (lambda factory: ServerRequestScreenshotMessage(factory, MessageCode.ServerRequestScreenshot),)
    ClientScreenshot = (lambda factory: ClientScreenshotMessage(factory, MessageCode.Chat),)

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
