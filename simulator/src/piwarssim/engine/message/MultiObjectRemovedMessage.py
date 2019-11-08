from piwarssim.engine.message.Message import Message


class MultiObjectRemovedMessage(Message):
    def __init__(self, factory, message_type):
        super(MultiObjectRemovedMessage, self).__init__(factory, message_type)
