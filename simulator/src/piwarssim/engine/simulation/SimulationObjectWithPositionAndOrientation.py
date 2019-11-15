import math

from piwarssim.engine.simulation import SimulationObjectWithPosition, SimulationObject


class SimulationObjectWithPositionAndOrientation(SimulationObjectWithPosition):
    FLOAT_ROUNDING_ERROR = 0.000001

    def __init__(self, factory, sim_object_id, sim_object_type):
        super(SimulationObjectWithPositionAndOrientation, self).__init__(factory, sim_object_id, sim_object_type)
        self._orientation = [0.0, 0.0, 0.0, 1.0]
        self._turn_speed = 0

    def get_orientation(self):
        return self._orientation

    def set_orientation_q(self, orientation):
        self.changed = self.changed \
                       or self._orientation[0] != orientation[0] \
                       or self._orientation[1] != orientation[1] \
                       or self._orientation[2] != orientation[2] \
                       or self._orientation[3] != orientation[3]
        self._orientation[0] = orientation[0]
        self._orientation[1] = orientation[1]
        self._orientation[2] = orientation[2]
        self._orientation[3] = orientation[3]

    def set_orientation_4(self, x, y, z, w):
        self.changed = self.changed or self._orientation[0] != x or self._orientation[1] != y or self._orientation[2] != z or self._orientation[3] != w
        self._orientation[0] = x
        self._orientation[1] = y
        self._orientation[2] = z
        self._orientation[3] = w

    def get_turn_speed(self):
        return self._turn_speed

    def set_turn_speed(self, turn_speed):
        self._turn_speed = turn_speed

    def perform_command(self, command):
        # This is needed only for clients
        # if isinstance(command, ServerUpdateMessage):
        #     position = command.get_position()
        #     velocity = command.get_velocity()
        #     self.set_position_v(position)
        #     self._velocity[0] = velocity[0]
        #     self._velocity[1] = velocity[1]
        #     self._velocity[2] = velocity[2]
        pass

    def server_update_message(self, server_frame_no, message_factory):
        # This is needed only for clients
        return message_factory.server_update_command(self._id, server_frame_no, self._position, self._velocity, SimulationObject.QUATERNION_FORWARD, 0, 0)

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

        serializer.serialize_float(self._turn_speed)

    def deserialize(self, full, serializer):
        super(SimulationObjectWithPositionAndOrientation, self).deserialize(full, serializer)
        self._orientation[0] = serializer.deserialize_float()
        self._orientation[1] = serializer.deserialize_float()
        self._orientation[2] = serializer.deserialize_float()

        self._orientation[0] = math.sqrt(1.0
                                         - self._orientation[0] * self._orientation[0]
                                         - self._orientation[1] * self._orientation[1]
                                         - self._orientation[2] * self._orientation[2])

        self._turn_speed = serializer.deserialize_float()

    def copy_internal(self, new_object):
        super(SimulationObjectWithPositionAndOrientation, self).copy_internal(new_object)

        new_object.set_orientation_q(self._orientation)
        new_object.set_turn_speed(self._turn_speed)

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
        r = angle * math.pi / 180
        pi2 = 2 * math.pi
        l_ang =  (pi2 - (-r % pi2)) if r < 0 else r % pi2
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

        self._orientation[0] = x
        self._orientation[1] = y
        self._orientation[2] = z
        self._orientation[3] = w

    def __repr__(self):
        return super(SimulationObjectWithPositionAndOrientation, self).__repr__() + ", b={:.1f}".format(self._get_bearing())
