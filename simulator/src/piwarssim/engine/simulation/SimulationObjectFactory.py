
from piwarssim.engine.factory.TypedObjectFactory import TypedObjectFactory
from piwarssim.engine.simulation.PiWarsSimObjectTypes import PiWarsSimObjectTypes
from piwarssim.engine.transfer.SizeCalculationSerializer import SizeCalculationSerializer


class SimulationObjectFactory(TypedObjectFactory):
    def __init__(self):
        super(SimulationObjectFactory, self).__init__()

        self._full_object_sizes = {}
        self._update_object_sizes = {}
        self._all_types = []

    # def init(self):
        self.collect_types()
        for object_type in self._all_types:
            self.free_objects[object_type] = []
        self.calculate_sizes()

    def collect_types(self):
        for object_type in PiWarsSimObjectTypes:
            self._all_types.append(object_type)

    def calculate_sizes(self):
        for object_type in self._all_types:
            serialiser = SizeCalculationSerializer()

            new_object = self.create_new_object(object_type)
            serialiser.free()
            new_object.serialize(True, serialiser)
            self._full_object_sizes[object_type] = serialiser.get_total_size()

            serialiser.free()
            new_object.serialize(False, serialiser)
            self._update_object_sizes[object_type] = serialiser.get_total_size()

    # TODO do we really want this?
    def create_new_object(self, object_type):
        return object_type.new_object(self, 0)

    def create_rover(self, object_id, rover_type):
        rover = self.obtain(PiWarsSimObjectTypes.Rover)
        rover.set_rover_type(rover_type)

    def full_object_serialized_size(self, sim_object_type):
        return self._full_object_sizes[sim_object_type]

    def update_object_serialized_size(self, sim_object_type):
        return self._update_object_sizes[sim_object_type]
