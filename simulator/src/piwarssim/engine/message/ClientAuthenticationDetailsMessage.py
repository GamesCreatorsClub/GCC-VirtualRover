from piwarssim.engine.message.Message import Message


class ClientAuthenticationDetailsMessage(Message):
    def __init__(self, factory, message_type):
        super(ClientAuthenticationDetailsMessage, self).__init__(factory, message_type)
        self._username = None
        self._passhash = None

    def get_username(self):
        return self._username

    def get_passhash(self):
        return self._passhash

    def set_values(self, username, passhash):
        self._username = username
        self._passhash = passhash

    def deserialize_impl(self, deserializer):
        # super(ClientAuthenticationDetailsMessage, self).deserialize_impl(deserializer)
        self._username = deserializer.deserialize_string()
        self._passhash = deserializer.deserialize_string()

    def serialize_impl(self, serializer):
        # super(ClientAuthenticationDetailsMessage, self).serialize_impl(serializer)
        serializer.serialize_string(self._username)
        serializer.serialize_string(self._passhash)
