from piwarssim.engine.message.Message import Message


class ClientAuthenticateMessage(Message):
    def __init__(self, factory, message_type):
        super(ClientAuthenticateMessage, self).__init__(factory, message_type)
