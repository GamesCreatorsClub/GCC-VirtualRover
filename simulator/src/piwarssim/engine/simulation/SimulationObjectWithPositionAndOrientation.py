import math

from piwarssim.engine.simulation.SimulationObjectWithPosition import SimulationObjectWithPosition


class SimulationObjectWithPositionAndOrientation(SimulationObjectWithPosition):
    FLOAT_ROUNDING_ERROR = 0.000001

    def __init__(self, factory, sim_object_id, sim_object_type):
        super(SimulationObjectWithPositionAndOrientation, self).__init__(factory, sim_object_id, sim_object_type)
        self._orientation = [0.0, 0.0, 0.0, 1.0]
        self._bearing = -1

    def free(self):
        self._orientation[0] = 0.0
        self._orientation[1] = 0.0
        self._orientation[2] = 0.0
        self._orientation[3] = 0.0
        self._bearing = -1
        super(SimulationObjectWithPositionAndOrientation, self).free()

    def get_orientation(self):
        return self._orientation

    def set_orientation_q(self, orientation):
        self.set_orientation_4(orientation[0], orientation[1], orientation[2], orientation[3])

    def set_orientation_4(self, x, y, z, w):
        if self._orientation[3] < 0:
            self._orientation[0] = -self._orientation[0]  # This is quaternion.conjugate
            self._orientation[1] = -self._orientation[1]  # This is quaternion.conjugate
            self._orientation[2] = -self._orientation[2]  # This is quaternion.conjugate
            self._orientation[3] = -self._orientation[3]

        self.changed = self.changed \
                       or self.float_equal(self._orientation[0], x) \
                       or self.float_equal(self._orientation[1], y) \
                       or self.float_equal(self._orientation[2], z) \
                       or self.float_equal(self._orientation[3], w)

        if self.changed:
            bearing = -1

        self._orientation[0] = x
        self._orientation[1] = y
        self._orientation[2] = z
        self._orientation[3] = w

        if self._orientation[3] < 0:
            self._orientation[0] = -self._orientation[0]  # This is quaternion.conjugate
            self._orientation[1] = -self._orientation[1]  # This is quaternion.conjugate
            self._orientation[2] = -self._orientation[2]  # This is quaternion.conjugate
            self._orientation[3] = -self._orientation[3]

    def set_position_and_bearing(self, x, y, z, angle):
        self.set_position_3(x, y, z)
        self.set_bearing(angle)

    def set_position_and_bearing_rad(self, x, y, z, rad):
        self.set_position_3(x, y, z)
        self.set_bearing_rad(rad)

    def serialize(self, full, serializer):
        super(SimulationObjectWithPositionAndOrientation, self).serialize(full, serializer)
        if self._orientation[3] < 0:
            self._orientation[0] = -self._orientation[0]  # This is quaternion.conjugate
            self._orientation[1] = -self._orientation[1]  # This is quaternion.conjugate
            self._orientation[2] = -self._orientation[2]  # This is quaternion.conjugate
            self._orientation[3] = -self._orientation[3]

        serializer.serialize_float(self._orientation[0])
        serializer.serialize_float(self._orientation[1])
        serializer.serialize_float(self._orientation[2])

    def deserialize(self, full, serializer):
        super(SimulationObjectWithPositionAndOrientation, self).deserialize(full, serializer)
        x = serializer.deserialize_float()
        y = serializer.deserialize_float()
        z = serializer.deserialize_float()

        w = math.sqrt(1.0 - x * x - y * y - z * z)
        self.set_orientation_4(x, y, z, w)

    def size(self, full):
        return super(SimulationObjectWithPositionAndOrientation, self).size(full) + 3 * 4

    def copy_internal(self, new_object):
        super(SimulationObjectWithPositionAndOrientation, self).copy_internal(new_object)

        new_object.set_orientation_q(self._orientation)

        return new_object

    def _get_bearing(self):
        # _get_angle_around_axis:
        # This is temporary method - here just to provide string representation
        # Implementation is from LibGDX Quaternion
        # 		final float d = Vector3.dot(this.x, this.y, this.z, axisX, axisY, axisZ);
        # 		final float l2 = Quaternion.len2(axisX * d, axisY * d, axisZ * d, this.w);
        # 		return MathUtils.isZero(l2) ? 0f : (float)(2.0 * Math.acos(MathUtils.clamp((float)((d < 0 ? -this.w : this.w) / Math.sqrt(l2)), -1f, 1f)));

        # x = self._orientation[0]
        # y = self._orientation[1]
        z = self._orientation[2]
        w = self._orientation[3]
        # dot = z
        l2 = z * z + w + w
        if abs(l2) < SimulationObjectWithPositionAndOrientation.FLOAT_ROUNDING_ERROR:
            return 0

        return math.acos(max(-1.0, min(1.0, (-w if z < 0.0 else w) / math.sqrt(l2)))) * 2.0 * 180.0 / math.pi

    def set_bearing(self, angle):
        self.set_bearing_rad(angle * math.pi / 180.0)

    def set_bearing_rad(self, rad):
        # 		float d = Vector3.len(x, y, z);
        # 		if (d == 0f) return idt();
        # 		d = 1f / d;
        # 		float l_ang = radians < 0 ? MathUtils.PI2 - (-radians % MathUtils.PI2) : radians % MathUtils.PI2;
        # 		float l_sin = (float)Math.sin(l_ang / 2);
        # 		float l_cos = (float)Math.cos(l_ang / 2);
        # 		return this.set(d * x * l_sin, d * y * l_sin, d * z * l_sin, l_cos).nor();
        # x = self._orientation[0]
        # y = self._orientation[1]

        d = 1.0
        pi2 = 2 * math.pi
        l_ang =  (pi2 - (-rad % pi2)) if rad < 0 else rad % pi2
        l_sin = math.sin(l_ang / 2)
        l_cos = math.cos(l_ang / 2)
        x = 0
        y = 0
        z = d * l_sin
        w = l_cos

        l = z * z + w * w
        # if (len != 0.f && !MathUtils.isEqual(len, 1f))

        if l != 0.0 and abs(1.0 - l) < SimulationObjectWithPositionAndOrientation.FLOAT_ROUNDING_ERROR:
            l = math.sqrt(l)
            w /= l
            z /= l

        self.set_orientation_4(x, y, z, w)

    @staticmethod
    def from_yaw_pitch_roll(yaw, pitch, roll):
        yaw = yaw * math.pi / 180.0
        pitch = pitch * math.pi / 180.0
        roll = roll * math.pi / 180.0

        hr = roll * 0.5
        shr = math.sin(hr)
        chr = math.cos(hr)
        hp = pitch * 0.5
        shp = math.sin(hp)
        chp = math.cos(hp)
        hy = yaw * 0.5
        shy = math.sin(hy)
        chy = math.cos(hy)
        chy_shp = chy * shp
        shy_chp = shy * chp
        chy_chp = chy * chp
        shy_shp = shy * shp

        x = (chy_shp * chr) + (shy_chp * shr) # cos(yaw / 2) * sin(pitch / 2) * cos(roll / 2) + sin(yaw / 2) * cos(pitch / 2) * sin(roll / 2)
        y = (shy_chp * chr) - (chy_shp * shr) # sin(yaw / 2) * cos(pitch / 2) * cos(roll / 2) - cos(yaw / 2) * sin(pitch / 2) * sin(roll / 2)
        z = (chy_chp * shr) - (shy_shp * chr) # cos(yaw / 2) * cos(pitch / 2) * sin(roll / 2) - sin(yaw / 2) * sin(pitch / 2) * cos(roll / 2)
        w = (chy_chp * chr) + (shy_shp * shr) # cos(yaw / 2) * cos(pitch / 2) * cos(roll / 2) + sin(yaw / 2) * sin(pitch / 2) * sin(roll / 2)
        return x, y, z, w

    def __repr__(self):
        return super(SimulationObjectWithPositionAndOrientation, self).__repr__() + ", b={:.1f}".format(self._get_bearing())

    def float_equal(self, a, b):
        return abs(a - b) < 0.001
