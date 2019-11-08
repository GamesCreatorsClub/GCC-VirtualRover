from piwarssim.engine.message.Message import Message


class ClientInternalMessage(Message):
    def __init__(self, factory, message_type):
        super(ClientInternalMessage, self).__init__(factory, message_type)
