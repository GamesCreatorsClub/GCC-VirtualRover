from fishtankclient.engine.message.Message import Message


class ServerRequestScreenshotMessage(Message):
    def __init__(self, factory, message_type):
        super(ServerRequestScreenshotMessage, self).__init__(factory, message_type)
        self._camera_id = 0

    def get_camera_id(self):
        return self.camera_id

    def set_camera_id(self, camera_id):
        self._camera_id = camera_id

    def size(self):
        return super(ServerRequestScreenshotMessage, self).size() + 2

    def deserialize_impl(self, deserializer):
        # super(FrameMessage, self).deserialize_impl(deserializer)
        self._camera_id = deserializer.deserialize_short()

    def serialize_impl(self, serializer):
        # super(FrameMessage, self).serialize_impl(serializer)
        serializer.serialize_unsigned_short(self._camera_id)
