from enum import Enum

from piwarssim.engine.simulation.MovingSimulationObjectWithPositionAndOrientation import MovingSimulationObjectWithPositionAndOrientation


class WaitingPlayerSimObject(MovingSimulationObjectWithPositionAndOrientation):
    def __init__(self, factory, sim_object_id, sim_object_type):
        super(WaitingPlayerSimObject, self).__init__(factory, sim_object_id, sim_object_type)
        self._rover_name = "waiting player"

    def free(self):
        super(WaitingPlayerSimObject, self).free()

    def get_rover_colour(self):
        return self._rover_colour

    def serialize(self, full, serializer):
        super(WaitingPlayerSimObject, self).serialize(full, serializer)

        if full:
            serializer.serialize_short_string(self._rover_name)

    def deserialize(self, full, serializer):
        super(WaitingPlayerSimObject, self).deserialize(full, serializer)

        if full:
            self._rover_name = serializer.deserialize_short_string()

    def size(self, full):
        return super(WaitingPlayerSimObject, self).size(full) + (1 + len(self._rover_name) if full else 0) + 1

    def copy_internal(self, new_object):
        super(WaitingPlayerSimObject, self).copy_internal(new_object)

        new_object._rover_name = self._rover_name

        return new_object

    def __repr__(self):
        return self._rover_name + ", " + super(WaitingPlayerSimObject, self).__repr__()
