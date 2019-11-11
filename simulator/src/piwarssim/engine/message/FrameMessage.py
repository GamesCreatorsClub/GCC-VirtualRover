from piwarssim.engine.message.Message import Message


class FrameMessage(Message):
    def __init__(self, factory, message_type):
        super(FrameMessage, self).__init__(factory, message_type)
        self._frame_no = 0

    def get_frame_no(self):
        return self._frame_no

    def set_frame_no(self, frame_no):
        self._frame_no = frame_no

    def deserialize_impl(self, deserializer):
        # super(FrameMessage, self).deserialize_impl(deserializer)
        self._frame_no = deserializer.deserialize_int()

    def serialize_impl(self, serializer):
        # super(FrameMessage, self).serialize_impl(serializer)
        serializer.serialize_int(self._frame_no)
