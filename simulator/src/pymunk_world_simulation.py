import math
import pygame
import pymunk
import pymunk.pygame_util

from importlib import import_module

from lib.robot import Robot

from piwarssim import BaseSimulationAdapter
from piwarssim.engine.simulation.objects import BarrelColour
from simulation_runner import SimulationRunner
from worlds.eco_disaster import BarrelBody


class PymunkWorldSimulationAdapter(BaseSimulationAdapter):
    def __init__(self):
        super(PymunkWorldSimulationAdapter, self).__init__()
        self.space = pymunk.Space()
        self._draw_options = None
        self.running_behaviour = None
        self.robot = None
        self.world = None
        self._surface = None

    def define_arguments(self, args_parser):
        args_parser.add_argument('-b', '--behaviour', dest='behaviour_module', help='behaviour module')
        args_parser.add_argument('-w', '--world', dest='world_module', help='world (challenge) module')

    def process_arguments(self, args):
        self.simulation_runner.set_delta_tick(0.02)

        behaviour_module = import_module("behaviours." + args.behaviour_module)
        world_module = import_module("worlds." + args.world_module)

        self.robot = Robot()

        self.space.damping = 0.2
        self.space.add(self.robot.body, self.robot.shape)

        self.running_behaviour = behaviour_module.Behaviour(self.robot.controls).run()
        self.world = world_module.World(self.space, self.robot)
        self._surface = pygame.Surface((self.world.get_width(), self.world.get_height()))
        self._draw_options = pymunk.pygame_util.DrawOptions(self._surface)

    def get_challenge_name(self):
        return self.world.get_challenge_name()

    def update(self):
        try:
            next(self.running_behaviour)
        except StopIteration:
            pass
        self.robot.update(self.space)
        self.space.step(0.4)

        world_width = self.world.get_width()
        world_height = self.world.get_height()

        sim_rover = self.challenge.get_sim_object(self.sim_rover_id)

        sim_rover.set_position_2(self.robot.body.position.x - world_width // 2, world_height // 2 - self.robot.body.position.y)
        sim_rover.set_bearing(270 - self.robot.body.angle * 180 / math.pi)
        sim_rover.changed = False

        for body in self.space.bodies:
            if isinstance(body, BarrelBody):
                barrel_body = body
                local_object_id = barrel_body.get_local_object()
                if local_object_id is None:
                    if barrel_body.is_green():
                        local_object = self.challenge.make_barrel(BarrelColour.Green)
                    else:
                        local_object = self.challenge.make_barrel(BarrelColour.Red)
                    local_object_id = local_object.get_id()
                    barrel_body.set_local_object(local_object_id)
                else:
                    local_object = self.challenge.get_sim_object(local_object_id)

                local_object.set_position_2(barrel_body.position.x - world_width // 2, world_height // 2 - barrel_body.position.y)

        self.server_engine.process(self.simulation_runner.get_timestamp())
        self.server_engine.send_update()

    def draw(self, screen, screen_world_rect):
        self.world.update(screen_world_rect)

        self._surface.fill((1.0, 0, 0))
        self.space.debug_draw(self._draw_options)
        self.robot.draw(self._surface)
        screen.blit(pygame.transform.scale(self._surface, (screen_world_rect.width, screen_world_rect.height)), (screen_world_rect.x, screen_world_rect.y))


if __name__ == '__main__':

    runner = SimulationRunner(PymunkWorldSimulationAdapter())
    runner.main()
