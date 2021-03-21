class PlayerInput:
    def __init__(self, factory):
        self._factory = factory
        self._is_sent_flag = False
        self._sequence_no = 0
        self._cam_x = 0
        self._cam_y = 0
        self._cam_z = 0
        self._trigger = False
        self._pause = False

    def is_sent(self):
        return self._is_sent_flag

    def mark_sent(self):
        self._is_sent_flag = True

    def get_sequence_no(self):
        return self._sequence_no

    def set_sequence_no(self, sequence_no):
        self._sequence_no = sequence_no

    def cam_x(self):
        return self._cam_x

    def cam_y(self):
        return self._cam_y

    def cam_z(self):
        return self._cam_z

    def trigger(self):
        return self._trigger

    def pause(self):
        return self._pause

    def size(self):
        return 14

    def free(self):
        self._is_sent_flag = False
        self._sequence_no = 0
        self._cam_x = 0
        self._cam_y = 0
        self._cam_z = 0
        self._trigger = False
        self._pause = False
        self._factory.free(self)

    def serialize(self, serializer):
        serializer.serialize_float(self._cam_x)
        serializer.serialize_float(self._cam_y)
        serializer.serialize_float(self._cam_z)

        bits = (1 if self._trigger else 0) + \
               (2 if self._pause else 0)
        serializer.serialize_unsigned_short(bits)

    def deserialize(self, deserializer):
        self._cam_x = deserializer.deserialize_float()
        self._cam_y = deserializer.deserialize_float()
        self._cam_z = deserializer.deserialize_float()

        bits = deserializer.deserialize_unsigned_short()
        self._trigger = (bits & 1) != 0
        self._pause = (bits & 2) != 0

    def assign_from(self, player_input):
        self._sequence_no = player_input.get_sequence_no()
        self._cam_x = player_input.cam_x()
        self._cam_y = player_input.cam_y()
        self._cam_z = player_input.cam_z()

        self._trigger = player_input.trigger()
        self._pause = player_input.pause()

        self._factory.free(self)

    @staticmethod
    def fit_to_8_bits(v):
        return int((v + 1) * 127)

    @staticmethod
    def fit_to_4_bits(v):
        return int(v * 15)

    def __repr__(self):
        return "Input[{:3d}: {:3.2f}, {:3.2f}, {:3.2f}, {:1s}{:1s}]".format(
            self._sequence_no,
            self._cam_x,
            self._cam_y,
            self._cam_z,
            "t" if self._trigger else " ",
            "p" if self._pause else " ")