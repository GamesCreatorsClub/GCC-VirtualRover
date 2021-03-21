
from fishtankclient.engine.simulation.SimulationObjectWithPositionAndOrientation import SimulationObjectWithPositionAndOrientation


class CameraPositionObject(SimulationObjectWithPositionAndOrientation):

    def __init__(self, factory, sim_object_id, sim_object_type):
        super().__init__(factory, sim_object_id, sim_object_type)

    def free(self):
        super().free()

    def serialize(self, full, serializer):
        super().serialize(full, serializer)

    def deserialize(self, full, serializer):
        super().deserialize(full, serializer)

    def size(self, full):
        return super().size(full)

    def copy_internal(self, new_object):
        super().copy_internal(new_object)

        return new_object

    def __repr__(self):
        return "CameraPosition[" + super().__repr__() + "]"
