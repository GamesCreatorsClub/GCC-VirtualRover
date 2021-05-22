
from fishtankclient.engine.simulation.rovers.AbstractRoverSimObject import AbstractRoverSimObject
from fishtankclient.engine.simulation.rovers.RoverType import RoverType
from fishtankclient.engine.utils import Polygon


class MacFeegle(AbstractRoverSimObject):

    def __init__(self, factory, sim_object_id, sim_object_type):
        super(MacFeegle, self).__init__(factory, sim_object_id, sim_object_type, RoverType.GCCRoverM16)
        self.attachment_position = (80.0, 0.0)
        self.stereo_camera = True
        self.interpupilary_distance = 61
        self.camera_position = (50.0, 0.0, 320.0)
        self.camera_orientation = AbstractRoverSimObject.from_yaw_pitch_roll(45.0, 0.0, 0.0) # positive yaw is down
        self.camera_angle = 45.0

    def __repr__(self):
        return "MacFeegle[" + super(MacFeegle, self).__repr__() + "]"

    def get_shape(self):
        return Polygon.box(280, 200)
