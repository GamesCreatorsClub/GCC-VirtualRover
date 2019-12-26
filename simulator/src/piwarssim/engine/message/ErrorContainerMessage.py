from piwarssim.engine.message.Message import Message


class ChatMessage(Message):
    def __init__(self, factory, message_type):
        super(ChatMessage, self).__init__(factory, message_type)
        self._line = None
        self._origin = None

    def free(self):
        super(Message, self).free()
        self._line = None
        self._origin = None

    def get_line(self):
        return self._line

    def set_line(self, line):
        self._line = line

    def get_origin(self):
        return self._origin

    def set_origin(self, origin):
        self._origin = origin

    def size(self):
        return super(ChatMessage, self).size() + 2 + (len(self._line) if self._line is not None  else 0) + 2 + (len(self._origin) if self._origin is not None else 0)

    def deserialize_impl(self, deserializer):
        # super(ChatMessage, self).deserialize_impl(deserializer)
        self._line = deserializer.deserialize_string()
        self._origin = deserializer.deserialize_string()

    def serialize_impl(self, serializer):
        # super(ChatMessage, self).serialize_impl(serializer)
        serializer.serialize_string(self._line)
        if self._origin is not None:
            serializer.serialize_string(self._origin)
        else:
            serializer.serializeString("")
