from piwarssim.engine.factory import PooledFactory
from piwarssim.engine.simulation import SimulationState


class SimulationStateFactory(PooledFactory):
    def __init__(self):
        super(SimulationStateFactory, self).__init__()

    def create_new(self):
        return SimulationState()

    def setup(self, o):
        pass
