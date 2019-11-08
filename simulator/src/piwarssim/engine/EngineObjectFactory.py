
from piwarssim.engine.factory import TypedObjectFactory
from piwarssim.engine.transfer import SizeCalculationSerializer


class EngineObjectFactory(TypedObjectFactory):
    def __init__(self):
        super(EngineObjectFactory, self).__init__()

        self._full_object_sizes = {}
        self._update_object_sizes = {}
        self._all_types = {}

    def init(self):
        self.collect_types()
        for t in self._all_types:
            self.free_objects[t] = []
        self.calculate_sizes()

    def collect_types(self):
        raise NotImplemented

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
