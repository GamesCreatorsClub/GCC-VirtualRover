from enum import Enum

from piwarssim.engine.message.Message import Message


class ClientInternalState(Enum):
    NoneState = ()
    RequestServerDetails = ()
    ClientReady = ()
    ClientLeaving = ()

    def __new__(cls):
        value = len(cls.__members__)
        obj = object.__new__(cls)
        obj._value_ = value
        return obj

    def ordinal(self):
        return self.value

    @staticmethod
    def from_ordinal(ordinal):
        for enum_obj in ClientInternalState:
            if enum_obj.value == ordinal:
                return enum_obj


class ClientInternalMessage(Message):
    def __init__(self, factory, message_type):
        super(ClientInternalMessage, self).__init__(factory, message_type)
        self._state = ClientInternalState.RequestServerDetails

    def free(self):
        super(ClientInternalMessage, self).free()
        self._state = ClientInternalState.RequestServerDetails

    def get_state(self):
        return self._state

    def set_state(self, state):
        self._state = state

    def size(self):
        return super(ClientInternalMessage, self).size() + 1

    def deserialize_impl(self, deserializer):
        # super(ClientInternalMessage, self).deserialize_impl(deserializer)
        self._state = ClientInternalState.from_ordinal(deserializer.deserialize_unsigned_byte())

    def serialize_impl(self, serializer):
        # super(ClientInternalMessage, self).serialize_impl(serializer)
        serializer.serialize_unsigned_byte(self._state.ordinal())


if __name__ == '__main__':
    # Simple test
    from piwarssim.engine.message.MessageCode import MessageCode
    from piwarssim.engine.transfer.ByteSerializer import ByteSerializer

    serializer = ByteSerializer(None)

    message = ClientInternalMessage(None, MessageCode.ClientInternal)
    message_received = ClientInternalMessage(None, MessageCode.ClientInternal)
    for state in ClientInternalState:

        serializer.setup()
        message.set_state(state)

        message.serialize(serializer)
        message_code = serializer.deserialize_unsigned_byte()
        message_received.deserialize(serializer)

        print(str(MessageCode.from_ordinal(message_code)) + ", state "+ str(message_received.get_state()) + "'s ordinal " + str(message_received.get_state().ordinal()))
