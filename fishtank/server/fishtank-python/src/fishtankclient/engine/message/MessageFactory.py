
from fishtankclient.engine.factory import TypedObjectFactory
from fishtankclient.engine.message.MessageCode import MessageCode


class MessageFactory(TypedObjectFactory):
    def __init__(self):
        super(MessageFactory, self).__init__()

        for message_code in MessageCode:
            self.free_objects[message_code] = []

    def create_new_object(self, message_code):
        return message_code.new_object(self)

    def create_message(self, deserializer):
        message_type_code = deserializer.deserialize_unsigned_byte()

        message_type = MessageCode.from_ordinal(message_type_code)
        if message_type is None:
            return None

        message = self.obtain(message_type)
        message.deserialize(deserializer)

        return message
