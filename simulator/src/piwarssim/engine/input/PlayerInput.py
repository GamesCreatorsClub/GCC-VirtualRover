class PlayerInput:
    def __init__(self, factory):
        self._factory = factory
        self._sequence_no = 0
        self._move_x = 0
        self._move_y = 0
        self._rotate_x = 0
        self._rotate_y = 0
        self._left_trigger = 0
        self._right_trigger = 0
        self._circle = False
        self._cross = False
        self._square = False
        self._triangle = False
        self._home = False
        self._share = False
        self._options = False
        self._trackpad = False
        self._hat_up = False
        self._hat_down = False
        self._hat_left = False
        self._hat_right = False
        self._desired_forward_speed = 300
        self._desired_rotation_speed = 300

    def move_x(self):
        return self._move_x

    def move_y(self):
        return self._move_y

    def rotate_x(self):
        return self._rotate_x

    def rotate_y(self):
        return self._rotate_y

    def left_trigger(self):
        return self._left_trigger

    def right_trigger(self):
        return self._right_trigger

    def circle(self):
        return self._circle

    def cross(self):
        return self._cross

    def square(self):
        return self._square

    def triangle(self):
        return self._triangle

    def home(self):
        return self._home

    def share(self):
        return self._share

    def options(self):
        return self._options

    def trackpad(self):
        return self._trackpad

    def hat_up(self):
        return self._hat_up

    def hat_down(self):
        return self._hat_down

    def hat_left(self):
        return self._hat_left

    def hat_right(self):
        return self._hat_right

    def desired_forward_speed(self):
        return self._desired_forward_speed

    def desired_rotation_speed(self):
        return self._desired_rotation_speed

    def free(self):
        self._sequence_no = 0
        self._move_x = 0
        self._move_y = 0
        self._rotate_x = 0
        self._rotate_y = 0
        self._left_trigger = 0
        self._right_trigger = 0
        self._circle = False
        self._cross = False
        self._square = False
        self._triangle = False
        self._home = False
        self._share = False
        self._options = False
        self._trackpad = False
        self._hat_up = False
        self._hat_down = False
        self._hat_left = False
        self._hat_right = False
        self._desired_forward_speed = 300
        self._desired_rotation_speed = 300
        self._factory.free(self)

    def get_sequence_no(self):
        return self._sequence_no

    def set_sequence_no(self, sequence_no):
        self._sequence_no = sequence_no

    def serialize(self, serializer):
        serializer.serialize_unsigned_short(PlayerInput.fit_to_8_bits(self._move_x) << 8 | PlayerInput.fit_to_8_bits(self._move_y))
        serializer.serialize_unsigned_short(PlayerInput.fit_to_8_bits(self._rotate_x) << 8 | PlayerInput.fit_to_8_bits(self._rotate_y))

        triggers = int(self._left_trigger * 15) + int(self._right_trigger * 15) * 16

        serializer.serialize_unsigned_short(triggers)

        bits = (1 if self._circle else 0) + \
               (2 if self._cross else 0) + \
               (4 if self._square else 0) + \
               (8 if self._triangle else 0) + \
               (6 if self._home else 0) + \
               (2 if self._share else 0) + \
               (4 if self._options else 0) + \
               (8 if self._trackpad else 0) + \
               (6 if self._hat_up else 0) + \
               (2 if self._hat_down else 0) + \
               (4 if self._hat_left else 0) + \
               (8 if self._hat_right else 0)
        serializer.serialize_unsigned_short(bits)

        serializer.serialize_unsigned_short(PlayerInput.fit_to_8_bits(int(self._desired_forward_speed / 10)) << 8 | PlayerInput.fit_to_8_bits(int(self._desired_rotation_speed / 10)))

    def deserialize(self, deserializer):
        move_xy = deserializer.deserialize_unsigned_short()
        self._move_x = (((move_xy >> 8) & 0xff) / 127) - 1
        self._move_y = ((move_xy & 0xff) / 127) - 1

        rotate_xy = deserializer.deserialize_unsigned_short()
        self._rotate_x = (((rotate_xy >> 8) & 0xff) / 127) - 1
        self._rotate_y = ((rotate_xy & 0xff) / 127) - 1

        triggers = deserializer.deserialize_unsigned_byte()
        self._left_trigger = (triggers & 0xf) / 15.0
        self._right_trigger = ((triggers >> 4) & 0xf) / 15.0

        bits = deserializer.deserialize_unsigned_short()
        self._circle = (bits & 1) != 0
        self._cross = (bits & 2) != 0
        self._square = (bits & 4) != 0
        self._triangle = (bits & 8) != 0

        self._home = (bits & 16) != 0
        self._share = (bits & 32) != 0
        self._options = (bits & 64) != 0
        self._trackpad = (bits & 128) != 0

        self._hat_up = (bits & 256) != 0
        self._hat_down = (bits & 512) != 0
        self._hat_left = (bits & 1024) != 0
        self._hat_right = (bits & 2048) != 0

        desired_speeds = deserializer.deserialize_unsigned_short()
        self._desired_forward_speed = ((((desired_speeds >> 8) & 0xff) / 127) - 1) * 10
        self._desired_rotation_speed = (((desired_speeds & 0xff) / 127) - 1) * 10

    def assign_from(self, player_input):
        self._sequence_no = player_input.get_sequence_no()
        self._move_x = player_input.move_x()
        self._move_y = player_input.move_y()
        self._rotate_x = player_input.rotate_x()
        self._rotate_y = player_input.rotate_y()
        self._left_trigger = player_input.left_trigger()
        self._right_trigger = player_input.right_trigger()
        self._circle = player_input.circle()
        self._cross = player_input.cross()
        self._square = player_input.square()
        self._triangle = player_input.triangle()
        self._home = player_input.home()
        self._share = player_input.share()
        self._options = player_input.options()
        self._trackpad = player_input.trackpad()
        self._hat_up = player_input.hat_up()
        self._hat_down = player_input.hat_down()
        self._hat_left = player_input.hat_left()
        self._hat_right = player_input.hat_right()
        self._desired_forward_speed = player_input.desired_forward_speed()
        self._desired_rotation_speed = player_input.desired_rotation_speed()
        self._factory.free(self)

    @staticmethod
    def fit_to_8_bits(v):
        return int((v + 1) * 127)

    @staticmethod
    def fit_to_4_bits(v):
        return int(v * 15)

    def __repr__(self):
        return "Input[{:3d}: {:3.2f}, {:3.2f}, {:3.2f}, {:3.2f}, {:2.1f}, {:2.1f}, {:1s}{:1s}{:1s}{:1s}]".format(
            self._sequence_no,
            self._move_x,
            self._move_y,
            self._rotate_x,
            self._rotate_y,
            self._left_trigger,
            self._right_trigger,
            "c" if self._circle else " ",
            "x" if self._cross else " ",
            "s" if self._square else " ",
            "t" if self._triangle else " ")