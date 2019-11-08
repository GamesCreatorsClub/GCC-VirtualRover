from piwarssim.engine.message.Message import Message


class PlayerDetailsMessage(Message):
    def __init__(self, factory, message_type):
        super(PlayerDetailsMessage, self).__init__(factory, message_type)
