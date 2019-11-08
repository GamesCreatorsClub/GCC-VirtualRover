from piwarssim.engine import EngineObjectWithPosition, EngineObject


class EngineObjectWithPositionAndOrientation(EngineObjectWithPosition):
    def __init__(self, factory, id):
        super(EngineObjectWithPositionAndOrientation, self).__init__(factory, id)
        self._position = [0, 0, 0]
        self._velocity = [0, 0, 0]

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
        self.changed = self.changed or self._position[0] != x or self._position[1] != y or self._position[1] != z
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
        return message_factory.server_update_command(self._id, server_frame_no, self._position, self._velocity, EngineObject.QUATERNION_FORWARD, 0, 0)

    def serialize(self, full, serializer):
        super(EngineObjectWithPositionAndOrientation, self).serialize(full, serializer)
        serializer.serialize_float(self._position[0])
        serializer.serialize_float(self._position[1])
        serializer.serialize_float(self._position[2])

        serializer.serialize_float(self._velocity[0])
        serializer.serialize_float(self._velocity[1])
        serializer.serialize_float(self._velocity[2])

    def deserialize(self, full, serializer):
        super(EngineObjectWithPositionAndOrientation, self).deserialize(full, serializer)
        self._position[0] = serializer.deserialize_float()
        self._position[1] = serializer.deserialize_float()
        self._position[2] = serializer.deserialize_float()

        self._velocity[0] = serializer.deserialize_float()
        self._velocity[1] = serializer.deserialize_float()
        self._velocity[2] = serializer.deserialize_float()

    def copy_internal(self, new_object):
        super(EngineObjectWithPositionAndOrientation, self).copy_internal(new_object)

        new_object._position[0] = self._position[0]
        new_object._position[1] = self._position[1]
        new_object._position[2] = self._position[2]

        new_object._velocity[0] = self._velocity[0]
        new_object._velocity[1] = self._velocity[1]
        new_object._velocity[2] = self._velocity[2]

        return new_object
