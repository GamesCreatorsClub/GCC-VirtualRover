from piwarssim.engine.simulation.SimulationObject import SimulationObject


class SimulationObjectWithPosition(SimulationObject):
    def __init__(self, factory, sim_object_id, sim_object_type):
        super(SimulationObjectWithPosition, self).__init__(factory, sim_object_id, sim_object_type)
        self._position = [0.0, 0.0, 0.0]

    def free(self):
        self._position[0] = 0.0
        self._position[1] = 0.0
        self._position[2] = 0.0
        super(SimulationObject, self).free()

    def get_position(self):
        return self._position

    def set_position_v(self, position):
        self.changed = self.changed \
                       or self.within_resolution(self._position[0], position[0]) \
                       or self.within_resolution(self._position[1], position[1]) \
                       or (len(position) > 2 and self.within_resolution(self._position[2], position[2]))
        self._position[0] = position[0]
        self._position[1] = position[1]
        if len(position) > 2:
            self._position[2] = position[2]

    def set_position_2(self, x, y):
        self.changed = self.changed \
                       or self.within_resolution(self._position[0], x) \
                       or self.within_resolution(self._position[1], y)
        self._position[0] = x
        self._position[1] = y

    def set_position_3(self, x, y, z):
        self.changed = self.changed \
                       or self.within_resolution(self._position[0], x)\
                       or self.within_resolution(self._position[1], y)\
                       or self.within_resolution(self._position[2], z)
        self._position[0] = x
        self._position[1] = y
        self._position[2] = z

    def serialize(self, full, serializer):
        super(SimulationObjectWithPosition, self).serialize(full, serializer)
        serializer.serialize_float(self._position[0])
        serializer.serialize_float(self._position[1])
        serializer.serialize_float(self._position[2])

    def deserialize(self, full, serializer):
        super(SimulationObjectWithPosition, self).deserialize(full, serializer)
        self._position[0] = serializer.deserialize_float()
        self._position[1] = serializer.deserialize_float()
        self._position[2] = serializer.deserialize_float()

    def size(self, full):
        return super(SimulationObjectWithPosition, self).size(full) + 3 * 4

    def copy_internal(self, new_object):
        super(SimulationObjectWithPosition, self).copy_internal(new_object)

        new_object.set_position_v(self._position)

        return new_object

    def __repr__(self):
        return super(SimulationObjectWithPosition, self).__repr__() + ", p=({:.2f}, {:.2f}, {:.2f})".format(self._position[0], self._position[1], self._position[2])

    def within_resolution(self, a, b):
        return abs(a - b) < 0.1
