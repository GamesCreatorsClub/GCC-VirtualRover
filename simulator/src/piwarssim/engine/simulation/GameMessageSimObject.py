from enum import Enum

from piwarssim.engine.simulation.MovingSimulationObjectWithPositionAndOrientation import MovingSimulationObjectWithPositionAndOrientation
from piwarssim.engine.simulation.rovers.RoverType import RoverType


class GameMessageSimObject(MovingSimulationObjectWithPositionAndOrientation):
    def __init__(self, factory, sim_object_id, sim_object_type):
        super(GameMessageSimObject, self).__init__(factory, sim_object_id, sim_object_type)
        self.flashing = False
        self.in_game = False
        self.waiting = False
        self.message = ""

    def free(self):
        self.flashing = False
        self.in_game = False
        self.waiting = False
        self.message = ""
        super(GameMessageSimObject, self).free()

    def set_message(self, message):
        self.message = message

    def get_message(self):
        return self.message

    def serialize(self, full, serializer):
        super(GameMessageSimObject, self).serialize(full, serializer)
        serializer.serialize_unsigned_byte((1 if self.flashing else 0) + (2 if self.in_game else 0) + (4 if self.waiting else 0))

    def deserialize(self, full, serializer):
        super(GameMessageSimObject, self).deserialize(full, serializer)
        status = serializer.deserialize_unsigned_byte()
        message = serializer.deserialize_string()

        flashing = (status & 1) != 0
        in_game = (status & 2) != 0
        waiting = (status & 3) != 0

        self.changed = self.changed or self.flashing != flashing or self.in_game != in_game or self.waiting != waiting or self.message != message

        self.flashing = flashing
        self.in_game = in_game
        self.waiting = waiting
        self.message = message

    def size(self, full):
        return super(GameMessageSimObject, self).size(full) + 1 + 2 + (len(self.message) if self.message is not None else 0)

    def copy_internal(self, new_object):
        super(GameMessageSimObject, self).copy_internal(new_object)
        new_object.flashing = self.flashing
        new_object.in_game = self.in_game
        new_object.waiting = self.waiting
        new_object.message = self.message

        return new_object

    def __repr__(self):
        return "GameMessage[" + super(GameMessageSimObject, self).__repr__() + ", in_game=" + str(self.in_game) + ", waiting=" + str(self.waiting) + ", msg=\"" + self.message + "\"]"
