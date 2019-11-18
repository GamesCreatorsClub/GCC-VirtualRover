from piwarssim.engine.factory.PooledFactory import PooledFactory
from piwarssim.engine.input.PlayerInput import PlayerInput


class PlayerInputFactory(PooledFactory):
    def __init__(self):
        super(PlayerInputFactory, self).__init__()

    def create_new(self):
        return PlayerInput(self)


class PlayerInputs:
    def __init__(self):
        self._factory = PlayerInputFactory()
        self._inputs = []

    def clear(self):
        del self._inputs[:]

    def get_inputs(self):
        return self._inputs

    def add_input(self, player_input):
        self._inputs.append(player_input)

    def new_player_input(self):
        return self._factory.obtain()

    def pop(self, frame_no):
        if len(self._inputs) > 0:
            player_input = self._inputs[0]

            dropped = False
            while player_input is not None and player_input.get_sequence_no() < frame_no:
                if not dropped:
                    dropped = True

                player_input.free()
                del self._inputs[0]
                if len(self._inputs) > 0:
                    player_input = self._inputs[0]
                else:
                    player_input = None

            if player_input is not None and player_input.get_sequence_no() == frame_no:
                del self._inputs[0]
                return player_input

        return None

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
            if PlayerInputs.is_less(new_input.get_sequence_no(), old_input.get_sequence_no()):
                if PlayerInputs.is_less(new_input.get_sequence_no(), current_frame):
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

    @staticmethod
    def is_less(l, r):
        if l > 60000 and r < 5000:
            return True

        return l < r
