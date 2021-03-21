from enum import Enum

from fishtankclient.engine.simulation import SimulationObject


class GameMessageSimObject(SimulationObject):
    def __init__(self, factory, sim_object_id, sim_object_type):
        super(GameMessageSimObject, self).__init__(factory, sim_object_id, sim_object_type)
        self.flashing = False
        self.in_game = False
        self.waiting = False
        self.has_timer = False
        self.timer_stopped = False
        self.timer = 0
        self.message = ""

    def free(self):
        self.flashing = False
        self.in_game = False
        self.waiting = False
        self.has_timer = False
        self.timer_stopped = False
        self.timer = 0
        self.message = ""
        super(GameMessageSimObject, self).free()

    def set_timer_tens(self, tens, challenge):
        self.timer = int(tens * 100000 // challenge.get_game_tick_micros())

    def get_timer_tens(self, challenge):
        return challenge.get_game_tick_micros() * self.timer // 100000

    def set_time(self, timer):
        self._timer = timer

    def get_timer(self):
        return self._timer

    def set_message(self, message):
        self.message = message

    def get_message(self):
        return self.message

    def serialize(self, full, serializer):
        super(GameMessageSimObject, self).serialize(full, serializer)
        serializer.serialize_unsigned_byte((1 if self.flashing else 0) + (2 if self.in_game else 0) + (4 if self.waiting else 0)
                                           + (8 if self.has_timer else 0) + (16 if self.timer_stopped else 0))
        serializer.serialize_short(self.timer)
        serializer.serialize_string(self.message)

    def deserialize(self, full, serializer):
        super(GameMessageSimObject, self).deserialize(full, serializer)
        status = serializer.deserialize_unsigned_byte()
        timer = serializer.deserialize_short()
        message = serializer.deserialize_string()

        flashing = (status & 1) != 0
        in_game = (status & 2) != 0
        waiting = (status & 4) != 0
        has_timer = (status & 8) != 0
        timer_stopped = (status & 16) != 0

        self.changed = self.changed or self.flashing != flashing \
                       or self.in_game != in_game or self.waiting != waiting or self.has_timer != has_timer or self.timer_stopped != timer_stopped \
                       or self.timer != timer or self.message != message

        self.flashing = flashing
        self.in_game = in_game
        self.waiting = waiting
        self.has_timer = has_timer
        self.timer_stopped = timer_stopped
        self.timer = timer
        self.message = message

    def size(self, full):
        return super(GameMessageSimObject, self).size(full) + 1 + 2 + 2 + (len(self.message) if self.message is not None else 0)

    def copy_internal(self, new_object):
        super(GameMessageSimObject, self).copy_internal(new_object)
        new_object.flashing = self.flashing
        new_object.in_game = self.in_game
        new_object.waiting = self.waiting
        new_object.has_timer = self.has_timer
        new_object.timer = self.timer
        new_object.message = self.message

        return new_object

    def __repr__(self):
        return "GameMessage[" + super(GameMessageSimObject, self).__repr__() + ", in_game=" + str(self.in_game) + ", waiting=" + str(self.waiting) + ", has_timer=" + str(self.has_timer) + ", timer_stopped=" + str(self.timer_stopped) + ", timer=" + str(self.timer) + ", msg=\"" + self.message + "\"]"
