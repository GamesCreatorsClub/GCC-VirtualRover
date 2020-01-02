

class Serializer:
    def __init__(self):
        self.MTU = 508
        self.ENCODING = 'UTF'

    def free(self):
        raise NotImplemented

    def serialize_byte(self, b):
        raise NotImplemented

    def serialize_unsigned_byte(self, b):
        raise NotImplemented

    def deserialize_byte(self):
        raise NotImplemented

    def deserialize_unsigned_byte(self):
        raise NotImplemented

    def serialize_short(self, s):
        raise NotImplemented

    def serialize_unsigned_short(self, s):
        self.serialize_short(s)

    def deserialize_short(self):
        raise NotImplemented

    def deserialize_unsigned_short(self):
        raise NotImplemented

    def serialize_int(self, i):
        raise NotImplemented

    def serialize_unsigned_int(self, i):
        raise NotImplemented

    def deserialize_int(self):
        raise NotImplemented

    def deserialize_unsigned_int(self):
        raise NotImplemented

    def serialize_long(self, i):
        raise NotImplemented

    def serialize_unsigned_long(self, i):
        raise NotImplemented

    def deserialize_long(self):
        raise NotImplemented

    def deserialize_unsigned_long(self):
        raise NotImplemented

    def serialize_float(self, f):
        raise NotImplemented

    def deserialize_float(self):
        raise NotImplemented

    def serialize_string(self, s):
        self.serialize_byte_array(s.encode(self.ENCODING))

    def serialize_short_string(self, s):
        buf = s.encode(self.ENCODING)
        if len(buf) > 255:
            raise ValueError("String is too long; " + str(len(s)))

        l = len(buf)
        b = l - 128
        self.serialize_byte(b)
        self.serialize_bytes(buf)

    def serialize_byte_array(self, buf):
        if len(buf) > 255:
            raise ValueError("Byte array is too long; " + str(len(buf)))

        self.serialize_short(len(buf))
        self.serialize_bytes(buf)

    def deserialize_byte_array(self):
        l = self.deserialize_unsigned_short()
        buf = [0] * l
        self.deserialize_bytes(buf)
        return buf

    def deserialize_byte_array_raw(self):
        l = self.deserialize_unsigned_short()

        return self.deserialize_bytes_raw(l)

    def deserialize_bytes_raw(self, l):
        pass

    def deserialize_string(self):
        buf = self.deserialize_byte_array()
        return bytes(buf).decode(self.ENCODING)

    def deserialize_short_string(self):
        b = self.deserialize_byte()
        l = b + 128
        buf = [0] * l
        self.deserialize_bytes(buf)
        return bytes(buf).decode(self.ENCODING)

    def serialize_bytes(self, buf):
        for b in buf:
            self.serialize_byte(b)

    def deserialize_bytes(self, buf):
        for i in range(len(buf)):
            buf[i] = self.deserialize_byte()

    def deserialize_skip(self, size):
        for i in range(size):
            self.deserialize_byte()
