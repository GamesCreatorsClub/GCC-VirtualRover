from piwarssim.engine.simulation.MovingSimulationObjectWithPositionAndOrientation import MovingSimulationObjectWithPositionAndOrientation
from piwarssim.engine.utils.Shapes import Circle


class GolfBallSimObject(MovingSimulationObjectWithPositionAndOrientation):

    def __init__(self, factory, sim_object_id, sim_object_type):
        super(GolfBallSimObject, self).__init__(factory, sim_object_id, sim_object_type)

    def free(self):
        super(GolfBallSimObject, self).free()

    def serialize(self, full, serializer):
        super(GolfBallSimObject, self).serialize(full, serializer)

    def deserialize(self, full, serializer):
        super(GolfBallSimObject, self).deserialize(full, serializer)

    def size(self, full):
        return super(GolfBallSimObject, self).size(full) + (1 if full else 0)

    def copy_internal(self, new_object):
        super(GolfBallSimObject, self).copy_internal(new_object)

        return new_object

    def __repr__(self):
        return "GolfBall[" + super(GolfBallSimObject, self).__repr__() + "]"

    def get_shape(self):
        return Circle(21)
