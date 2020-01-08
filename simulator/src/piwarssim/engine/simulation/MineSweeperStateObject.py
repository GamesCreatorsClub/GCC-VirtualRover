from enum import Enum

from piwarssim.engine.simulation.MovingSimulationObjectWithPositionAndOrientation import MovingSimulationObjectWithPositionAndOrientation
from piwarssim.engine.simulation import SimulationObject


class MineSweeperStateObject(SimulationObject):
    def __init__(self, factory, sim_object_id, sim_object_type):
        super(MineSweeperStateObject, self).__init__(factory, sim_object_id, sim_object_type)
        self._state_bits = 0

    def free(self):
        self._state_bits = 0
        super(MineSweeperStateObject, self).free()

    def set_state_bits(self, state_bits):
        self.changed |= (state_bits != self._state_bits)
        self._state_bits = state_bits

    def get_state_bits(self):
        return self._state_bits

    def set_state_bit(self, x, y, value):
        index = x * 4 + y
        bit = 1 << index
        state_bits = self._state_bits
        if value:
            state_bits |= bit
        else:
            state_bits &= ~bit

        self.changed |= (state_bits != self._state_bits)
        self._state_bits = state_bits

    def get_state_bit(self, x, y):
        index = x * 4 + y
        bit = 1 << index
        return (self._state_bits & bit) != 0

    def serialize(self, full, serializer):
        super(MineSweeperStateObject, self).serialize(full, serializer)
        serializer.serialize_unsigned_short(self._state_bits)

    def deserialize(self, full, serializer):
        super(MineSweeperStateObject, self).deserialize(full, serializer)
        state_bits = serializer.deserialize_unsigned_short()

        self.changed = self.changed or self._state_bits != state_bits

        self._state_bits = state_bits

    def size(self, full):
        return super(MineSweeperStateObject, self).size(full) + 2

    def copy_internal(self, new_object):
        super(MineSweeperStateObject, self).copy_internal(new_object)
        new_object._state_bits = self._state_bits

        return new_object

    def __repr__(self):
        return "MineSweeperState[" + super(MineSweeperStateObject, self).__repr__() + ", state_bits=" + str(self._state_bits) + "\"]"
