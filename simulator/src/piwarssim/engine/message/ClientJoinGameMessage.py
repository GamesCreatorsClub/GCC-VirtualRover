from enum import Enum

from piwarssim.engine.message.Message import Message


class ClientJoinGameMessage(Message):
    def __init__(self, factory, message_type):
        super(ClientJoinGameMessage, self).__init__(factory, message_type)
        self._game_id = None

    def free(self):
        super(ClientJoinGameMessage, self).free()

    def get_game_id(self):
        return self._game_id

    def set_game_id(self, game_id):
        self._game_id = game_id

    def size(self):
        return super(ClientJoinGameMessage, self).size() + 1 + len(self._game_id)

    def deserialize_impl(self, deserializer):
        # super(ClientInternalMessage, self).deserialize_impl(deserializer)
        self._game_id = deserializer.deserialize_short_string()

    def serialize_impl(self, serializer):
        # super(ClientInternalMessage, self).serialize_impl(serializer)
        serializer.serialize_short_string(self._game_id)


if __name__ == '__main__':
    # Simple test
    from piwarssim.engine.message.MessageCode import MessageCode
    from piwarssim.engine.transfer.ByteSerializer import ByteSerializer

    serializer = ByteSerializer(None)

    message = ClientJoinGameMessage(None, MessageCode.ClientInternal)
    message_received = ClientJoinGameMessage(None, MessageCode.ClientInternal)
    serializer.setup()
    message.set_game_id("game1")

    message.serialize(serializer)
    message_code = serializer.deserialize_unsigned_byte()
    message_received.deserialize(serializer)

    print(str(MessageCode.from_ordinal(message_code)) + ", game_id "+ str(message_received.get_game_id()))
