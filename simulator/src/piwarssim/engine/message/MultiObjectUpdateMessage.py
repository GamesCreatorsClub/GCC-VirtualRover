from piwarssim.engine.message.Message import Message


class MultiObjectUpdateMessage(Message):
    def __init__(self, factory, message_type):
        super(MultiObjectUpdateMessage, self).__init__(factory, message_type)
