from fishtankclient.engine.simulation.MovingSimulationObjectWithPositionAndOrientation import MovingSimulationObjectWithPositionAndOrientation
from fishtankclient.engine.simulation.objects.FishSimObject import FishSimObject
from fishtankclient.engine.utils.Shapes import Circle


class TetraFish(FishSimObject):

    def __init__(self, factory, sim_object_id, sim_object_type):
        super().__init__(factory, sim_object_id, sim_object_type)

    def free(self):
        super().free()

    def serialize(self, full, serializer):
        super().serialize(full, serializer)

    def deserialize(self, full, serializer):
        super().deserialize(full, serializer)

    def size(self, full):
        return super().size(full) + (1 if full else 0)

    def copy_internal(self, new_object):
        super().copy_internal(new_object)

        return new_object

    def __repr__(self):
        return "TetraFish[" + super(TetraFish, self).__repr__() + "]"

    def get_shape(self):
        return Circle(21)
