from piwarssim.engine.message.FrameMessage import FrameMessage


class MultiObjectRequestForFullUpdateMessage(FrameMessage):
    def __init__(self, factory, message_type):
        super(MultiObjectRequestForFullUpdateMessage, self).__init__(factory, message_type)
        self._requrested_sim_objects = []
        self.size = 0

    def free(self):
        super(MultiObjectRequestForFullUpdateMessage, self).free()
        self.size = super(MultiObjectRequestForFullUpdateMessage, self).size()

    def setup(self):
        super(MultiObjectRequestForFullUpdateMessage, self).setup()
        del self._requrested_sim_objects[:]

    def size(self):
        return self.size + len(self._requrested_sim_objects) * 2

    def deserialize_impl(self, deserializer):
        super(MultiObjectRequestForFullUpdateMessage, self).deserialize_impl(deserializer)
        size = deserializer.deserialize_unsigned_byte()
        for i in range(size):
            self._requrested_sim_objects.append(deserializer.deserialize_short())

    def serialize_impl(self, serializer):
        super(MultiObjectRequestForFullUpdateMessage, self).serialize_impl(serializer)
        serializer.serialize_unsigned_byte(len(self._requrested_sim_objects))
        for sim_object_id in self._requrested_sim_objects:
            serializer.serialize_short(sim_object_id)
