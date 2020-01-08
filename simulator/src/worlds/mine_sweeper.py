"""Simple Mine Sweeper Example"""
import time
import random

from piwarssim.engine.simulation import PiWarsSimObjectTypes
from worlds.abstract_world import AbstractWorld


WIDTH = 2200
HEIGHT = 2200


class World(AbstractWorld):
    def __init__(self, space, robot):
        super(World, self).__init__("MineSweeper", space, robot, WIDTH, HEIGHT)
        self._green_barrel = True
        self._next_event = 0

    def update(self, world_screen_rect):
        super(World, self).update(world_screen_rect)
        if time.time() > self._next_event:
            mine_sweeper_status = self.get_mine_sweeper_status()

    def get_mine_sweeper_status(self):
        for game_object_id in self.challenge:
            game_object = self.challenge[game_object_id]
            if game_object.get_type() == PiWarsSimObjectTypes.MineSweeperStateObject:
                next_light = random.random(16)

    # def mouse_pressed(self, x, y):
    #     self._green_barrel = not self._green_barrel
    #     box_body = BarrelBody(self._green_barrel, body_type=pymunk.Body.STATIC)
    #     box_shape = pymunk.Circle(box_body, 25)
    #     box_body.position = (x, y)
    #     self.space.add(box_body, box_shape)
    #     box_shape.color = pygame.color.THECOLORS["white"]
