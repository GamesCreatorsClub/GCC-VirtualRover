import math

from piwarssim.engine.simulation.DependentObject import DependentObject


class PiNoonAttachmentObject(DependentObject):

    def __init__(self, factory, sim_object_id, sim_object_type):
        super(PiNoonAttachmentObject, self).__init__(factory, sim_object_id, sim_object_type)
        self._score = 0
        self._balloon_bits = 0

    def free(self):
        self._score = 0
        self._balloon_bits = 0
        super(PiNoonAttachmentObject, self).free()

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
        super(PiNoonAttachmentObject, self).serialize(full, serializer)

        serializer.serialize_byte(self._score)
        serializer.serialize_unsigned_byte(self._balloon_bits)

    def deserialize(self, full, serializer):
        super(PiNoonAttachmentObject, self).deserialize(full, serializer)
        score = serializer.deserialize_byte()
        balloon_bits = serializer.deserialize_unsigned_byte()

        self.set_score(score)
        self.set_balloon_bits(balloon_bits)

    def size(self, full):
        return super(PiNoonAttachmentObject, self).size(full) + 2

    def copy_internal(self, new_object):
        super(PiNoonAttachmentObject, self).copy_internal(new_object)
        new_object._score = self._score
        new_object._balloon_bits = self._balloon_bits

        return new_object

    def __repr__(self):
        return "PiNoonAttachment[" + super(PiNoonAttachmentObject, self).__repr__() + ", score=" + str(self._score) + ", bb=" + str(self._balloon_bits) + "]"
