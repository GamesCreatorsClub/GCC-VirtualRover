"""Simple world with walls"""
import pymunk
import pygame

from worlds.abstract_world import AbstractWorld
from piwarssim.engine.challenges import PiNoonChallenge
from worlds.eco_disaster import BarrelBody

WIDTH = 2000
HEIGHT = 2000


class World(AbstractWorld):
    def __init__(self, space, robot):
        super(World, self).__init__(PiNoonChallenge(), space, robot, WIDTH, HEIGHT)
        self._green_barrel = True

    def mouse_pressed(self, x, y):
        self._green_barrel = not self._green_barrel
        box_body = BarrelBody(self._green_barrel, body_type=pymunk.Body.STATIC)
        box_shape = pymunk.Circle(box_body, 25)
        box_body.position = (x, y)
        self.space.add(box_body, box_shape)
        box_shape.color = pygame.color.THECOLORS["white"]
