import struct

from piwarssim.engine.transfer import Serializer


class ByteSerializer(Serializer):
    def __init__(self, factory):
        super(ByteSerializer, self).__init__()
        self.factory = factory
        self.buf = bytearray()

    def free(self):
        self.factory.free(self)

    def setup(self):
        del self.buf[:]

    def get_buffer(self):
        return self.buf

    def serialize_byte(self, b):
        self.buf += struct.pack('b', b)

    def serialize_unsigned_byte(self, b):
        self.buf += struct.pack('B', b)

    def deserialize_byte(self):
        b = struct.unpack('b', self.buf[0:1])[0]
        del self.buf[0:1]
        return b

    def deserialize_unsigned_byte(self):
        b = struct.unpack('B', self.buf[0:1])[0]
        del self.buf[0:1]
        return b

    def serialize_short(self, s):
        self.buf += struct.pack('h', s)

    def serialize_unsigned_short(self, s):
        self.buf += struct.pack('H', s)

    def deserialize_short(self):
        s = struct.unpack('h', self.buf[0:2])[0]
        del self.buf[0:2]
        return s

    def deserialize_unsigned_short(self):
        s = struct.unpack('H', self.buf[0:2])[0]
        del self.buf[0:2]
        return s

    def serialize_int(self, i):
        self.buf += struct.pack('i', i)

    def serialize_unsigned_int(self, i):
        self.buf += struct.pack('I', i)

    def deserialize_int(self):
        i = struct.unpack('i', self.buf[0:4])[0]
        del self.buf[0:4]
        return i

    def deserialize_unsigned_int(self):
        i = struct.unpack('I', self.buf[0:4])[0]
        del self.buf[0:4]
        return i

    def serialize_long(self, l):
        self.buf += struct.pack('q', l)

    def serialize_unsigned_long(self, l):
        self.buf += struct.pack('Q', l)

    def deserialize_long(self):
        l = struct.unpack('q', self.buf[0:8])[0]
        del self.buf[0:8]
        return l

    def deserialize_unsigned_long(self):
        f = struct.unpack('Q', self.buf[0:8])[0]
        del self.buf[0:8]
        return f

    def serialize_float(self, f):
        self.buf += struct.pack('f', f)

    def deserialize_float(self):
        f = struct.unpack('f', self.buf[0:4])[0]
        del self.buf[0:4]
        return f

    def get_total_size(self):
        return len(self.buf)
