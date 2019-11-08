from piwarssim.engine.message.GameObjectMessage import GameObjectMessage
# from piwarssim.engine.message.MessageCode import MessageCode


class ServerUpdateMessage(GameObjectMessage):
    def __init__(self, factory, message_type):
        super(ServerUpdateMessage, self).__init__(factory, message_type)
        self._position = [0, 0, 0]
        self._velocity = [0, 0, 0]
        self._orientation = [0, 0, 0, 1]
        self._turn_speed = 0
        self._health = 0
        self._server_frame_no = 0

    def get_position(self):
        return self._position

    def get_velocity(self):
        return self._velocity

    def get_health(self):
        return self._health

    def get_orientation(self):
        return self._orientation

    def get_turn_speed(self):
        return self._turn_speed

    def get_server_frame_no(self):
        return self._server_frame_no

    def set_values(self, object_id, server_frame_no, position, velocity, orientation, turn_speed, health):
        self.setup()
        self.set_id(object_id)

        self._server_frame_no = server_frame_no

        self._position[0] = position[0]
        self._position[1] = position[1]
        self._position[2] = position[2]

        self._velocity[0] = velocity[0]
        self._velocity[1] = velocity[1]
        self._velocity[2] = velocity[2]

        self._orientation[0] = orientation[0]
        self._orientation[1] = orientation[1]
        self._orientation[2] = orientation[2]
        self._orientation[3] = orientation[3]

        self._turn_speed = turn_speed
        self._health = health

    def deserialize_impl(self, deserializer):
        super(GameObjectMessage, self).deserialize_impl(deserializer)

        self._position[0] = deserializer.deserialize_float()
        self._position[1] = deserializer.deserialize_float()
        self._position[2] = deserializer.deserialize_float()

        self._velocity[0] = deserializer.deserialize_float()
        self._velocity[1] = deserializer.deserialize_float()
        self._velocity[2] = deserializer.deserialize_float()

        self._orientation[0] = deserializer.deserialize_float()
        self._orientation[1] = deserializer.deserialize_float()
        self._orientation[2] = deserializer.deserialize_float()
        self._orientation[3] = deserializer.deserialize_float()

        self._turn_speed = deserializer.deserialize_float()
        self._health = deserializer.deserialize_float()

        self._server_frame_no = deserializer.deserialize_unsigned_short()

    def serialize_impl(self, serializer):
        super(GameObjectMessage, self).serialize_impl(serializer)

        serializer.serialize_float(self._position[0])
        serializer.serialize_float(self._position[1])
        serializer.serialize_float(self._position[2])

        serializer.serialize_float(self._velocity[0])
        serializer.serialize_float(self._velocity[1])
        serializer.serialize_float(self._velocity[2])

        serializer.serialize_float(self._orientation[0])
        serializer.serialize_float(self._orientation[1])
        serializer.serialize_float(self._orientation[2])
        serializer.serialize_float(self._orientation[3])

        serializer.serialize_float(self._turn_speed)
        serializer.serialize_float(self._health)
        serializer.serialize_unsigned_short(self._server_frame_no)
