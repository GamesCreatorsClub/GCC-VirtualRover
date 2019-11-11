from piwarssim.engine.simulation import SimulationObject


class SimulationObjectWithPosition(SimulationObject):
    def __init__(self, factory, sim_object_id, sim_object_type):
        super(SimulationObjectWithPosition, self).__init__(factory, sim_object_id, sim_object_type)
        self._position = [0.0, 0.0, 0.0]
        self._velocity = [0.0, 0.0, 0.0]

    def get_position(self):
        return self._position

    def set_position_v(self, position):
        self.changed = self.changed or self._position[0] != position[0] or self._position[1] != position[1] or self._position[2] != position[2]
        self._position[0] = position[0]
        self._position[1] = position[1]
        self._position[2] = position[2]

    def set_position_2(self, x, y):
        self.changed = self.changed or self._position[0] != x or self._position[1] != y
        self._position[0] = x
        self._position[1] = y

    def set_position_3(self, x, y, z):
        self.changed = self.changed or self._position[0] != x or self._position[1] != y or self._position[2] != z
        self._position[0] = x
        self._position[1] = y
        self._position[2] = z

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
        super(SimulationObjectWithPosition, self).serialize(full, serializer)
        serializer.serialize_float(self._position[0])
        serializer.serialize_float(self._position[1])
        serializer.serialize_float(self._position[2])

        serializer.serialize_float(self._velocity[0])
        serializer.serialize_float(self._velocity[1])
        serializer.serialize_float(self._velocity[2])

    def deserialize(self, full, serializer):
        super(SimulationObjectWithPosition, self).deserialize(full, serializer)
        self._position[0] = serializer.deserialize_float()
        self._position[1] = serializer.deserialize_float()
        self._position[2] = serializer.deserialize_float()

        self._velocity[0] = serializer.deserialize_float()
        self._velocity[1] = serializer.deserialize_float()
        self._velocity[2] = serializer.deserialize_float()

    def copy_internal(self, new_object):
        super(SimulationObjectWithPosition, self).copy_internal(new_object)

        new_object.set_position_v(self._position)
        new_object.set_velocity_v(self._velocity)

        return new_object

    def __repr__(self):
        return super(SimulationObjectWithPosition, self).__repr__() + ", p=({:.2f}, {:.2f}, {:.2f})".format(self._position[0], self._position[1], self._position[2])