from piwarssim.engine.message.Message import Message


class ServerRequestScreenshotMessage(Message):
    def __init__(self, factory, message_type):
        super(ServerRequestScreenshotMessage, self).__init__(factory, message_type)

    def size(self):
        return super(ServerRequestScreenshotMessage, self).size()

    def deserialize_impl(self, deserializer):
        # super(FrameMessage, self).deserialize_impl(deserializer)
        pass

    def serialize_impl(self, serializer):
        # super(FrameMessage, self).serialize_impl(serializer)
        pass
