from fishtankclient.engine.factory import PooledFactory
from fishtankclient.engine.simulation import SimulationState


class SimulationStateFactory(PooledFactory):
    def __init__(self):
        super(SimulationStateFactory, self).__init__()

    def create_new(self):
        return SimulationState()

    def setup(self, o):
        pass
