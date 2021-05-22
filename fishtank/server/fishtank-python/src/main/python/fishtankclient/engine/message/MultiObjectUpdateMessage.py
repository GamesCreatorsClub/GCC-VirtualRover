from fishtankclient.engine.message.FrameMessage import FrameMessage
from fishtankclient.engine.simulation.PiWarsSimObjectTypes import PiWarsSimObjectTypes


class MultiObjectUpdateMessage(FrameMessage):
    END_OF_ARRAY = 0
    NEW_OBJECT = 1
    UPDATED_OBJECT = 2

    def __init__(self, factory, message_type):
        super(MultiObjectUpdateMessage, self).__init__(factory, message_type)
        self._replaced_game_objects = []
        self._new_sim_objects = []
        self._sim_objects = []
        self._update_type = []
        self._unknown_objects = None
        self.size = 0
        self._received = False

    def free(self):
        super(MultiObjectUpdateMessage, self).free()
        del self._new_sim_objects[:]
        del self._sim_objects[:]
        del self._update_type[:]
        self._unknown_objects = None
        self.size = super(MultiObjectUpdateMessage, self).size()
        self._received = False

    def get_new_sim_objects(self):
        return self._new_sim_objects

    def get_sim_objects(self):
        return self._sim_objects

    def add_new_sim_object(self, sim_object):
        self._sim_objects.append(sim_object)
        self._update_type.append(MultiObjectUpdateMessage.NEW_OBJECT)
        self.size += sim_object.size(True)

    def add_updated_sim_object(self, sim_object):
        self._sim_objects.append(sim_object)
        self._update_type.append(MultiObjectUpdateMessage.UPDATED_OBJECT)
        self.size += sim_object.size(False)

    def skip_deserialize(self):
        super(MultiObjectUpdateMessage, self).remove_deserializer()

    def size(self):
        return self.size

    def deserialize_impl(self, deserializer):
        super(MultiObjectUpdateMessage, self).deserialize_impl(deserializer)
        super(MultiObjectUpdateMessage, self).keep_deserializer(deserializer)
        self._received = True

    def finish_deserialize(self, sim_object_factory, existing_sim_objects, unknown_objects):
        update_type = self._deserializer.deserialize_unsigned_byte()
        while update_type != MultiObjectUpdateMessage.END_OF_ARRAY:
            object_id = self._deserializer.deserialize_unsigned_short()
            object_type_id = self._deserializer.deserialize_unsigned_byte()

            # TODO this is wrong - it should be somehow sorted out in the factory itself!!!
            object_type = PiWarsSimObjectTypes.from_ordinal(object_type_id)

            existing_sim_object = None
            if object_id in existing_sim_objects:
                existing_sim_object = existing_sim_objects[object_id]

            if update_type == MultiObjectUpdateMessage.NEW_OBJECT:
                if existing_sim_object is not None and existing_sim_object.get_type() != object_type:
                    self._replaced_game_objects.append(existing_sim_object)
                    existing_sim_object = None

                if existing_sim_object is not None:
                    existing_sim_object.changed = False
                    existing_sim_object.deserialize(True, self._deserializer)
                    self._sim_objects.append(existing_sim_object)

                    # if existing_sim_object.changed:
                    #     updated_game_statem.set_changed()

                else:
                    new_sim_object = sim_object_factory.obtain(object_type)
                    new_sim_object.deserialize(True, self._deserializer)

                    self._new_sim_objects.append(new_sim_object)
            elif update_type == MultiObjectUpdateMessage.UPDATED_OBJECT:
                if existing_sim_object is not None:
                    existing_sim_object.changed = False
                    existing_sim_object.deserialize(False, self._deserializer)
                    self._sim_objects.append(existing_sim_object)

                    # if existing_sim_object.changed:
                    #     updated_game_statem.set_changed()
                else:
                    temp_sim_object = sim_object_factory.obtain(object_type)
                    temp_sim_object.deserialize(True, self._deserializer)
                    temp_sim_object.free()
                    unknown_objects.append(temp_sim_object)
            else:
                print("Got wrong update time " + str(update_type))
                return

            update_type = self._deserializer.deserialize_unsigned_byte()

        # super(MultiObjectUpdateMessage, self).remove_deserializer()

    def serialize_impl(self, serializer):
        super(MultiObjectUpdateMessage, self).serialize_impl(serializer)
        for i in range(len(self._sim_objects)):
            sim_object = self._sim_objects[i]
            serializer.serialize_unsigned_byte(self._update_type[i])
            serializer.serialize_short(sim_object.get_id())
            serializer.serialize_unsigned_byte(sim_object.get_type().ordinal())

            sim_object.serialize(self._update_type[i] == MultiObjectUpdateMessage.NEW_OBJECT, serializer)

        serializer.serialize_unsigned_byte(MultiObjectUpdateMessage.END_OF_ARRAY)
