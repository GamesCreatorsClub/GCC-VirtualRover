from piwarssim.engine.message.SimulationObjectMessage import SimulationObjectMessage


class RemovedMessage(SimulationObjectMessage):
    def __init__(self, factory, message_type):
        super(RemovedMessage, self).__init__(factory, message_type)

    def deserialize_impl(self, deserializer):
        pass

    def serialize_impl(self, serializer):
        pass
