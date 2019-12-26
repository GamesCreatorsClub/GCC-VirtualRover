from piwarssim.engine.factory.PooledFactory import PooledFactory
from piwarssim.engine.input.PlayerInput import PlayerInput
from piwarssim.engine.utils.short_math import is_less


class PlayerInputFactory(PooledFactory):
    def __init__(self):
        super(PlayerInputFactory, self).__init__()

    def create_new(self):
        return PlayerInput(self)


class PlayerInputs:
    MAX_INPUT_SIZE = 20

    def __init__(self):
        self._factory = PlayerInputFactory()
        self._inputs = []

    def clear(self):
        del self._inputs[:]

    def get_inputs(self):
        return self._inputs

    def add(self, player_input):
        if len(self._inputs) < PlayerInputs.MAX_INPUT_SIZE:
            self._inputs.append(player_input)

    def trim_before_frame(self, frame_no):
        input = self._inputs[0] if len(self._inputs) > 0 else None
        while input is not None and is_less(input.get_sequence_no(), frame_no):
            del self._inputs[0]
            input.free()
            input = self._inputs[0] if len(self._inputs) > 0 else None

    def get_player_input_for_frame(self, frame_no):
        for i in range(len(self._inputs)):
            input = self._inputs[i]
            if input.get_sequence_no() == frame_no:
                return input

        return None

    def add_input(self, current_frame_no, player_input):
        player_input.set_sequence_no(current_frame_no)
        if len(self._inputs) > 0:
            last_input = self._inputs[-1]
            last_seq_no = last_input.get_sequence_no()
            if last_seq_no < current_frame_no + (PlayerInputs.MAX_INPUT_SIZE - 2):
                while len(self._inputs) > 0:
                    input = self._inputs[-1]
                    del self._inputs[-1]
                    input.free()
                last_seq_no = 0
            else:
                while last_seq_no + 1 < current_frame_no:
                    last_seq_no += 1
                    filler_input = self.new_player_input()
                    filler_input.set_sequence_no(last_seq_no)
                    filler_input.assign_from(last_input)
                    self._inputs.append(filler_input)

                    if len(self._inputs) > PlayerInputs.MAX_INPUT_SIZE:
                        input = self._inputs[0]
                        del self._inputs[0]
                        input.free()

            if last_seq_no == current_frame_no:
                if not last_input.is_sent():
                    last_input.assign_from(player_input)
                else:
                    pass
            else:
                self._inputs.append(player_input)
        else:
            self._inputs.append(player_input)

    def mark_sent(self):
        for input in self._inputs:
            input.mark_sent()

    def new_player_input(self):
        return self._factory.obtain()

    def merge_inputs(self, current_frame, other_inputs):
        if len(self._inputs) == 0:
            self._inputs += other_inputs.get_inputs()
            return

        new_inputs = other_inputs.get_inputs()

        ni = 0
        oi = 0
        while ni < len(new_inputs) and oi < len(self._inputs):
            new_input = new_inputs[ni]
            old_input = self._inputs[oi]
            if is_less(new_input.get_sequence_no(), old_input.get_sequence_no()):
                if is_less(new_input.get_sequence_no(), current_frame):
                    new_input.free()
                else:
                    self._inputs.insert(oi, new_input)

                ni += 1
            elif new_input.get_sequence_no() == old_input.get_sequence_no():
                old_input.assign_from(new_input)
                new_input.free()
                ni += 1
                oi += 1
            else:
                old_input.free()
                del self._inputs[oi]
                oi += 1

        while ni < len(new_inputs):
            self._inputs.append(new_inputs[ni])
            ni += 1
