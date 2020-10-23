from enum import Enum

from piwarssim.engine.message.Message import Message


class ServerInternalState(Enum):
    NoneState = ()
    AuthenticateSuccessful = ()
    AuthenticateFailed = ()
    RegistrationSuccessful = ()
    RegistrationFailed = ()
    SessionClosed = ()
    RegistrationServerURL = ()

    def __new__(cls):
        value = len(cls.__members__)
        obj = object.__new__(cls)
        obj._value_ = value
        return obj

    def ordinal(self):
        return self.value

    @staticmethod
    def from_ordinal(ordinal):
        for enum_obj in ServerInternalState:
            if enum_obj.value == ordinal:
                return enum_obj


class ServerInternalMessage(Message):
    def __init__(self, factory, message_type):
        super(ServerInternalMessage, self).__init__(factory, message_type)
        self._state = None
        self._message = None

    def free(self):
        super(ServerInternalMessage, self).free()
        self._state = None
        self._message = None

    def get_state(self):
        return self._state

    def set_state(self, state):
        self._state = state

    def get_message(self):
        return self._message

    def set_message(self, message):
        self._message = message

    def size(self):
        return super(ServerInternalMessage, self).size() + 1 + 2 + (len(self._message) if self._message is not None else 0)

    def deserialize_impl(self, deserializer):
        # super(ServerInternalMessage, self).deserialize_impl(deserializer)
        self._state = ServerInternalState.from_ordinal(deserializer.deserialize_unsigned_byte())
        self._message = deserializer.deserialize_string()

    def serialize_impl(self, serializer):
        # super(ServerInternalMessage, self).serialize_impl(serializer)
        serializer.serialize_unsigned_byte(self._state.ordinal())
        serializer.serialize_string(self._message)


if __name__ == '__main__':
    # Simple test
    from piwarssim.engine.message.MessageCode import MessageCode
    from piwarssim.engine.transfer.ByteSerializer import ByteSerializer

    serializer = ByteSerializer(None)

    message = ServerInternalMessage(None, MessageCode.ServerInternal)
    message_received = ServerInternalMessage(None, MessageCode.ServerInternal)
    for state in ServerInternalState:

        serializer.setup()
        message.set_state(state)
        message.set_message("MSG:" + str(state))

        message.serialize(serializer)
        message_code = serializer.deserialize_int()
        message_received.deserialize(serializer)

        print(str(MessageCode.from_ordinal(message_code)) + ", state "+ str(message_received.get_state()) + "'s ordinal "
              + str(message_received.get_state().ordinal()) + "; msg='" + message_received.get_message() + "'")
