from fishtankclient.engine.message.ClientAuthenticationDetailsMessage import ClientAuthenticationDetailsMessage


class ClientAuthenticateMessage(ClientAuthenticationDetailsMessage):
    def __init__(self, factory, message_type):
        super(ClientAuthenticateMessage, self).__init__(factory, message_type)

    def deserialize_impl(self, deserializer):
        super().deserialize_impl(deserializer)

    def serialize_impl(self, serializer):
        super().serialize_impl(serializer)
