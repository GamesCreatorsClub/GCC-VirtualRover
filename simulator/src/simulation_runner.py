import argparse
import numpy
import os
import time
import platform
import pygame
import sys

from enum import Enum
from functools import cmp_to_key
from PIL import Image
from threading import Thread

from piwarssim.engine.challenges.Challenges import Challenges
from piwarssim.engine.message import MessageFactory
from piwarssim.engine.message.MessageCode import MessageCode
from piwarssim.engine.server import ServerEngine
from piwarssim.engine.simulation import PiWarsSimObjectTypes
from piwarssim.engine.simulation.attachments.CameraAttachemntObject import CameraAttachmentObject
from piwarssim.engine.transfer.ByteSerializerFactory import ByteSerializerFactory
from piwarssim.engine.transfer.TCPServerModule import TCPServerModule
from piwarssim.engine.transfer.UDPServerModule import UDPServerModule
from piwarssim.visualisation.Visualisation import Visualisation


class SnapshotState(Enum):
    Idle = ()
    SnapshotRequested = ()
    SnapshotReceivedWaitingToDeliver = ()


class SnapshotHandler:
    def __init__(self, camera_id, create_open_cv_image=False):
        self.camera_id = camera_id
        self.camera_order = 1
        self.image = None
        self.open_cv_image = None
        self._create_open_cv_image = create_open_cv_image
        self._state = SnapshotState.Idle
        self._image_bytes = None
        self._received_callback = None
        self._received_timestamp = 0

    def snapshot_requested(self):
        return self._state != SnapshotState.Idle

    def request_snapshot(self, current_timestamp, server_engine, snapshot_received_callback=None):
        self._received_timestamp = current_timestamp
        self._received_callback = snapshot_received_callback

        message = server_engine.get_message_factory().obtain(MessageCode.ServerRequestScreenshot)
        try:
            message.set_camera_id(self.camera_id)
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
                if self._create_open_cv_image:
                    self.open_cv_image = numpy.array(pil_image)
                self._received_callback(self)


