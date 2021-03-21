from fishtankclient.engine.message.FrameMessage import FrameMessage


class MultiObjectRemovedMessage(FrameMessage):
    def __init__(self, factory, message_type):
        super(MultiObjectRemovedMessage, self).__init__(factory, message_type)
        self._removed_sim_objects = []
        self.size = 0

    def free(self):
        super(MultiObjectRemovedMessage, self).free()
        self.size = super(MultiObjectRemovedMessage, self).size()

    def setup(self):
        super(MultiObjectRemovedMessage, self).setup()
        del self._removed_sim_objects[:]

    def size(self):
        return self.size + len(self._removed_sim_objects) * 2

    def deserialize_impl(self, deserializer):
        super(MultiObjectRemovedMessage, self).deserialize_impl(deserializer)
        size = deserializer.deserialize_unsigned_byte()
        for i in range(size):
            self._removed_sim_objects.append(deserializer.deserialize_short())

    def serialize_impl(self, serializer):
        super(MultiObjectRemovedMessage, self).serialize_impl(serializer)
        serializer.serialize_unsigned_byte(len(self._removed_sim_objects))
        for sim_object_id in self._removed_sim_objects:
            serializer.serialize_short(sim_object_id)
