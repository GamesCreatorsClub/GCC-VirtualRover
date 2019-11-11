from piwarssim.engine.message.ClientAuthenticationDetailsMessage import ClientAuthenticationDetailsMessage


class ClientAuthenticateMessage(ClientAuthenticationDetailsMessage):
    def __init__(self, factory, message_type):
        super(ClientAuthenticateMessage, self).__init__(factory, message_type)

    def deserialize_impl(self, deserializer):
        pass

    def serialize_impl(self, serializer):
        pass
