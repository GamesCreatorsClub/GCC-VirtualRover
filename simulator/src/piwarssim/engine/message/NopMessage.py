from piwarssim.engine.message.Message import Message


class NopMessage(Message):
    def __init__(self, factory, message_type):
        super(NopMessage, self).__init__(factory, message_type)

    def deserialize_impl(self, deserializer):
        pass

    def serialize_impl(self, serializer):
        pass
