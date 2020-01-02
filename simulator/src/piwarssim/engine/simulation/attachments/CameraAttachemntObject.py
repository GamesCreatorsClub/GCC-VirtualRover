
from piwarssim.engine.simulation.SimulationObjectWithPositionAndOrientation import SimulationObjectWithPositionAndOrientation


class CameraAttachmentObject(SimulationObjectWithPositionAndOrientation):

    def __init__(self, factory, sim_object_id, sim_object_type):
        super(CameraAttachmentObject, self).__init__(factory, sim_object_id, sim_object_type)
        self._parent_id = 0
        self._camera_angle = 0.0

    def free(self):
        super(CameraAttachmentObject, self).free()
        self._parent_id = 0

    def set_parent_id(self, parent_id):
        self.changed = self.changed or self._parent_id != parent_id
        self._parent_id = parent_id

    def get_parent_id(self):
        return self._parent_id

    def set_camera_angle(self, camera_angle):
        self.changed = self.changed or self._camera_angle != camera_angle
        self._camera_angle = camera_angle

    def get_camera_angle(self):
        return self._camera_angle

    def serialize(self, full, serializer):
        super(CameraAttachmentObject, self).serialize(full, serializer)

        serializer.serialize_unsigned_short(self._parent_id)
        serializer.serialize_float(self._camera_angle)

    def deserialize(self, full, serializer):
        super(CameraAttachmentObject, self).deserialize(full, serializer)
        self.set_parent_id(serializer.deserialize_unsigned_short())
        self.set_camera_angle(serializer.deserialize_unsigned_float())

    def size(self, full):
        return super(CameraAttachmentObject, self).size(full) + 2 + 2

    def copy_internal(self, new_object):
        super(CameraAttachmentObject, self).copy_internal(new_object)
        new_object._parent_id = self._parent_id

        return new_object

    def attach_to_rover(self, rover):
        self.set_parent_id(rover.get_id())
        self.set_position_v(rover.camera_position)
        self.set_orientation_q(rover.camera_orientation)
        self._camera_angle = rover.camera_angle
        rover.set_camera_id(self.get_id())

    def __repr__(self):
        return "CameraAttachment[" + super(CameraAttachmentObject, self).__repr__() + ", parent=" + str(self._parent_id) + "]"
