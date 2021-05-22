from fishtankclient.engine.message.Message import Message


class ErrorContainerMessage(Message):
    def __init__(self, factory, message_type):
        super(ErrorContainerMessage, self).__init__(factory, message_type)

    def free(self):
        super(Message, self).free()

    def deserialize_impl(self, deserializer):
        pass

    def serialize_impl(self, serializer):
        pass
