from piwarssim.engine.message.SimulationObjectMessage import SimulationObjectMessage


class PlayerDetailsMessage(SimulationObjectMessage):
    def __init__(self, factory, message_type):
        super(PlayerDetailsMessage, self).__init__(factory, message_type)
        self._alias = None

    def get_alias(self):
        return self._alias

    def set_values(self, sim_object_id, alias):
        super(PlayerDetailsMessage, self).set_id(sim_object_id)
        self._alias = alias

    def deserialize_impl(self, deserializer):
        super(PlayerDetailsMessage, self).deserialize_impl(deserializer)
        self._alias = deserializer.deserialize_string()

    def serialize_impl(self, serializer):
        super(PlayerDetailsMessage, self).serialize_impl(serializer)
        serializer.serialize_string(self._alias)
