from enum import Enum

from piwarssim.engine.simulation.MovingSimulationObjectWithPositionAndOrientation import MovingSimulationObjectWithPositionAndOrientation
from piwarssim.engine.utils.Shapes import Circle


class GolfBallSimObject(MovingSimulationObjectWithPositionAndOrientation):

    def __init__(self, factory, sim_object_id, sim_object_type):
        super(GolfBallSimObject, self).__init__(factory, sim_object_id, sim_object_type)
        self.circle = Circle(0, 0, 25)

    def free(self):
        super(GolfBallSimObject, self).free()

    def get_circle(self):
        position = self.get_position()
        self.circle.x = position[0]
        self.circle.y = position[1]
        return self.circle

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
