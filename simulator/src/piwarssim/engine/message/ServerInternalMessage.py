from piwarssim.engine.message.Message import Message


class ServerInternalMessage(Message):
    def __init__(self, factory, message_type):
        super(ServerInternalMessage, self).__init__(factory, message_type)

