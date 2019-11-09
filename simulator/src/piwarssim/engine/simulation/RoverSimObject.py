
from piwarssim.engine.simulation.SimulationObjectWithPositionAndOrientation import SimulationObjectWithPositionAndOrientation
from piwarssim.engine.simulation.rovers.RoverType import RoverType


class RoverSimObject(SimulationObjectWithPositionAndOrientation):
    def __init__(self, factory, sim_object_id):
        super(RoverSimObject, self).__init__(factory, sim_object_id)
        self._rover_type = None
        self._challenge_bits = 0
        self._score = 0

    def get_rover_type(self):
        return self._rover_type

    def set_rover_type(self, rover_type):
        self._rover_type = rover_type

    def get_challenge_bits(self):
        return self._challenge_bits

    def set_challenge_bits(self, challenge_bits):
        self._challenge_bits = challenge_bits

    def get_score(self):
        return self._score

    def set_score(self, score):
        self._score = score

    def serialize(self, full, serializer):
        super(RoverSimObject, self).serialize(full, serializer)

        if full:
            serializer.serialize_string(self._rover_type.get_name())
        serializer.serialize_byte(0 if self._rover_type is None else self._rover_type.get_id())
        serializer.serialize_byte(self._score)
        serializer.serialize_short(self._challenge_bits)

    def deserialize(self, full, serializer):
        super(RoverSimObject, self).deserialize(full, serializer)

        if full:
            serializer.deserialize_string()

        rover_type_id = serializer.deserialize_byte()
        self._rover_type = RoverType.from_ordinal(rover_type_id)
        self._score = serializer.deserialize_byte()
        self._challenge_bits = serializer.deserialize_short()

    def copy_internal(self, new_object):
        super(RoverSimObject, self).copy_internal(new_object)

        new_object.set_rover_type(self._rover_type)
        new_object.set_challenge_bits(self._challenge_bits)
        new_object.set_score(self._score)

        return new_object
