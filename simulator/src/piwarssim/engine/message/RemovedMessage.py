from piwarssim.engine.message.Message import Message


class RemovedMessage(Message):
    def __init__(self, factory, message_type):
        super(RemovedMessage, self).__init__(factory, message_type)

