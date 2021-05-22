
class TypedObject:
    def __init__(self, factory):
        self.factory = factory
        self.type = None

    def get_type(self):
        raise NotImplemented

    def free(self):
        self.factory.free(self)