import argparse
import math
import os
import time
from PIL import Image

from importlib import import_module
from threading import Thread

import pygame
import pymunk
import pymunk.pygame_util

from lib.robot import Robot
from piwarssim.engine.message import MessageFactory
from piwarssim.engine.message.MessageCode import MessageCode
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
        self._font = None
        self._is_connected_method = None
        self._message_factory = None
        self._server_engine = None
        self._sim_rover_id = None
        self._tick = 0
        self._comm_server_module = None
        self._visualiser = None
        self._snapshot_image_bytes = []
        self._snapshot_image = None
        self._paused = True
        self._step = False

    def update(self):
        # player_inputs = server_engine.get_player_inputs()
        # player_inputs_array = player_inputs.get_inputs()
        # if len(player_inputs_array) > 0:
        #     player_input = player_inputs_array[0]
        #     paused = player_input.circle()
        #     # print(str(player_inputs_array[0]))
        #
        if self._is_connected_method() and self._server_engine.is_client_ready() and (not self._paused or self._step):
            if self._step:
                self._step = False
            try:
                next(self.running_behaviour)
            except StopIteration:
                pass
            self.robot.update(self.space)
            self.space.step(0.4)

            world_width = self.world.get_width()
            world_height = self.world.get_height()

            sim_rover = self._server_engine.challenge.get_sim_object(self._sim_rover_id)

            sim_rover.set_position_2(self.robot.body.position.x - world_width // 2, world_height // 2 - self.robot.body.position.y)
            sim_rover.set_bearing(270 - self.robot.body.angle * 180 / math.pi)
            sim_rover.changed = False

            for object in self.space.bodies:
                if isinstance(object, BarrelBody):
                    barrel_body = object
                    local_object_id = barrel_body.get_local_object()
                    if local_object_id is None:
                        if barrel_body.is_green():
                            local_object = self._server_engine.challenge.make_barrel(BarrelColour.Green)
                        else:
                            local_object = self._server_engine.challenge.make_barrel(BarrelColour.Red)
                        local_object_id = local_object.get_id()
                        barrel_body.set_local_object(local_object_id)
                    else:
                        local_object = self._server_engine.challenge.get_sim_object(local_object_id)

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
        if self._snapshot_image is not None:
            self.screen.blit(self._snapshot_image, (0, 0))
        if self._paused:
            self.screen.blit(self._font.render("Paused", True, (255, 255, 255)), (10, 0))
        pygame.display.flip()

    def screenshot_callback(self, message):
        packet_no = message.get_packet_no()
        total_packets = message.get_total_packets()
        if packet_no == 0:
            self._snapshot_image_bytes = message.get_buffer()
        else:
            self._snapshot_image_bytes += message.get_buffer()

        if packet_no + 1 == total_packets:
            pilImage = Image.frombytes("RGBA", (320, 256), bytes(self._snapshot_image_bytes))
            # openCVImage = numpy.array(pilImage)
            self._snapshot_image = pygame.image.fromstring(pilImage.tobytes("raw"), (320, 256), "RGBA")

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
        self._sim_rover_id = self._server_engine.challenge.spawn_rover(RoverType.GCC).get_id()

        self._server_engine.process(self._tick)
        self._server_engine.set_screenshot_callback(self.screenshot_callback)

        serializer_factory = ByteSerializerFactory()
        self._message_factory = MessageFactory()

        if args.tcp:
            self._comm_server_module = TCPServerModule(self._server_engine, serializer_factory, self._message_factory)
        else:
            self._comm_server_module = UDPServerModule(self._server_engine, serializer_factory, self._message_factory)

        self._is_connected_method = self._comm_server_module.is_connected

        thread = Thread(target=self._comm_server_module.process, daemon=True)
        thread.start()

        self._font = pygame.font.SysFont("comicsansms", 32)
        self.surface = pygame.Surface((self.world.get_width(), self.world.get_height()))
        self.screen = pygame.display.set_mode((800, 800))
        self.draw_options = pymunk.pygame_util.DrawOptions(self.surface)

        time.sleep(0.2)

        self._visualiser = Visualisation()
        # self._visualiser.set_debug(True)
        # self._visualiser.set_remote_java_debugging(True)
        self._visualiser.start()

        pause_pressed = False
        step_pressed = False
        running = True
        while running:
            self.world.update(self.screen.get_width(), self.screen.get_height())
            pressed = pygame.key.get_pressed()
            if pressed[pygame.K_s]:
                message = self._message_factory.obtain(MessageCode.ServerRequestScreenshot)
                self._server_engine.send_message(message)
                message.free()

            self.update()
            self.draw()
            for event in pygame.event.get():
                if event.type == pygame.QUIT:
                    running = False
                pressed = pygame.key.get_pressed()
                if pressed[pygame.K_p]:
                    if not pause_pressed:
                        pause_pressed = True
                        self._paused = not self._paused
                else:
                    if pause_pressed:
                        pause_pressed = False
                if pressed[pygame.K_o]:
                    if not step_pressed:
                        step_pressed = True
                        self._step = not self._step
                else:
                    if step_pressed:
                        step_pressed = False


if __name__ == '__main__':

    runner = WorldRunner()
    runner.main()
