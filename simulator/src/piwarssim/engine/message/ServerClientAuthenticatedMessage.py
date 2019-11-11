from piwarssim.engine.message.Message import Message


class ServerClientAuthenticatedMessage(Message):
    def __init__(self, factory, message_type):
        super(ServerClientAuthenticatedMessage, self).__init__(factory, message_type)
        self._session_id = 0

    def get_session_id(self):
        return self._session_id

    def set_session_id(self, sesssion_id):
        self._session_id = sesssion_id

    def deserialize_impl(self, deserializer):
        super(ServerClientAuthenticatedMessage, self).deserialize_impl(deserializer)
        self._session_id = deserializer.deserialize_int()

    def serialize_impl(self, serializer):
        super(ServerClientAuthenticatedMessage, self).serialize_impl(serializer)
        serializer.serialize_int(self._session_id)
