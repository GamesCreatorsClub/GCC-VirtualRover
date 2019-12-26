import math
from importlib import import_module
import sys

import pymunk, pygame
import pymunk.pygame_util

from lib.robot import Robot
from piwarssim.engine.message import MessageFactory
from piwarssim.engine.server import ServerEngine


class WorldRunner:
    def __init__(self):
        self.space = pymunk.Space()
        self.screen = None
        self.draw_options = None
        self.running_behaviour = None
        self.robot = None
        self.world = None

    def update(self):
        pause = False

        player_inputs = server_engine.get_player_inputs()
        player_inputs_array = player_inputs.get_inputs()
        if len(player_inputs_array) > 0:
            player_input = player_inputs_array[0]
            pause = player_input.circle()
            # print(str(player_inputs_array[0]))

        if not pause:
            try:
                next(self.running_behaviour)
            except StopIteration:
                pass
            self.robot.update(self.space)
            self.space.step(0.4)

            rover.set_position_2(1000 - self.robot.body.position.x * 2.5, self.robot.body.position.y * 2.5 - 1000)
            rover.set_bearing(90 - self.robot.body.angle * 180 / math.pi)
            rover.changed = False

        server_engine.process(t)
        server_engine.send_update()

    def draw(self):
        self.screen.fill((1.0, 0, 0))
        self.space.debug_draw(self.draw_options)
        self.robot.draw(self.screen)
        pygame.display.flip()

    def main(self):
        print(sys.argv)
        pygame.init()
        pymunk.pygame_util.positive_y_is_up = False

        behaviour_module = import_module("behaviours." + sys.argv[1])
        world_module = import_module("worlds." + sys.argv[2])

        self.screen = pygame.display.set_mode((world_module.WIDTH, world_module.HEIGHT))
        self.draw_options = pymunk.pygame_util.DrawOptions(self.screen)

        self.robot = Robot()

        self.space.damping = 0.2
        self.space.add(self.robot.body, self.robot.shape)

        self.running_behaviour = behaviour_module.Behaviour(self.robot.controls).run()
        self.world = world_module.World(self.space, self.robot)

        running = True
        while running:
            self.world.update()
            self.update()
            self.draw()
            for event in pygame.event.get():
                if event.type == pygame.QUIT:
                    running = False


if __name__ == '__main__':
    from threading import Thread

    from piwarssim.engine.transfer.UDPServerModule import UDPServerModule
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

    udpServerModule = UDPServerModule(server_engine, serializer_factory, message_factory)

    print(str(rover))

    thread = Thread(target=udpServerModule.process, daemon=True)
    thread.start()

    runner = WorldRunner()
    runner.main()
