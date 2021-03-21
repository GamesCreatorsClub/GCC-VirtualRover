from fishtankclient.engine.message.Message import Message


class FrameMessage(Message):
    def __init__(self, factory, message_type):
        super(FrameMessage, self).__init__(factory, message_type)
        self._frame_no = 0
        self._game_inputs_frame_no = 0

    def get_frame_no(self):
        return self._frame_no

    def set_frame_no(self, frame_no):
        self._frame_no = frame_no

    def get_game_inputs_frame_no(self):
        return self._game_inputs_frame_no

    def set_game_inputs_frame_no(self, game_inputs_frame_no):
        self._game_inputs_frame_no = game_inputs_frame_no

    def size(self):
        return super(FrameMessage, self).size() + 4 + 4

    def deserialize_impl(self, deserializer):
        # super(FrameMessage, self).deserialize_impl(deserializer)
        self._frame_no = deserializer.deserialize_int()
        self._game_inputs_frame_no = deserializer.deserialize_int()

    def serialize_impl(self, serializer):
        # super(FrameMessage, self).serialize_impl(serializer)
        serializer.serialize_int(self._frame_no)
        serializer.serialize_int(self._game_inputs_frame_no)
