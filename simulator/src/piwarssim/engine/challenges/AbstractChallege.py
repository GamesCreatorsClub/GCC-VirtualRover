from piwarssim.engine.simulation.PiWarsSimObjectTypes import PiWarsSimObjectTypes
from piwarssim.engine.simulation.SimulationStateFactory import SimulationStateFactory
from piwarssim.engine.simulation.SimulationObjectFactory import SimulationObjectFactory


class AbstractChallenge:
    def __init__(self, challenge_id):
        self._challenge_id = challenge_id
        self._sim_object_factory = SimulationObjectFactory()
        self._sim_state_factory = SimulationStateFactory()
        self._next_sim_state = self._sim_state_factory.obtain()
        self._next_sim_state.set_frame_no(1)
        self._previous_sim_states = []
        self._new_sim_objects = []
        self._remove_sim_object = []

        self._previous_sim_states.append(self._next_sim_state)
        self._next_sim_state = self._next_sim_state.copy_state(self)

    def get_challenge_id(self):
        return self._challenge_id

    def get_frame_id(self):
        if self._next_sim_state is not None:
            return self._next_sim_state.get_frame_no()

    def remove_sim_object(self, object_id):
        self._remove_sim_object.append(object_id)

    def add_new_sim_object(self, sim_object):
        self._new_sim_objects.append(sim_object)

    def contains_sim_object(self, sim_object_id):
        return sim_object_id in self._next_sim_state

    def get_sim_state(self, frame_id):
        first_frame_no = self._previous_sim_states[0].get_frame_no()
        last_frame_no = self._previous_sim_states[-1].get_frame_no()
        if first_frame_no <= frame_id <= last_frame_no:
            return self._previous_sim_states[frame_id - first_frame_no]

        return None

    def get_previous_sim_state(self):
        return self._previous_sim_states[-1]

    def get_current_sim_state(self):
        return self._next_sim_state

    def new_sim_state(self, frame_id):
        new_sim_state = self._sim_state_factory.obtain()
        new_sim_state.set_frame_no(frame_id)
        # new_sim_state.set_time(sim_time)  # TODO - this needs to be sorted eventually
        return new_sim_state

    def process_command(self, command):
        pass

    def process(self, timestamp):
        for new_sim_object in self._new_sim_objects:
            self._next_sim_state.add_new(new_sim_object)

        # del self._new_sim_objects[:]

        current_sim_state = self.get_current_sim_state()

        self._previous_sim_states.append(current_sim_state)
        if len(self._previous_sim_states) > 10:
            del self._previous_sim_states[0]

        new_sim_state = current_sim_state.copy_state(self)
        self._next_sim_state = new_sim_state
        return new_sim_state

    def check_for_collision(self, sim_object, objects):
        pass

    def new_id(self):
        return self._next_sim_state.new_id()

    def spawn_rover(self, rover_type):
        rover = self._sim_object_factory.obtain(PiWarsSimObjectTypes.GCCRover)
        # rover.set_id(self.new_id())
        rover.set_id(1)
        self.add_new_sim_object(rover)
        return rover

    def make_barrel(self, barrel_colour):
        barrel = self._sim_object_factory.obtain(PiWarsSimObjectTypes.BarrelObject)
        barrel.set_id(self._next_sim_state.new_id())
        barrel.set_barrel_colour(barrel_colour)
        self.add_new_sim_object(barrel)
        return barrel
