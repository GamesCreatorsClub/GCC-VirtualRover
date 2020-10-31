"""Simple Eco Disaster Example"""
import pygame
import pymunk

import worlds.abstract_world
from lib import categories
from piwarssim.engine.simulation.objects import ToyCubeSimObject, ToyCubeColour

TOY_CUBE_SIDE_LENGTH = 50
TOY_CUBE_SIDE_HALF_LENGTH = TOY_CUBE_SIDE_LENGTH // 2


class ToyCubeBody(worlds.abstract_world.PymunkBody):
    def __init__(self, sim_object, pos):
        super(ToyCubeBody, self).__init__()
        self._cube_colour = sim_object.get_cube_colour()

        self.set_local_object(sim_object.get_id())
        self.position = pos
        self.shape = pymunk.Poly.create_box(self, (TOY_CUBE_SIDE_LENGTH, TOY_CUBE_SIDE_LENGTH))
        self.shape.mass = 30
        self.shape.filter = categories.object_filter
        self.shape.elasticity = 0.9999999
        self.shape.friction = 0.5

        if sim_object.get_cube_colour() == ToyCubeColour.Green:
            self.shape.color = pygame.color.THECOLORS["green"]
        elif sim_object.get_cube_colour() == ToyCubeColour.Red:
            self.shape.color = pygame.color.THECOLORS["red"]
        else:
            self.shape.color = pygame.color.THECOLORS["blue"]

    def cube_colour(self):
        return self._cube_colour


class World(worlds.abstract_world.PymunkAbstractWorld):
    def __init__(self):
        super(World, self).__init__("TidyUpTheToys")

    def to_pymunk_body(self, sim_object, object_id):
        if isinstance(sim_object, ToyCubeSimObject):
            toy_cube_body = ToyCubeBody(sim_object, self.translate_from_sim_2(sim_object.get_position()))

            self.space.add(toy_cube_body, toy_cube_body.shape)
        else:
            super(World, self).to_pymunk_body(sim_object, object_id)
