import math

from fishtankclient.engine.simulation.SimulationObjectWithPositionAndOrientation import SimulationObjectWithPositionAndOrientation


class MovingSimulationObjectWithPositionAndOrientation(SimulationObjectWithPositionAndOrientation):
    FLOAT_ROUNDING_ERROR = 0.000001

    def __init__(self, factory, sim_object_id, sim_object_type):
        super(MovingSimulationObjectWithPositionAndOrientation, self).__init__(factory, sim_object_id, sim_object_type)
        self._velocity = [0.0, 0.0, 0.0]
        self._turn_speed = 0

    def free(self):
        self._velocity[0] = 0.0
        self._velocity[1] = 0.0
        self._velocity[2] = 0.0
        self._turn_speed = -1
        super(MovingSimulationObjectWithPositionAndOrientation, self).free()

    def get_velocity(self):
        return self._velocity

    def set_velocity_2(self, x, y):
        self.changed = self.changed \
                       or self.within_resolution(self._velocity[0], x) \
                       or self.within_resolution(self._velocity[1], y)

        self._velocity[0] = x
        self._velocity[1] = y

    def set_velocity_3(self, x, y, z):
        self.changed = self.changed \
                       or self.within_resolution(self._velocity[0], x) \
                       or self.within_resolution(self._velocity[1], y) \
                       or self.within_resolution(self._velocity[2], z)

        self._velocity[0] = x
        self._velocity[1] = y
        self._velocity[2] = z

    def get_turn_speed(self):
        return self._turn_speed

    def set_turn_speed(self, turn_speed):
        self.changed = self.changed or self.within_resolution(self._turn_speed, turn_speed)
        self._turn_speed = turn_speed

    def serialize(self, full, serializer):
        super(MovingSimulationObjectWithPositionAndOrientation, self).serialize(full, serializer)

        serializer.serialize_float(self._velocity[0])
        serializer.serialize_float(self._velocity[1])
        serializer.serialize_float(self._velocity[2])
        serializer.serialize_float(self._turn_speed)

    def deserialize(self, full, serializer):
        super(MovingSimulationObjectWithPositionAndOrientation, self).deserialize(full, serializer)
        x = serializer.deserialize_float()
        y = serializer.deserialize_float()
        z = serializer.deserialize_float()

        self.set_velocity_3(x, y, z)
        self.set_turn_speed(serializer.deserialize_float())

    def size(self, full):
        return super(MovingSimulationObjectWithPositionAndOrientation, self).size(full) + 4 * 4

    def copy_internal(self, new_object):
        super(MovingSimulationObjectWithPositionAndOrientation, self).copy_internal(new_object)

        new_object._velocity[0] = self._velocity[0]
        new_object._velocity[1] = self._velocity[1]
        new_object._velocity[2] = self._velocity[2]
        new_object._turn_speed = self._turn_speed

        return new_object

    def __repr__(self):
        return super(MovingSimulationObjectWithPositionAndOrientation, self).__repr__() + ", v=({:.2f}, {:.2f}, {:.2f})".format(self._velocity[0], self._velocity[1], self._velocity[2])

    def float_equal(self, a, b):
        return abs(a - b) < 0.001
