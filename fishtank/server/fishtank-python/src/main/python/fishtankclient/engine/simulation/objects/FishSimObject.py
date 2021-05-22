
from fishtankclient.engine.simulation.MovingSimulationObjectWithPositionAndOrientation import SimulationObjectWithPositionAndOrientation
from fishtankclient.engine.utils.Shapes import Polygon


class FishSimObject(SimulationObjectWithPositionAndOrientation):

    def __init__(self, factory, sim_object_id, sim_object_type):
        super(FishSimObject, self).__init__(factory, sim_object_id, sim_object_type)
        self.speed = 0.4

    def free(self):
        super(FishSimObject, self).free()

    def serialize(self, full, serializer):
        super(FishSimObject, self).serialize(full, serializer)
        serializer.serialize_unsigned_byte(self.speed * 200)

    def deserialize(self, full, serializer):
        super(FishSimObject, self).deserialize(full, serializer)
        s = serializer.deserialize_unsigned_byte()
        self.speed = s / 200.0

    def size(self, full):
        return super(FishSimObject, self).size(full) + 1

    def copy_internal(self, new_object):
        super(FishSimObject, self).copy_internal(new_object)

        return new_object

    def __repr__(self):
        return "Fish[" + super(FishSimObject, self).__repr__() + "]"

    def get_shape(self):
        return Polygon.box(200, 200)
