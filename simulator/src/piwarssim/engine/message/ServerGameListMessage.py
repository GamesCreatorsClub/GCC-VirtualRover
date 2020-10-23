from enum import Enum

from piwarssim.engine.message.Message import Message


class ServerGameListMessage(Message):
    def __init__(self, factory, message_type):
        super(ServerGameListMessage, self).__init__(factory, message_type)
        self._seq = 0
        self._game_list = []

    def free(self):
        super(ServerGameListMessage, self).free()
        self._seq = 0
        self._game_list = []

    def get_seq(self):
        return self._seq

    def set_seq(self, seq):
        self._seq = seq

    def get_game_list(self):
        return self._game_list

    def size(self):
        return super(ServerGameListMessage, self).size() + 2 + len(self._game_list) + sum([len(s) for s in self._game_list])

    def deserialize_impl(self, deserializer):
        # super(ServerInternalMessage, self).deserialize_impl(deserializer)
        self._seq = deserializer.deserialize_byte()
        size = deserializer.deserialize_unsigned_byte()
        self._game_list = []
        for i in range(size):
            self._game_list.append(deserializer.deserialize_short_string())


    def serialize_impl(self, serializer):
        # super(ServerInternalMessage, self).serialize_impl(serializer)
        serializer.serialize_byte(self._seq)
        serializer.serialize_unsigned_byte(len(self._game_list))
        for s in self._game_list:
            serializer.serialize_short_string(s)
