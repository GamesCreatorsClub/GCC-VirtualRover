import math

from piwarssim.engine.simulation.SimulationObjectWithPositionAndOrientation import SimulationObjectWithPositionAndOrientation


class DependentObject(SimulationObjectWithPositionAndOrientation):
    FLOAT_ROUNDING_ERROR = 0.000001

    def __init__(self, factory, sim_object_id, sim_object_type):
        super(DependentObject, self).__init__(factory, sim_object_id, sim_object_type)
        self._parent_id = 0

    def free(self):
        self._parent_id = -1
        super(DependentObject, self).free()

    def set_parent_id(self, parent_id):
        self.changed = self.changed or self._parent_id != parent_id
        self._parent_id = parent_id

    def get_parent_id(self):
        return self._parent_id

    def serialize(self, full, serializer):
        super(DependentObject, self).serialize(full, serializer)

        serializer.serialize_unsigned_short(self._parent_id)

    def deserialize(self, full, serializer):
        super(DependentObject, self).deserialize(full, serializer)
        parent_id = serializer.serialize_unsigned_short(self._parent_id)

        self.set_parent_id(parent_id)

    def size(self, full):
        return super(DependentObject, self).size(full) + 2

    def copy_internal(self, new_object):
        super(DependentObject, self).copy_internal(new_object)
        new_object._parent_id = self._parent_id

        return new_object

    def __repr__(self):
        return super(DependentObject, self).__repr__() + ", parent=" + str(self._parent_id)
