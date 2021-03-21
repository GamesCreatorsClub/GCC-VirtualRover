from enum import Enum

from fishtankclient.engine.message.Message import Message


class ServerGameDetailsMessage(Message):
    def __init__(self, factory, message_type):
        super(ServerGameDetailsMessage, self).__init__(factory, message_type)
        self._game_id = ""
        self._game_name = ""
        self._map_id = ""
        self._player_id = 0
        self._message = ""

    def free(self):
        super(ServerGameDetailsMessage, self).free()
        self._game_id = ""
        self._game_name = ""
        self._map_id = ""
        self._player_id = 0
        self._message = ""

    def get_game_id(self):
        return self._game_id

    def set_game_id(self, game_id):
        self._game_id = game_id

    def get_game_name(self):
        return self._game_name

    def set_game_name(self, game_name):
        self._game_name = game_name

    def get_map_id(self):
        return self._map_id

    def set_map_id(self, map_id):
        self._map_id = map_id

    def get_player_id(self):
        return self._player_id

    def set_player_id(self, player_id):
        self._player_id = player_id

    def get_message(self):
        return self._message

    def set_message(self, message):
        self._message = message

    def size(self):
        return super(ServerGameDetailsMessage, self).size() + 2\
               + 1 + (len(self._game_id) if self._game_id is not None else 0) \
               + 1 + (len(self._game_name) if self._game_name is not None else 0) \
               + 1 + (len(self._map_id) if self._map_id is not None else 0) \
               + 1 \
               + 1 + (len(self._message) if self._message is not None else 0)

    def deserialize_impl(self, deserializer):
        # super(ServerInternalMessage, self).deserialize_impl(deserializer)
        self._game_id = deserializer.deserialize_short_string()
        self._game_name = deserializer.deserialize_short_string()
        self._map_id = deserializer.deserialize_short_string()
        self._player_id = deserializer.deserialize_unsigned_short()
        self._message = deserializer.deserialize_string()

    def serialize_impl(self, serializer):
        # super(ServerInternalMessage, self).serialize_impl(serializer)
        serializer.serialize_short_string(self._game_id)
        serializer.serialize_short_string(self._game_name)
        serializer.serialize_short_string(self._map_id)
        serializer.serialize_unsigned_short(self._player_id)
        serializer.serialize_string(self._message)
