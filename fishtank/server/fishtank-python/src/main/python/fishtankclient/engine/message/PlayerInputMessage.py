from fishtankclient.engine.message.Message import Message
from fishtankclient.engine.input.PlayerInputs import PlayerInputs


class PlayerInputMessage(Message):
    def __init__(self, factory, message_type):
        super(PlayerInputMessage, self).__init__(factory, message_type)
        self._session_id = None
        self._frame_no = 0
        self._game_update_frame_no = 0
        self._player_inputs = PlayerInputs()
        self._received_removed_frame_nos = []
        self._input_size = 0

    def free(self):
        super(PlayerInputMessage, self).free()
        self._session_id = None
        self._frame_no = None
        self._game_update_frame_no = None
        del self._received_removed_frame_nos[:]
        self._input_size = 0

    def get_session_id(self):
        return self._session_id

    def set_session_id(self, session_id):
        self._session_id = session_id

    def set_frame_no(self, frame_no):
        self._frame_no = frame_no

    def get_frame_no(self):
        return self._frame_no

    def set_game_update_frame_no(self, game_update_frame_no):
        self._game_update_frame_no = game_update_frame_no

    def get_game_update_frame_no(self):
        return self._game_update_frame_no

    def add_received_removed_frame_no(self, received_removed_frame_no):
        self._received_removed_frame_nos.append(received_removed_frame_no)

    def get_received_removed_frame_nos(self):
        return self._received_removed_frame_nos

    def get_player_inputs(self):
        return self._player_inputs

    def size(self):
        inputs = self._player_inputs.get_inputs()

        if self._input_size == 0 and len(inputs) > 0:
            self._input_size = inputs[0].size()

        return super(PlayerInputMessage, self).size() + 2 + 2 + 2 + 1 + 1 + (len(self._received_removed_frame_nos) * 2) + self._input_size * len(inputs)

    def deserialize_impl(self, deserializer):
        self._player_inputs = PlayerInputs()
        del self._received_removed_frame_nos[:]

        # super(PlayerInputMessage, self).deserialize_impl(deserializer)
        self._session_id = deserializer.deserialize_unsigned_short()
        self._frame_no = deserializer.deserialize_unsigned_short()
        self._game_update_frame_no = deserializer.deserialize_unsigned_short()

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
            self._player_inputs.add(player_input)

    def serialize_impl(self, serializer):
        # super(PlayerInputMessage, self).serialize_impl(serializer)
        serializer.serialize_unsigned_short(self._session_id)
        serializer.serialize_unsigned_short(self._frame_no)
        serializer.serialize_unsigned_short(self._game_update_frame_no)

        serializer.serialize_unsigned_byte(len(self._received_removed_frame_nos))
        for frame_no in self._received_removed_frame_nos:
            serializer.serialize_unsigned_short(frame_no)

        inputs = self._player_inputs.get_inputs()
        serializer.serialize_unsigned_byte(len(inputs))
        for player_input in inputs:
            player_input.serialize(serializer)
            player_input.mark_sent()
