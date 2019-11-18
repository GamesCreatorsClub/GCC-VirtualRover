from piwarssim.engine.message.Message import Message
from piwarssim.engine.input.PlayerInputs import PlayerInputs


class PlayerInputMessage(Message):
    def __init__(self, factory, message_type):
        super(PlayerInputMessage, self).__init__(factory, message_type)
        self._session_id = None
        self._frame_no = None
        self._player_inputs = PlayerInputs()
        self._received_removed_frame_nos = []

    def get_session_id(self):
        return self._session_id

    def get_frame_no(self):
        return self._frame_no

    def add_received_removed_frame_no(self, received_removed_frame_no):
        self._received_removed_frame_nos.append(received_removed_frame_no)

    def get_received_removed_frame_nos(self):
        return self._received_removed_frame_nos

    def get_player_inputs(self):
        return self._player_inputs

    def deserialize_impl(self, deserializer):
        self._player_inputs = PlayerInputs()
        self._received_removed_frame_nos = []

        # super(PlayerInputMessage, self).deserialize_impl(deserializer)
        self._session_id = deserializer.deserialize_unsigned_short()
        self._frame_no = deserializer.deserialize_unsigned_short()

        del self._received_removed_frame_nos[:]
        size = deserializer.deserialize_unsigned_byte()
        for i in range(size):
            self._received_removed_frame_nos.append(deserializer.deserialize_unsigned_short())

        self._player_inputs.clear()
        size = deserializer.deserialize_unsigned_byte()
        for i in range(size):
            player_input = self._player_inputs.new_player_input()
            player_input.deserialize(deserializer)
            player_input.set_sequence_no(self._frame_no + i)
            self._player_inputs.add_input(player_input)

    def serialize_impl(self, serializer):
        # super(PlayerInputMessage, self).serialize_impl(serializer)
        serializer.serialize_unsigned_short(self._session_id)
        serializer.serialize_unsigned_short(self._frame_no)

        serializer.serialize_unsigned_byte(len(self._received_removed_frame_nos))
        for frame_no in self._received_removed_frame_nos:
            serializer.serialize_unsigned_short(frame_no)

        inputs = self._player_inputs.get_inputs()
        serializer.serialize_unsigned_byte(len(inputs))
        for player_input in inputs:
            player_input.serialize(serializer)
