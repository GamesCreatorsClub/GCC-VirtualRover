import argparse
import numpy
import os
import time
import platform
import pygame
import sys

from enum import Enum
from PIL import Image
from threading import Thread

from piwarssim.engine.challenges.Challenges import Challenges
from piwarssim.engine.message import MessageFactory
from piwarssim.engine.message.MessageCode import MessageCode
from piwarssim.engine.server import ServerEngine
from piwarssim.engine.simulation.rovers.RoverType import RoverType
from piwarssim.engine.transfer.ByteSerializerFactory import ByteSerializerFactory
from piwarssim.engine.transfer.TCPServerModule import TCPServerModule
from piwarssim.engine.transfer.UDPServerModule import UDPServerModule
from piwarssim.visualisation.Visualisation import Visualisation


class SnapshotState(Enum):
    Idle = ()
    SnapshotRequested = ()
    SnapshotReceivedWaitingToDeliver = ()


class SnapshotHandler:
    def __init__(self):
        self.image = None
        self._state = SnapshotState.Idle
        self._image_bytes = None
        self._received_callback = None
        self._delay = 0.25  # 4 times a second
        self._receive_timestamp = 0

    def set_snapshot_delay(self, snapshot_delay):
        """
        Amount of time (in simulation) before snapshot is delivered after requested.
        Zero means before next 'tick'
        :param snapshot_delay: amount in seconds
        :return:
        """
        self._delay = snapshot_delay

    def get_snapshot_delay(self):
        self._delay

    def snapshot_requested(self):
        return self._state != SnapshotState.Idle

    def wait_for_snapshot(self, current_timestamp):
        if self._state != SnapshotState.Idle:
            return current_timestamp < self._receive_timestamp

        self._state = SnapshotState.Idle

        return False

    def request_snapshot(self, current_timestamp, server_engine, snapshot_received_callback=None):
        self._receive_timestamp = current_timestamp + self._delay
        self._received_callback = snapshot_received_callback

        message = server_engine.get_message_factory().obtain(MessageCode.ServerRequestScreenshot)
        try:
            server_engine.send_message(message)
        finally:
            message.free()

    def snapshot_callback(self, message):
        packet_no = message.get_packet_no()
        total_packets = message.get_total_packets()
        if packet_no == 0:
            self._image_bytes = message.get_buffer()
        else:
            self._image_bytes += message.get_buffer()

        if packet_no + 1 == total_packets:
            self._state = SnapshotState.SnapshotReceivedWaitingToDeliver

            pil_image = Image.frombytes("RGBA", (320, 256), bytes(self._image_bytes))
            self.image = pygame.image.fromstring(pil_image.tobytes("raw"), (320, 256), "RGBA")

            if self._received_callback is not None:
                open_cv_image = numpy.array(pil_image)
                self._received_callback(open_cv_image)


