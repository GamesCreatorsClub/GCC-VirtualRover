
from fishtankclient.engine.factory import Factory


class PooledFactory(Factory):
    def __init__(self):
        super(PooledFactory, self).__init__()
        self._free = []
        self._max_instances = 0
        self._instances = 0

    def get_max_instances(self):
        return self._max_instances

    def set_max_instances(self, max_instances):
        self._max_instances = max_instances

    def obtain(self):
        if len(self._free) == 0:
            o = self.create_new()
        else:
            l = len(self._free)
            o = self._free[l - 1]
            del self._free[l - 1]

        return o

    def free(self, o):
        self._free.append(o)

    def create_new(self):
        raise NotImplemented

    def setup(self, o):
        pass
