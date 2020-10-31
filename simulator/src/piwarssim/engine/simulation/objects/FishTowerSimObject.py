
from piwarssim.engine.simulation.MovingSimulationObjectWithPositionAndOrientation import MovingSimulationObjectWithPositionAndOrientation
from piwarssim.engine.utils.Shapes import Polygon


class FishTowerSimObject(MovingSimulationObjectWithPositionAndOrientation):

    def __init__(self, factory, sim_object_id, sim_object_type):
        super(FishTowerSimObject, self).__init__(factory, sim_object_id, sim_object_type)

    def free(self):
        super(FishTowerSimObject, self).free()

    def serialize(self, full, serializer):
        super(FishTowerSimObject, self).serialize(full, serializer)

    def deserialize(self, full, serializer):
        super(FishTowerSimObject, self).deserialize(full, serializer)

    def size(self, full):
        return super(FishTowerSimObject, self).size(full) + (1 if full else 0)

    def copy_internal(self, new_object):
        super(FishTowerSimObject, self).copy_internal(new_object)

        return new_object

    def __repr__(self):
        return "GolfBall[" + super(FishTowerSimObject, self).__repr__() + "]"

    def get_shape(self):
        return Polygon.box(200, 200)