class SimulationRunner:
    def __init__(self, simulation_adapter):
        self.simulation_adapter = simulation_adapter
        self._screen = None
        self._screen_world_rect = None
        self._font = None
        self._is_connected_method = None
        self._message_factory = None
        self._server_engine = None
        self._comm_server_module = None
        self._sim_rover_id = None
        self._visualiser = None
        self._snapshot_handler = SnapshotHandler()
        self._paused = False
        self._step = False
        self._delta_tick = 0.02
        self._timestamp = 0

    def set_delta_tick(self, delta_tick):
        """
        Sets amount of time to pass between two simulation loops in seconds.
        :param delta_tick: amount of time between two loop executions in seconds
        :return:
        """
        self._delta_tick = delta_tick

    def get_delta_tick(self):
        return self._delta_tick

    def set_timestamp(self, timestamp):
        """
        Sets simulation time. It starts with zero.
        :param timestamp: timestamp in seconds.
        :return:
        """
        self._timestamp = timestamp

    def get_timestamp(self):
        return self._timestamp

    def set_snapshot_delay(self, snapshot_delay):
        """
        Amount of time (in simulation) before snapshot is delivered after requested.
        Zero means before next 'tick'
        :param snapshot_delay: amount in seconds
        :return:
        """
        self._snapshot_handler.set_snapshot_delay(snapshot_delay)

    def get_snapshot_delay(self):
        return self._snapshot_handler.get_snapshot_delay()

    def update(self):
        # player_inputs = server_engine.get_player_inputs()
        # player_inputs_array = player_inputs.get_inputs()
        # if len(player_inputs_array) > 0:
        #     player_input = player_inputs_array[0]
        #     paused = player_input.circle()
        #     # print(str(player_inputs_array[0]))
        #
        if self._is_connected_method() \
                and self._server_engine.is_client_ready() \
                and not self._snapshot_handler.wait_for_snapshot(self._timestamp) \
                and (not self._paused or self._step):
            self.simulation_adapter.update(self._timestamp)
            self._timestamp += self._delta_tick

    def draw(self):
        self._screen.fill((1.0, 0, 0))
        self.simulation_adapter.draw(self._screen, self._screen_world_rect)

        if self._snapshot_handler.image is not None:
            self._screen.blit(self._snapshot_handler.image, (0, 0))
        if self._paused:
            self._screen.blit(self._font.render("Paused", True, (255, 255, 255)), (10, 0))
        pygame.display.flip()

    def request_snapshot(self, snapshot_received_callback=None):
        self._snapshot_handler.request_snapshot(self._timestamp, self._server_engine, snapshot_received_callback)

    def leave(self):
        if self._visualiser is not None:
            self._visualiser.stop()
        sys.exit(0)

    def main(self):
        self.simulation_adapter.set_simulation_runner(self)

        parser = argparse.ArgumentParser(description="Simulation runner")
        group = parser.add_mutually_exclusive_group(required=True)
        group.add_argument('--tcp', action='store_true', help="use TCP to connect to UI")
        group.add_argument('--udp', action='store_false', help="use UDP to connect to UI")
        parser.add_argument('--challenge', dest='challenge', help="name of challenge to simulate")
        parser.add_argument('--no-visualiser', action='store_true', dest='no_visualiser', help="don't start visualiser")
        parser.add_argument('--debug-java', action='store_true', dest='debug_java', help="should visualiser asked to output debug")
        parser.add_argument('--remote-java-debugging', action='store_true', dest='remote_java_debugging', help="should visualiser started in debug mode waiting for debugger to be attached")
        parser.add_argument('--start-paused', action='store_true', dest='start_paused', help="should simulation start paused")

        self.simulation_adapter.define_arguments(parser)
        args = parser.parse_args()

        os.environ['SDL_VIDEO_WINDOW_POS'] = "%d,%d" % (800, 60)
        pygame.init()

        self.simulation_adapter.process_arguments(args)

        self._paused = args.start_paused

        challenge_name = args.challenge if args.challenge is not None else self.simulation_adapter.get_challenge_name()
        if challenge_name is None:
            print("You must supply '--challenge' command line argument with name of challenge")
            print("    (from piwarsim.challenges.Challenges enum) or implement get_challenge_name")
            print("    from BaseSimulationAdapter class.")
            sys.exit(-1)
        challenge = Challenges.from_name(challenge_name).new_object()
        self._server_engine = ServerEngine(challenge)
        self._sim_rover_id = self._server_engine.challenge.spawn_rover(RoverType.GCC).get_id()

        self._server_engine.process(self._timestamp)
        self._server_engine.set_screenshot_callback(self._snapshot_handler.snapshot_callback)

        self.simulation_adapter.set_server_engine(self._server_engine)
        self.simulation_adapter.set_sim_rover_id(self._sim_rover_id)

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
        self._screen = pygame.display.set_mode((800, 800))
        self._screen_world_rect = pygame.Rect(0, 0, 800, 800)

        time.sleep(0.2)

        if not args.no_visualiser:
            self._visualiser = Visualisation()
            if args.debug_java:
                self._visualiser.set_debug(True)

            if args.remote_java_debugging:
                self._visualiser.set_remote_java_debugging(True)
            self._visualiser.start()

        on_mac = platform.system() == 'Darwin'

        previous_pressed = pygame.key.get_pressed()
        running = True
        while running:

            self.update()
            self.draw()
            for event in pygame.event.get():
                if event.type == pygame.QUIT:
                    running = False

            pressed = pygame.key.get_pressed()
            if on_mac and pressed[pygame.K_q] and (pressed[pygame.K_LMETA] or pressed[pygame.K_RMETA]):
                self.leave()
            elif not on_mac and pressed[pygame.K_x] and (pressed[pygame.K_LALT] or pressed[pygame.K_RALT]):
                self.leave()
            if pressed[pygame.K_p] and not previous_pressed[pygame.K_p]:
                self._paused = not self._paused
            if pressed[pygame.K_o] and not previous_pressed[pygame.K_o]:
                self._step = not self._step
            if pressed[pygame.K_s] and not self._snapshot_handler.snapshot_requested():
                self.request_snapshot()

            previous_pressed = pressed
