import argparse
import math
import os
import time

from importlib import import_module
from threading import Thread

import pygame
import pymunk
import pymunk.pygame_util

from lib.robot import Robot
from piwarssim.engine.message import MessageFactory
from piwarssim.engine.server import ServerEngine
from piwarssim.engine.simulation.objects.BarrelSimObject import BarrelColour
from piwarssim.engine.simulation.rovers.RoverType import RoverType
from piwarssim.engine.transfer.ByteSerializerFactory import ByteSerializerFactory
from piwarssim.engine.transfer.TCPServerModule import TCPServerModule
from piwarssim.engine.transfer.UDPServerModule import UDPServerModule
from piwarssim.visualisation.Visualisation import Visualisation
from worlds.eco_disaster import BarrelBody


class WorldRunner:
    def __init__(self):
        self.space = pymunk.Space()
        self.screen = None
        self.draw_options = None
        self.running_behaviour = None
        self.robot = None
        self.world = None
        self.surface = None
        self._is_connected_method = None
        self._server_engine = None
        self._sim_rover = None
        self._tick = 0
        self._comm_server_module = None
        self._visualiser = None

    def update(self):
        # player_inputs = server_engine.get_player_inputs()
        # player_inputs_array = player_inputs.get_inputs()
        # if len(player_inputs_array) > 0:
        #     player_input = player_inputs_array[0]
        #     paused = player_input.circle()
        #     # print(str(player_inputs_array[0]))
        #
        if self._is_connected_method() and self._server_engine.is_client_ready():
            try:
                next(self.running_behaviour)
            except StopIteration:
                pass
            self.robot.update(self.space)
            self.space.step(0.4)

            world_width = self.world.get_width()
            world_height = self.world.get_height()

            self._sim_rover.set_position_2(self.robot.body.position.x - world_width // 2, world_height // 2 - self.robot.body.position.y)
            self._sim_rover.set_bearing(270 - self.robot.body.angle * 180 / math.pi)
            self._sim_rover.changed = False

            for object in self.space.bodies:
                if isinstance(object, BarrelBody):
                    barrel_body = object
                    local_object = barrel_body.get_local_object()
                    if local_object is None:
                        if barrel_body.is_green():
                            local_object = self._server_engine.challenge.make_barrel(BarrelColour.Green)
                        else:
                            local_object = self._server_engine.challenge.make_barrel(BarrelColour.Red)
                        barrel_body.set_local_object(local_object)
                    # local_object.set_position_2(1000 - barrel_body.position.x * 2.5, barrel_body.position.y * 2.5 - 1000)
                    local_object.set_position_2(barrel_body.position.x - world_width // 2, world_height // 2 - barrel_body.position.y)

            self._server_engine.process(self._tick)
            self._server_engine.send_update()

            self._tick += 0.4

    def draw(self):
        # self.screen.fill((1.0, 0, 0))
        self.surface.fill((1.0, 0, 0))
        self.space.debug_draw(self.draw_options)
        self.robot.draw(self.surface)
        # self.robot.draw(self.screen)
        self.screen.blit(pygame.transform.scale(self.surface, (self.screen.get_width(), self.screen.get_height())), (0, 0))
        pygame.display.flip()

    def main(self):
        parser = argparse.ArgumentParser(description='World runner')
        group = parser.add_mutually_exclusive_group(required=True)
        group.add_argument('--tcp', action='store_true', help='use TCP to connect to UI')
        group.add_argument('--udp', action='store_false', help='use UDP to connect to UI')
        parser.add_argument('-b', '--behaviour', dest='behaviour_module', help='behaviour module')
        parser.add_argument('-w', '--world', '--challenge', dest='world_module', help='world (challenge) module')
        args = parser.parse_args()

        os.environ['SDL_VIDEO_WINDOW_POS'] = "%d,%d" % (800, 60)
        pygame.init()
        pymunk.pygame_util.positive_y_is_up = False

        behaviour_module = import_module("behaviours." + args.behaviour_module)
        world_module = import_module("worlds." + args.world_module)

        self.robot = Robot()

        self.space.damping = 0.2
        self.space.add(self.robot.body, self.robot.shape)

        self.running_behaviour = behaviour_module.Behaviour(self.robot.controls).run()
        self.world = world_module.World(self.space, self.robot)

        self._server_engine = ServerEngine(self.world.get_challenge())
        self._sim_rover = self._server_engine.challenge.spawn_rover(RoverType.GCC)

        self._server_engine.process(self._tick)

        serializer_factory = ByteSerializerFactory()
        message_factory = MessageFactory()

        if args.tcp:
            self._comm_server_module = TCPServerModule(self._server_engine, serializer_factory, message_factory)
        else:
            self._comm_server_module = UDPServerModule(self._server_engine, serializer_factory, message_factory)

        self._is_connected_method = self._comm_server_module.is_connected

        thread = Thread(target=self._comm_server_module.process, daemon=True)
        thread.start()

        self.surface = pygame.Surface((self.world.get_width(), self.world.get_height()))
        self.screen = pygame.display.set_mode((800, 800))
        self.draw_options = pymunk.pygame_util.DrawOptions(self.surface)

        time.sleep(0.2)

        self._visualiser = Visualisation()
        self._visualiser.start()

        running = True
        while running:
            self.world.update(self.screen.get_width(), self.screen.get_height())
            self.update()
            self.draw()
            for event in pygame.event.get():
                if event.type == pygame.QUIT:
                    running = False


if __name__ == '__main__':

    runner = WorldRunner()
    runner.main()
