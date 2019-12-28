"""Simple world with walls"""
import pymunk
import pygame

WIDTH = 800
HEIGHT = 800


class BarrelBody(pymunk.Body):
    def __init__(self, green_barrel, mass=0, moment=0, body_type=pymunk.Body.DYNAMIC):
        super(BarrelBody, self).__init__(mass=mass, moment=moment, body_type=body_type)
        self._local_object = None
        self._green_barrel = green_barrel

    def is_green(self):
        return self._green_barrel

    def get_local_object(self):
        return self._local_object

    def set_local_object(self, local_object):
        self._local_object = local_object


class World:
    def __init__(self, space, robot):
        self._green_barrel = True
        self._mouse_pressed = False
        self.space = space
        robot.body.position = WIDTH / 2, HEIGHT / 2
        walls = [
            pymunk.Segment(space.static_body, (1, 0), (1, HEIGHT), 1),
            pymunk.Segment(space.static_body, (0, HEIGHT - 1), (WIDTH, HEIGHT - 1), 1),
            pymunk.Segment(space.static_body, (WIDTH - 1, 0), (WIDTH - 1, HEIGHT), 1),
            pymunk.Segment(space.static_body, (0, 1), (WIDTH, 1), 1)
        ]
        for wall in walls:
            wall.elasticity = 0.95
            wall.friction = 0.9
        self.space.add(walls)

    def update(self):
        if pygame.mouse.get_pressed()[0] and not self._mouse_pressed:
            self._mouse_pressed = True
            position = pygame.mouse.get_pos()
            self._green_barrel = not self._green_barrel
            box_body = BarrelBody(self._green_barrel, body_type=pymunk.Body.STATIC)
            box_shape = pymunk.Circle(box_body, 25)
            # box_shape = pymunk.Poly.create_box(box_body,  (30, 30), 1)
            box_body.position = position # (40, 400)
            self.space.add(box_body, box_shape)
            box_shape.color = pygame.color.THECOLORS["white"]
        elif not pygame.mouse.get_pressed()[0] and self._mouse_pressed:
            self._mouse_pressed = False

