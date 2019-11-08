from piwarssim.engine.message.Message import Message


class ClientRegisterMessage(Message):
    def __init__(self, factory, message_type):
        super(ClientRegisterMessage, self).__init__(factory, message_type)
