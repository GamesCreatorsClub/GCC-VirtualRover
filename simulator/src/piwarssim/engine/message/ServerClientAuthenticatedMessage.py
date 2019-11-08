from piwarssim.engine.message.Message import Message


class ServerClientAuthenticatedMessage(Message):
    def __init__(self, factory, message_type):
        super(ServerClientAuthenticatedMessage, self).__init__(factory, message_type)

