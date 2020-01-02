from piwarssim.engine.message.Message import Message


class ClientScreenshotMessage(Message):
    def __init__(self, factory, message_type):
        super(ClientScreenshotMessage, self).__init__(factory, message_type)
        self._frame_no = 0
        self._packet_no = 0
        self._total_packets = 0
        self._buffer = None

    def get_frame_no(self):
        return self._frame_no

    def get_packet_no(self):
        return self._packet_no

    def get_total_packets(self):
        return self._total_packets

    def get_buffer(self):
        return self._buffer

    def size(self):
        return super(ClientScreenshotMessage, self).size() + 4

    def deserialize_impl(self, deserializer):
        # super(FrameMessage, self).deserialize_impl(deserializer)
        self._frame_no = deserializer.deserialize_int()
        self._packet_no = deserializer.deserialize_unsigned_short()
        self._total_packets = deserializer.deserialize_unsigned_short()
        self._buffer = deserializer.deserialize_byte_array_raw()

    def serialize_impl(self, serializer):
        # super(FrameMessage, self).serialize_impl(serializer)
        pass # Not implemented
