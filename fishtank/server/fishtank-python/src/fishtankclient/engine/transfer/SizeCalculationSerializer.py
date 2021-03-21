
from fishtankclient.engine.transfer.Serializer import Serializer


class SizeCalculationSerializer(Serializer):
    def __init__(self):
        super(SizeCalculationSerializer, self).__init__()
        self._size = 0

    def free(self):
        self._size = 0

    def serialize_byte(self, b):
        self._size += 1

    def serialize_unsigned_byte(self, b):
        self._size += 1

    def deserialize_byte(self):
        raise NotImplemented

    def deserialize_unsigned_byte(self):
        raise NotImplemented

    def serialize_short(self, s):
        self._size += 2

    def serialize_unsigned_short(self, s):
        self._size += 2

    def deserialize_short(self):
        raise NotImplemented

    def deserialize_unsigned_short(self):
        raise NotImplemented

    def serialize_int(self, i):
        self._size += 4

    def serialize_unsigned_int(self, i):
        self._size += 4

    def deserialize_int(self):
        raise NotImplemented

    def deserialize_unsigned_int(self):
        raise NotImplemented

    def serialize_long(self, i):
        self._size += 8

    def serialize_unsigned_long(self, i):
        self._size += 8

    def deserialize_long(self):
        raise NotImplemented

    def deserialize_unsigned_long(self):
        raise NotImplemented

    def serialize_float(self, f):
        self._size += 4

    def deserialize_float(self):
        raise NotImplemented

    def get_total_size(self):
        return self._size
