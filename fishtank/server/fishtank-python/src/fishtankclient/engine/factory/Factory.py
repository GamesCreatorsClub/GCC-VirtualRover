
class Factory:
    def __init__(self):
        pass

    def obtain(self):
        raise NotImplemented

    def free(self, o):
        raise NotImplemented
