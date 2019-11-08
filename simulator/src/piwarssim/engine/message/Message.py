import time

from piwarssim.engine.factory import TypedObject


class Message(TypedObject):
    def __init__(self, factory, message_type):
        super(Message, self).__init__(factory)
        self._message_type = message_type
        self._time = 0
        self._total_size = 0
        self._deserializer = None

    def get_type(self):
        return self._message_type

    def setup(self):
        self._time = time.time()
        self._deserializer = None

    def get_time(self):
        return self._time

    def keep_deserializer(self, deserializer):
        self._deserializer = deserializer

    def remove_deserializer(self):
        if self._deserializer is not None:
            self._deserializer.free()

        self._deserializer = None

    def serialize(self, serializer):
        serializer.serializeInt(self.get_type().ordinal())
        self.serialize_impl(serializer)
        self._total_size = serializer.getTotalSize()

    def serialize_impl(self, serializer):
        raise NotImplemented

    def deserialize(self, deserializer):
        self.deserialize_impl(deserializer)
        if self._deserializer is not None:
            self._deserializer.free()

    def deserialize_impl(self, deserializer):
        raise NotImplemented

    def get_size(self):
        return self._total_size