class SnapshotRequester:
    def __init__(self, server_engine, sim_rover_id):
        self._server_engine = server_engine
        self._sim_rover_id = sim_rover_id
        self._snapshot_handlers_list = []
        self._snapshot_handlers_map = {}
        self._snapshot_handlers_to_process = []
        self._current_timestamp = None
        self._snapshot_callback = None
        self._processed_snapshot_handlers = []
        self._in_progress = False
        self._server_engine.set_screenshot_callback(self._internal_snapshot_callback)
        self._server_engine.challenge.register_after_sim_object_added_listener(self._after_sim_object_camera_added_listener)

    def _after_sim_object_camera_added_listener(self, challenge, sim_object):
        if isinstance(sim_object, CameraAttachmentObject):
            camera_id = sim_object.get_id()
            handler = SnapshotHandler(camera_id)
            self._snapshot_handlers_list.append(handler)
            self._snapshot_handlers_list.sort(key=cmp_to_key(lambda x, y: 1 if x.camera_order < y.camera_order else (0 if x.camera_order == y.camera_order else -1)))
            self._snapshot_handlers_map[camera_id] = handler

    def get_camera_snapshots(self):
        return self._snapshot_handlers_map

    def get_camera_snapshopts_as_list(self):
        return self._snapshot_handlers_list

    def request_snapshot(self, current_timestamp, snapshot_callback):
        self._snapshot_handlers_to_process = [handler for handler in self._snapshot_handlers_list]
        self._current_timestamp = current_timestamp
        self._snapshot_callback = snapshot_callback
        self._in_progress = True
        self._process()

    def is_snapshot_request_in_progress(self):
        return self._in_progress

    def _internal_snapshot_callback(self, message):
        camera_id = message.get_camera_id()
        if camera_id in self._snapshot_handlers_map:
            self._snapshot_handlers_map[camera_id].snapshot_callback(message)
        else:
            # TODO should we throw an error in case of getting snapshot for camera we don't know anything about?
            print(f"Got snapshot for camera id {camera_id} which is not available")

    def _snapshot_received_callback(self, snapshot_handler):
        self._processed_snapshot_handlers.append(snapshot_handler)
        self._process()

    def _process(self):
        if len(self._snapshot_handlers_to_process) > 0:
            snapshot_handler = self._snapshot_handlers_to_process[0]
            self._snapshot_handlers_to_process = self._snapshot_handlers_to_process[1:]
            snapshot_handler.request_snapshot(self._current_timestamp, self._server_engine, self._snapshot_received_callback)
        else:
            if self._snapshot_callback is not None:
                self._snapshot_callback(self._processed_snapshot_handlers)
            self._in_progress = False


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
        self._sim_game_message_id = None
        self._visualiser = None
        self._paused = False
        self._step = False
        self._delta_tick = 0.02
        self._timestamp = 0
        self._snapshot_requester = None

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

    def get_camera_snapshots(self):
        return self._snapshot_requester.get_camera_snapshopts()

    def update(self):
        # player_inputs = server_engine.get_player_inputs()
        # player_inputs_array = player_inputs.get_inputs()
        # if len(player_inputs_array) > 0:
        #     player_input = player_inputs_array[0]
        #     paused = player_input.circle()
        #     # print(str(player_inputs_array[0]))
        #

        if (self._is_connected_method is None
                    or (self._is_connected_method())
                        and self._server_engine.is_client_ready()
                        and not self._snapshot_requester.is_snapshot_request_in_progress()) \
                and (not self._paused or self._step):
            self.simulation_adapter.update(self._timestamp, self._delta_tick)
            self._timestamp += self._delta_tick
            self._step = False

    def draw(self):
        self._screen.fill((1.0, 0, 0), special_flags=pygame.BLEND_RGBA_MULT)
        self.simulation_adapter.draw(self._screen, self._screen_world_rect)

        i = 0
        for snapshot_handler in self._snapshot_requester.get_camera_snapshopts_as_list():
            if snapshot_handler.image is not None:
                self._screen.blit(snapshot_handler.image, (0, i))
                i += snapshot_handler.image.get_height()
        if self._paused:
            self._screen.blit(self._font.render("Paused", True, (255, 255, 255)), (10, 0))
        self._screen.blit(self._font.render(f"{self._timestamp:3.2f}", True, (255, 255, 255)), (700, 0))
        pygame.display.flip()

    def request_snapshot(self, snapshot_received_callback=None):
        self._snapshot_requester.request_snapshot(self._timestamp, snapshot_received_callback)

    def leave(self):
        if self._visualiser is not None:
            self._visualiser.stop()
        sys.exit(0)

    def init(self):
        self.simulation_adapter.set_simulation_runner(self)

        parser = argparse.ArgumentParser(description="Simulation runner")
        group = parser.add_mutually_exclusive_group(required=False)
        group.add_argument('--tcp', action='store_true', help="use TCP to connect to UI")
        group.add_argument('--udp', action='store_true', help="use UDP to connect to UI")
        parser.add_argument('--challenge', dest='challenge', help="name of challenge to simulate")
        parser.add_argument('--rover', dest='rover', default="GCCRoverM16", help="name of challenge to simulate")
        parser.add_argument('--no-visualiser', action='store_true', dest='no_visualiser', help="don't start visualiser")
        parser.add_argument('--debug-java', action='store_true', dest='debug_java', help="should visualiser asked to output debug")
        parser.add_argument('--remote-java-debugging', action='store_true', dest='remote_java_debugging', help="should visualiser started in debug mode waiting for debugger to be attached")
        parser.add_argument('--start-paused', action='store_true', dest='start_paused', help="should simulation start paused")

        self.simulation_adapter.define_arguments(parser)  # This will allow adapter to add its own parameters
        args = parser.parse_args()

        self.simulation_adapter.process_arguments(args)  # This will create 'simulation_adapter.world' object

        self._paused = args.start_paused

        challenge_name = args.challenge if args.challenge is not None else self.simulation_adapter.get_challenge_name()
        if challenge_name is None:
            print("You must supply '--challenge' command line argument with name of challenge")
            print("    (from piwarsim.challenges.Challenges enum) or implement get_challenge_name")
            print("    from BaseSimulationAdapter class.")
            sys.exit(-1)

        self.setup_pygame()
        self.setup_server_engine(challenge_name, args.rover)

        if args.tcp or args.udp:
            self.setup_connection(args.tcp)

        time.sleep(0.2)

        if not args.no_visualiser:
            self._visualiser = Visualisation()
            if args.debug_java:
                self._visualiser.set_debug(True)

            if args.remote_java_debugging:
                self._visualiser.set_remote_java_debugging(True)
            self._visualiser.start()

    def setup_pygame(self):
        os.environ['SDL_VIDEO_WINDOW_POS'] = "%d,%d" % (800, 60)
        pygame.init()

        self._font = pygame.font.SysFont("comicsansms", 32)
        self._screen = pygame.display.set_mode((800, 800))
        self._screen_world_rect = pygame.Rect(0, 0, 800, 800)

    def setup_server_engine(self, challenge_name, rover_name="GCCRoverM16"):
        rover = PiWarsSimObjectTypes.from_name(rover_name)
        if not rover.is_rover():
            print(f"Selected rover name {rover_name} is not a rover")
            sys.exit(-1)

        challenge = Challenges.from_name(challenge_name).new_object()
        self._server_engine = ServerEngine(challenge)
        self._sim_rover_id = self._server_engine.challenge.spawn_rover(rover).get_id()
        self._sim_game_message_id = self._server_engine.challenge.create_game_message_object().get_id()
        self._snapshot_requester = SnapshotRequester(self._server_engine, self._sim_rover_id)

        self._server_engine.process(self._timestamp)  # Move to '0' seconds position

        self.simulation_adapter.set_server_engine(self._server_engine)
        self.simulation_adapter.set_sim_rover_id(self._sim_rover_id)
        self.simulation_adapter.set_game_message_object_id(self._sim_game_message_id)

        self.simulation_adapter.init()

    def setup_connection(self, tcp):
        serializer_factory = ByteSerializerFactory()
        self._message_factory = MessageFactory()

        if tcp:
            self._comm_server_module = TCPServerModule(self._server_engine, serializer_factory, self._message_factory)
        else:
            self._comm_server_module = UDPServerModule(self._server_engine, serializer_factory, self._message_factory)

        self._is_connected_method = self._comm_server_module.is_connected

        thread = Thread(target=self._comm_server_module.process, daemon=True)
        thread.start()

    def main(self):
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
            if pressed[pygame.K_s] and not self._snapshot_requester.is_snapshot_request_in_progress():
                self.request_snapshot()

            previous_pressed = pressed
