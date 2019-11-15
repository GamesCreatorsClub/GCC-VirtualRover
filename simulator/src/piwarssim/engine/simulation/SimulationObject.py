
from piwarssim.engine.factory import TypedObject


class SimulationObject(TypedObject):
    VECTOR_ZERO = [0, 0, 0]
    QUATERNION_FORWARD = [0, 0, 0, 1]

    def __init__(self, factory, sim_object_id, sim_object_type):
        super(SimulationObject, self).__init__(factory)
        self._id = sim_object_id
        self._sim_object_type = sim_object_type
        self.changed = False
        self._added = True
        self._removed = False
        self._last_sim_state = None

        self._link_back = None

    def get_type(self):
        return self._sim_object_type

    def remove(self):
        self._removed = True

    def is_added(self):
        return self._added

    def is_removed(self):
        return self._removed

    def get_id(self):
        return self._id

    def set_id(self, object_id):
        self._id = object_id

    def get_last_sim_state(self):
        return self._last_sim_state

    def get_link_back(self):
        return self._link_back

    def set_link_back(self, link):
        self._link_back = link

    def process(self, challenge, objects):
        pass

    def perform_command(self, command):
        pass

    def server_update_message(self, server_frame_no, message_factory):
        # This is needed only for clients
        return message_factory.server_update_command(self._id, server_frame_no, SimulationObject.VECTOR_ZERO, SimulationObject.VECTOR_ZERO, SimulationObject.QUATERNION_FORWARD, 0, 0)

    def newly_created_object_message(self, message_factory):
        raise NotImplemented

    def serialize(self, full, serializer):
        serializer.serialize_byte(self._sim_object_type.ordinal())
        serializer.serialize_unsigned_byte(0)  # Health!?

    def deserialize(self, full, serializer):
        # serializer.deserialize_byte()
        # Above is already deserialised so we know the type of object we are creating in the first place
        pass

    def copy_internal(self, new_object):
        new_object.changed = False
        new_object.added = False
        new_object._id = self._id
        new_object._link_back = self._link_back
        return new_object

    def copy(self, engine_state):
        self._last_sim_state = engine_state
        if not self.changed:
            return self

        return self.copy_internal(self.factory.obtain(self.get_type()))

    def __repr__(self):
        return "{:d}".format(self._id)
