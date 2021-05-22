from fishtankclient.engine.factory import PooledFactory
from fishtankclient.engine.transfer import ByteSerializer


class ByteSerializerFactory(PooledFactory):
    def __init__(self):
        super(ByteSerializerFactory, self).__init__()

    def create_new(self):
        return ByteSerializer(self)

    def setup(self, byte_serializer):
        byte_serializer.setup()
