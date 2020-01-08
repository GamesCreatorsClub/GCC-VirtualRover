
import time
import random
from datetime import time

from piwarssim.engine.challenges.AbstractChallenge import AbstractChallenge
from piwarssim.engine.simulation.PiWarsSimObjectTypes import PiWarsSimObjectTypes
from piwarssim.engine.simulation.rovers.AbstractRoverSimObject import AbstractRoverSimObject


class MineSweeperChallenge(AbstractChallenge):
    def __init__(self):
        super(MineSweeperChallenge, self).__init__("MineSweeper")
        self.camera_id = 0
        self.rover_id = 0
        self.mine_sweeper_status_id = 0
        self._next_event = 0

    def after_sim_object_added(self, sim_object):
        super(MineSweeperChallenge, self).after_sim_object_added(sim_object)
        if isinstance(sim_object, AbstractRoverSimObject):
            camera_attachment = self._sim_object_factory.obtain(PiWarsSimObjectTypes.CameraAttachment)
            camera_attachment.set_id(self.new_id())
            camera_attachment.attach_to_rover(sim_object)
            self.add_new_sim_object_immediately(camera_attachment)
            self.camera_id = camera_attachment.get_id()

            self.rover_id = sim_object.get_id()
            # reset_rover()

            mine_sweeper_state_object = self._sim_object_factory.obtain(PiWarsSimObjectTypes.MineSweeperStateObject)
            mine_sweeper_state_object.set_id(self.new_id())
            self.add_new_sim_object_immediately(mine_sweeper_state_object)
            mine_sweeper_state_object.set_state_bits(0)

    def process(self, timestamp):
        if timestamp > self._next_event:
            mine_sweeper_status = self.get_mine_sweeper_status()
            if mine_sweeper_status is not None:
                next_light = random.randint(0, 16)
                bit = 1 << next_light
                mine_sweeper_status.set_state_bits(bit)

                self._next_event = timestamp + 2

        super(MineSweeperChallenge, self).process(timestamp)

    def get_mine_sweeper_status(self):
        sim_state = self.get_current_sim_state()
        for game_object_id in sim_state:
            sim_object = sim_state[game_object_id]
            if sim_object.get_type() == PiWarsSimObjectTypes.MineSweeperStateObject:
                return sim_object
        return None
