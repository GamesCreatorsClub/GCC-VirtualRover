import random


class SimulationState(dict):
    def __init__(self):
        super(SimulationState, self).__init__()

        self._added_objects = []
        self._removed_objects = []
        self._frame_no = 0
        self._time = 0

    def get_frame_no(self):
        return self._frame_no

    def set_frame_no(self, frame_no):
        self._frame_no = frame_no

    def _next_game_state_frame_no(self):
        frame_no = self._frame_no + 1
        if frame_no >= 65535:
            frame_no = 1
        return frame_no

    def get_time(self):
        return self._time

    def set_time(self, sim_time):
        self._time = sim_time

    def add_new(self, sim_object):
        self[sim_object.get_id()] = sim_object
        self._added_objects.append(sim_object)

    def get_added(self):
        return self._added_objects

    def remove(self, sim_object):
        sim_object.remove()
        self._removed_objects.append(sim_object)

    def get_removed(self):
        return self._removed_objects

    def new_id(self):
        next_id = random.randrange(0, 32767)
        while next_id == 0 or next_id in self:
            next_id += 1
            if next_id > 32767:
                next_id = 1

        return next_id

    def clear(self):
        del self._added_objects[:]
        del self._removed_objects[:]
        for sim_object_id in self:
            sim_object = self[sim_object_id]
            if sim_object.get_last_sim_state() == self:
                sim_object.free()

    def copy_state(self, challenge):
        previous_sim_state = challenge.get_previous_sim_state()
        new_sim_state = challenge.new_sim_state(self._next_game_state_frame_no())
        for sim_object_id in self:
            sim_object = self[sim_object_id]
            if not sim_object.is_removed():
                new_sim_object = sim_object.copy(previous_sim_state)
                if not new_sim_object.is_added() and new_sim_object == sim_object and previous_sim_state is not None:
                    old_sim_object = previous_sim_state[new_sim_object.get_id()]
                    if old_sim_object is not None:
                        self[sim_object_id] = old_sim_object
                new_sim_state[sim_object_id] = new_sim_object
                if new_sim_object.get_id != sim_object_id:
                    raise ValueError("Failed to copy objects properly")

        return new_sim_state
