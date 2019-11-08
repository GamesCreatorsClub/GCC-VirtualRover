from piwarssim.engine.message.Message import Message


class ChatMessage(Message):
    def __init__(self, factory, message_type):
        super(ChatMessage, self).__init__(factory, message_type)
