from piwarssim.engine.message.Message import Message


class SimulationObjectMessage(Message):
    def __init__(self, factory, message_type):
        super(SimulationObjectMessage, self).__init__(factory, message_type)
        self._id = 0

    def get_id(self):
        return self._id

    def set_id(self, frame_no):
        self._id = frame_no

    def deserialize_impl(self, deserializer):
        # super(SimulationObjectMessage, self).deserialize_impl(deserializer)
        self._id = deserializer.deserialize_int()

    def serialize_impl(self, serializer):
        # super(SimulationObjectMessage, self).serialize_impl(serializer)
        serializer.serialize_int(self._id)
