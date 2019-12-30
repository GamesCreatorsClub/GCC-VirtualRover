
from piwarssim.engine.simulation.SimulationObjectWithPositionAndOrientation import SimulationObjectWithPositionAndOrientation


class CameraAttachmentObject(SimulationObjectWithPositionAndOrientation):

    def __init__(self, factory, sim_object_id, sim_object_type):
        super(CameraAttachmentObject, self).__init__(factory, sim_object_id, sim_object_type)
        self._parent_id = 0

    def free(self):
        super(CameraAttachmentObject, self).free()
        self._parent_id = 0

    def set_parent_id(self, parent_id):
        self.changed = self.changed or self._parent_id != parent_id
        self._parent_id = parent_id

    def get_parent_id(self):
        return self._parent_id

    def set_score(self, score):
        self.changed = self.changed or self._score != score
        self._score = score

    def get_score(self):
        return self._score

    def set_balloon_bits(self, balloon_bits):
        self.changed = self.changed or self._balloon_bits != balloon_bits
        self._balloon_bits = balloon_bits

    def get_balloon_bits(self):
        return self._balloon_bits

    def serialize(self, full, serializer):
        super(CameraAttachmentObject, self).serialize(full, serializer)

        serializer.serialize_unsigned_short(self._parent_id)

    def deserialize(self, full, serializer):
        super(CameraAttachmentObject, self).deserialize(full, serializer)
        parent_id = serializer.deserialize_unsigned_short()

        self.set_parent_id(parent_id)

    def size(self, full):
        return super(CameraAttachmentObject, self).size(full) + 2 + 2

    def copy_internal(self, new_object):
        super(CameraAttachmentObject, self).copy_internal(new_object)
        new_object._parent_id = self._parent_id

        return new_object

    def __repr__(self):
        return "CameraAttachment[" + super(CameraAttachmentObject, self).__repr__() + ", parent=" + str(self._parent_id) + "]"
