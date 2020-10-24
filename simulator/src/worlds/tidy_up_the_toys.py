"""Simple Eco Disaster Example"""
import pygame
import pymunk
import worlds.abstract_world
from piwarssim.engine.simulation.objects import ToyCubeSimObject, ToyCubeColour
from lib import categories


TOY_CUBE_SIDE_LENGTH = 50
TOY_CUBE_SIDE_HALF_LENGTH = TOY_CUBE_SIDE_LENGTH // 2


class ToyCubeBody(pymunk.Body):
    def __init__(self, cube_colour, mass=0, moment=0, body_type=pymunk.Body.DYNAMIC):
        super(ToyCubeBody, self).__init__(mass=mass, moment=moment, body_type=body_type)
        self._local_object = None
        self._cube_colour = cube_colour

    def cube_colour(self):
        return self._cube_colour

    def get_local_object(self):
        return self._local_object

    def set_local_object(self, local_object):
        self._local_object = local_object


class World(worlds.abstract_world.AbstractWorld):
    def __init__(self, space, robot):
        super(World, self).__init__("TidyUpTheToys", space, robot)
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

    def to_pymunk_body(self, object, object_id):
        if isinstance(object, ToyCubeSimObject):
            pos = self.translate_from_sim_2(object.get_position())
            box_body = ToyCubeBody(object.get_cube_colour(), body_type=pymunk.Body.DYNAMIC)
            box_body.set_local_object(object_id)
            box_body.position = pos
            box_shape = pymunk.Poly(
                box_body,
                [
                    (-TOY_CUBE_SIDE_HALF_LENGTH, -TOY_CUBE_SIDE_HALF_LENGTH),
                    (TOY_CUBE_SIDE_HALF_LENGTH, -TOY_CUBE_SIDE_HALF_LENGTH),
                    (TOY_CUBE_SIDE_HALF_LENGTH, TOY_CUBE_SIDE_HALF_LENGTH),
                    (-TOY_CUBE_SIDE_HALF_LENGTH, TOY_CUBE_SIDE_HALF_LENGTH)
                ],
                radius=TOY_CUBE_SIDE_HALF_LENGTH)

            box_shape.density = 0.1
            box_shape.mass = 1
            box_shape.friction = 0.7
            box_shape.elasticity = 0.9999999
            self.space.add(box_body, box_shape)
            box_shape.filter = categories.marker_filter
            if object.get_cube_colour() == ToyCubeColour.Green:
                box_shape.color = pygame.color.THECOLORS["green"]
            elif object.get_cube_colour() == ToyCubeColour.Red:
                box_shape.color = pygame.color.THECOLORS["red"]
            else:
                box_shape.color = pygame.color.THECOLORS["blue"]

            pivot = pymunk.PivotJoint(self.space.static_body, box_body, (0, 0), (0, 0))
            self.space.add(pivot)
            pivot.max_bias = 0  # disable joint correction
            pivot.max_Force = 1000  # emulate linear friction

            gear = pymunk.GearJoint(self.space.static_body, box_body, 0.0, 1.0)
            self.space.add(gear)
            gear.max_bias = 0  # disable joint correction
            gear.max_force = 5000  # emulate angular friction
