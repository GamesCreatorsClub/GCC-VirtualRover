from piwarssim.engine.message.Message import Message


class SimulationObjectMessage(Message):
    def __init__(self, factory, message_type):
        super(SimulationObjectMessage, self).__init__(factory, message_type)
        self._object_id = 0

    def get_id(self):
        return self._object_id

    def set_id(self, object_id):
        self._object_id = object_id

    def deserialize_impl(self, deserializer):
        self._object_id = deserializer.deserialize_int()

    def serialize_impl(self, serializer):
        serializer.serialize_int(self._object_id)
