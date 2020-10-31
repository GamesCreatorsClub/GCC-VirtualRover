import math
import pygame
import pymunk
import pymunk.pygame_util

from importlib import import_module

from lib.robot import Robot

from piwarssim import BaseSimulationAdapter
from simulation_runner import SimulationRunner
from worlds.abstract_world import PymunkBody


class PymunkWorldSimulationAdapter(BaseSimulationAdapter):
    def __init__(self):
        super(PymunkWorldSimulationAdapter, self).__init__()
        self.space = None
        self._draw_options = None
        self._behaviour_module = None
        self.running_behaviour = None
        self._world_module = None
        self.world = None
        self._surface = None

    def define_arguments(self, args_parser):
        args_parser.add_argument('-b', '--behaviour', dest='behaviour_module', help='behaviour module')
        args_parser.add_argument('-w', '--world', dest='world_module', help='world (challenge) module')

    def set_server_engine(self, server_engine):
        super(PymunkWorldSimulationAdapter, self).set_server_engine(server_engine)

    def process_arguments(self, args):
        super(PymunkWorldSimulationAdapter, self).process_arguments(args)

        self.simulation_runner.set_delta_tick(0.02)

        pymunk.pygame_util.positive_y_is_up = False

        self._behaviour_module = import_module("behaviours." + args.behaviour_module)
        self._world_module = import_module("worlds." + args.world_module)

        self.world = self._world_module.World()
        self.space = self.world.space

    def init(self):
        self.world.synchronise_challenge(self.challenge)

        size = (self.world.get_width(), self.world.get_length())
        self._surface = pygame.Surface(size, pygame.SRCALPHA)
        self._draw_options = pymunk.pygame_util.DrawOptions(self._surface)

        self.running_behaviour = self._behaviour_module.Behaviour(self.world.robot.controls).run()

    def get_challenge_name(self):
        return self.world.get_challenge_name()

    def update(self, timestamp, delta):
        '''
        This method is synchronising values from pymunk physics engine to 'challenge' object (the world in piwarssim 'package'
        which is used for UI and reading camera input)
        :param timestamp: absolute time
        :param delta: delta time passed since last call to this method
        :return:
        '''
        super(PymunkWorldSimulationAdapter, self).update(timestamp, delta)

        try:
            next(self.running_behaviour)
        except StopIteration:
            pass

        world_width = self.world.get_width()
        world_height = self.world.get_length()

        # Synchronise from Pymunk world to Simulation
        for body in self.space.bodies:
            if isinstance(body, Robot):
                sim_rover = self.challenge.get_sim_object(body.sim_rover_id)
                sim_rover.set_position_v(self.world.translate_to_sim(body.position.x, body.position.y))
                sim_rover.set_bearing(270 - body.angle * 180 / math.pi)
                sim_rover.changed = False
                body.update(self.space)
            elif isinstance(body, PymunkBody):
                local_object_id = body.get_local_object()
                if local_object_id is None:
                    # TODO - add to_sim_body method on AbstractWorld for subclasses to implement
                    # self.world.to_pymunk_body()
                    # if barrel_body.is_green():
                    #     local_object = self.challenge.make_barrel(BarrelColour.Green)
                    # else:
                    #     local_object = self.challenge.make_barrel(BarrelColour.Red)
                    # local_object_id = local_object.get_id()
                    # barrel_body.set_local_object(local_object_id)
                    raise Exception("Non implemented")
                else:
                    local_object = self.challenge.get_sim_object(local_object_id)

                # local_object.set_position_v2(body.position.x - world_width // 2, world_height // 2 - body.position.y)
                local_object.set_position_and_bearing_rad(body.position.x - world_width // 2, world_height // 2 - body.position.y, 0, body.angle)

        self.space.step(delta)
        self.server_engine.process(timestamp)
        self.server_engine.send_update()

    def draw(self, screen, screen_world_rect):
        super(PymunkWorldSimulationAdapter, self).draw(screen, screen_world_rect)
        self.world.update(screen_world_rect)

        self._surface.fill((255, 255, 255, 128))
        self.world.draw(self._surface)
        self.space.debug_draw(self._draw_options)
        screen.blit(pygame.transform.scale(self._surface, (screen_world_rect.width, screen_world_rect.height)), (screen_world_rect.x, screen_world_rect.y))


if __name__ == '__main__':

    runner = SimulationRunner(PymunkWorldSimulationAdapter())
    runner.init()
    runner.main()
