import math
from importlib import import_module
import sys

import pymunk, pygame
import pymunk.pygame_util

from lib.robot import Robot
from piwarssim.engine.message import MessageFactory
from piwarssim.engine.server import ServerEngine
from piwarssim.engine.simulation.BarrelSimObject import BarrelColour

from worlds.eco_disaster import BarrelBody


class WorldRunner:
    def __init__(self, is_connected_method, *args):
        self.is_connected_method = is_connected_method
        self.args = args
        self.space = pymunk.Space()
        self.screen = None
        self.draw_options = None
        self.running_behaviour = None
        self.robot = None
        self.world = None
        self.surface = None

    def update(self):
        # player_inputs = server_engine.get_player_inputs()
        # player_inputs_array = player_inputs.get_inputs()
        # if len(player_inputs_array) > 0:
        #     player_input = player_inputs_array[0]
        #     paused = player_input.circle()
        #     # print(str(player_inputs_array[0]))
        #
        if self.is_connected_method():
            try:
                next(self.running_behaviour)
            except StopIteration:
                pass
            self.robot.update(self.space)
            self.space.step(0.4)

            world_width = self.world.get_width()
            world_height = self.world.get_height()

            # rover.set_position_2(1000 - self.robot.body.position.x * 2.5, self.robot.body.position.y * 2.5 - 1000)
            rover.set_position_2(self.robot.body.position.x - world_width // 2, world_height // 2 - self.robot.body.position.y)
            rover.set_bearing(90 - self.robot.body.angle * 180 / math.pi)
            rover.changed = False

            for object in self.space.bodies:
                if isinstance(object, BarrelBody):
                    barrel_body = object
                    local_object = barrel_body.get_local_object()
                    if local_object is None:
                        if barrel_body.is_green():
                            local_object = server_engine.challenge.make_barrel(BarrelColour.Green)
                        else:
                            local_object = server_engine.challenge.make_barrel(BarrelColour.Red)
                        barrel_body.set_local_object(local_object)
                    # local_object.set_position_2(1000 - barrel_body.position.x * 2.5, barrel_body.position.y * 2.5 - 1000)
                    local_object.set_position_2(barrel_body.position.x - world_width // 2, world_height // 2 - barrel_body.position.y)

            server_engine.process(t)
            server_engine.send_update()

    def draw(self):
        # self.screen.fill((1.0, 0, 0))
        self.surface.fill((1.0, 0, 0))
        self.space.debug_draw(self.draw_options)
        self.robot.draw(self.surface)
        # self.robot.draw(self.screen)
        self.screen.blit(pygame.transform.scale(self.surface, (self.screen.get_width(), self.screen.get_height())), (0, 0))
        pygame.display.flip()

    def main(self):
        print("Starting with arguments " + str(self.args))
        pygame.init()
        pymunk.pygame_util.positive_y_is_up = False

        behaviour_module = import_module("behaviours." + self.args[0])
        world_module = import_module("worlds." + self.args[1])

        self.robot = Robot()

        self.space.damping = 0.2
        self.space.add(self.robot.body, self.robot.shape)

        self.running_behaviour = behaviour_module.Behaviour(self.robot.controls).run()
        self.world = world_module.World(self.space, self.robot)

        self.surface = pygame.Surface((self.world.get_width(), self.world.get_height()))
        self.screen = pygame.display.set_mode((800, 800))
        self.draw_options = pymunk.pygame_util.DrawOptions(self.surface)

        running = True
        while running:
            self.world.update(self.screen.get_width(), self.screen.get_height())
            self.update()
            self.draw()
            for event in pygame.event.get():
                if event.type == pygame.QUIT:
                    running = False


if __name__ == '__main__':
    from threading import Thread

    from piwarssim.engine.transfer.UDPServerModule import UDPServerModule
    from piwarssim.engine.transfer.TCPServerModule import TCPServerModule
    from piwarssim.engine.transfer.ByteSerializerFactory import ByteSerializerFactory
    from piwarssim.engine.simulation.rovers.RoverType import RoverType
    from piwarssim.engine.challenges.PiNoonChallenge import PiNoonChallenge

    pi_noon_challenge = PiNoonChallenge()

    server_engine = ServerEngine(pi_noon_challenge)
    server_engine.challenge = pi_noon_challenge

    # pi_noon_challenge.process(0) # 1 second into the game
    rover = pi_noon_challenge.spawn_rover(RoverType.GCC)

    t = 0
    server_engine.process(t)

    serializer_factory = ByteSerializerFactory()
    message_factory = MessageFactory()

    args = [a for a in sys.argv]
    del args[0]
    if args[0].upper() == 'UDP':
        commServerModule = UDPServerModule(server_engine, serializer_factory, message_factory)
        del args[0]
    elif args[0].upper() == 'TCP':
        commServerModule = TCPServerModule(server_engine, serializer_factory, message_factory)
        del args[0]
    else:
        print("First argument must be 'TCP' or 'UDP'")
        sys.exit(-1)

    print(str(rover))

    thread = Thread(target=commServerModule.process, daemon=True)
    thread.start()

    runner = WorldRunner(commServerModule.is_connected, *args)
    runner.main()
