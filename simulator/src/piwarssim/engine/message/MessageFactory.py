
from piwarssim.engine.factory import TypedObjectFactory
from piwarssim.engine.message.MessageCode import MessageCode


class MessageFactory(TypedObjectFactory):
    def __init__(self):
        super(MessageFactory, self).__init__()

        for message_code in MessageCode:
            self.free_objects[message_code] = []

    def create_new_object(self, message_code):
        message_code.new_object(self)

    def create_message(self, deserializer):
        message_type = deserializer.deserialize_int()

        if message_type >= MessageCode.values().length or message_type < 0:
            return None  # TODO handle this nicely...

        message_type = MessageCode.values()[message_type]

        message = self.obtain(message_type)
        message.deserialize(deserializer)

        return message

    def new_message(self, message_type):
        message = self.obtain(message_type)
        message.setup()
        return message

    def server_update_command(self, object_id, server_frame_no, position, velocity, orientation, turn_speed, health):
        server_update_command = self.new_message(MessageCode.ServerUpdate)
        server_update_command.set_values(object_id, server_frame_no, position, velocity, orientation, turn_speed, health)
        return server_update_command
