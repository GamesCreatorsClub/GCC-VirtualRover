from piwarssim.engine.message.FrameMessage import FrameMessage
from piwarssim.engine.simulation.PiWarsSimObjectTypes import PiWarsSimObjectTypes


class MultiObjectUpdateMessage(FrameMessage):
    def __init__(self, factory, message_type):
        super(MultiObjectUpdateMessage, self).__init__(factory, message_type)
        self._new_sim_objects = []
        self._updated_sim_objects = []

    def setup(self):
        super(MultiObjectUpdateMessage, self).setup()
        del self._new_sim_objects[:]
        del self._updated_sim_objects[:]

    def get_new_sim_objects(self):
        return self._new_sim_objects

    def get_updated_sim_objects(self):
        return self._updated_sim_objects

    def add_new_sim_object(self, sim_object):
        self._new_sim_objects.append(sim_object)

    def updated_new_sim_object(self, sim_object):
        self._updated_sim_objects.append(sim_object)

    def skip_deserialize(self):
        super(MultiObjectUpdateMessage, self).remove_deserializer()

    def deserialize_impl(self, deserializer):
        super(MultiObjectUpdateMessage, self).deserialize_impl(deserializer)
        super(MultiObjectUpdateMessage, self).keep_deserializer(deserializer)

    def finish_deserialize(self, sim_object_factory, existing_sim_objects):
        size = self._deserializer.deserialize_unsigned_byte()
        for i in range(size):
            object_id = self._deserializer.deserialize_unsigned_short()
            object_type_id = self._deserializer.deserialize_unsigned_byte()

            # TODO this is wrong - it should be somehow sorted out in the factory itself!!!
            object_type = PiWarsSimObjectTypes.from_ordinal(object_type_id)
            sim_object = sim_object_factory.obtain(object_type)
            sim_object.set_id(object_id)
            sim_object.deserialize(True, self._deserializer)
            self._new_sim_objects.append(sim_object)

        size = self._deserializer.deserialize_unsigned_byte()
        for i in range(size):
            object_id = self._deserializer.deserialize_unsigned_short()
            object_type_id = self._deserializer.deserialize_unsigned_byte()

            # TODO this is wrong - it should be somehow sorted out in the factory itself!!!
            object_type = PiWarsSimObjectTypes.from_ordinal(object_type_id)
            if object_id in existing_sim_objects:
                sim_object = existing_sim_objects[object_id]
                sim_object.deserialize(False, self._deserializer)
            else:
                sim_object = sim_object_factory.obtain(object_type)
                object_type = PiWarsSimObjectTypes.from_ordinal(object_type_id)
                skip_size = sim_object_factory.update_object_serialized_size(object_type)
                self._deserializer.deserialize_skip(skip_size)

        super(MultiObjectUpdateMessage, self).remove_deserializer()

    def serialize_impl(self, serializer):
        super(MultiObjectUpdateMessage, self).serialize_impl(serializer)
        serializer.serialize_unsigned_byte(len(self._new_sim_objects))
        for sim_object in self._new_sim_objects:
            serializer.serialize_short(sim_object.get_id())
            # serializer.serialize_short(sim_object.get_type().ordinal())

            sim_object.serialize(True, serializer)

        serializer.serialize_unsigned_byte(len(self._updated_sim_objects))
        for sim_object in self._updated_sim_objects:
            serializer.serialize_short(sim_object.get_id())
            # serializer.serialize_short(sim_object.get_type().ordinal())

            sim_object.serialize(False, serializer)
