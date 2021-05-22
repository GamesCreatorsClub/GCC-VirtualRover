
class TypedObjectFactory:
    def __init__(self):
        self.free_objects = {}

    def create_new_object(self, object_type):
        raise NotImplemented

    def obtain(self, object_type):
        types_list = self.free_objects[object_type]

        l = len(types_list)
        if l > 0:
            o = types_list[l - 1]
            del types_list[l - 1]
        else:
            o = self.create_new_object(object_type)
        return o

    def free(self, o):
        self.free_objects[o.get_type()].append(o)
