from fishtankclient.engine.message.ClientAuthenticationDetailsMessage import ClientAuthenticationDetailsMessage


class ClientRegisterMessage(ClientAuthenticationDetailsMessage):
    def __init__(self, factory, message_type):
        super(ClientRegisterMessage, self).__init__(factory, message_type)
        self._email = None

    def free(self):
        super(ClientRegisterMessage, self).free()
        self._email = None

    def get_email(self):
        return self._email

    def set_values_with_email(self, username, passhash, email):
        super(ClientRegisterMessage, self).set_values(username, passhash)
        self._email = email

    def size(self):
        return super(ClientRegisterMessage, self).size() + 2 + (len(self._email) if self._email is not None else 0)

    def deserialize_impl(self, deserializer):
        super(ClientRegisterMessage, self).deserialize_impl(deserializer)
        self._email = deserializer.deserialize_string()

    def serialize_impl(self, serializer):
        super(ClientRegisterMessage, self).serialize_impl(serializer)
        serializer.serialize_string(self._email)
