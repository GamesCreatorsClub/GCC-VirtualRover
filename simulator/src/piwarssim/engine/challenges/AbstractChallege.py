
class AbstractChallenge:
    def __init__(self):
        self.engine_object_factory = None
        self.next_engine_state = None

    def create_engine_factory(self):
        return None

    def process(self, timestamp):
        pass

    def check_for_collision(self, object, objects):
        pass

    def get_frame_id(self):
        pass

    def remove_engine_object(self, id):
        pass

    def add_new_engine_object(self, engine_object):
        pass

    def get_engine_state(self, frame_id):
        pass

    def get_previous_engine_state(self):
        pass

    def get_current_engine_state(self):
        pass

    def new_engine_state(self, frame_id):
        pass

    def release_engine_state(self, engine_state):
        pass

    def process_command(self, command):
        pass

