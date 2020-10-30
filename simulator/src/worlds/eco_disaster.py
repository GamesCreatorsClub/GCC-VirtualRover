"""Simple Eco Disaster Example"""
import pygame
import pymunk
import worlds.abstract_world
from piwarssim.engine.simulation.objects import BarrelSimObject, BarrelColour
from lib import categories


class BarrelBody(worlds.abstract_world.PymunkBody):
    def __init__(self, sim_object, pos):
        super(BarrelBody, self).__init__()
        self._green_barrel = sim_object.get_barrel_colour() == BarrelColour.Green
        self.set_local_object(sim_object.get_id())
        self.position = pos
        self.shape = pymunk.Circle(self, 25)
        self.shape.mass = 10
        self.shape.filter = categories.object_filter
        self.shape.elasticity = 0.9999999
        self.shape.friction = 0.5

        if sim_object.get_barrel_colour() == BarrelColour.Green:
            self.shape.color = pygame.color.THECOLORS["green"]
        else:
            self.shape.color = pygame.color.THECOLORS["red"]

    def is_green(self):
        return self._green_barrel


class World(worlds.abstract_world.AbstractWorld):
    def __init__(self, space, robot):
        super(World, self).__init__("EcoDisaster", space, robot)
        # self._green_barrel = True

    def update(self, world_screen_rect):
        super(World, self).update(world_screen_rect)

    # def mouse_pressed(self, x, y):
    #     self._green_barrel = not self._green_barrel
    #     box_body = BarrelBody(self._green_barrel, body_type=pymunk.Body.STATIC)
    #     box_shape = pymunk.Circle(box_body, 25)
    #     box_body.position = (x, y)
    #     self.space.add(box_body, box_shape)
    #     box_shape.color = pygame.color.THECOLORS["white"]

    def synchronise_challenge(self, challenge):
        super(World, self).synchronise_challenge(challenge)

    def to_pymunk_body(self, sim_object, object_id):
        super(World, self).to_pymunk_body(sim_object, object_id)

        if isinstance(sim_object, BarrelSimObject):
            box_body = BarrelBody(sim_object, self.translate_from_sim_2(sim_object.get_position()))
            self.space.add(box_body, box_body.shape)
