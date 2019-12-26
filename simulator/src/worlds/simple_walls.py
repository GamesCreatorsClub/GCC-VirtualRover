"""Simple world with walls"""
import pymunk
import pygame

WIDTH = 800
HEIGHT = 800


class World:
    def __init__(self, space, robot):
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
        if pygame.mouse.get_pressed()[0]:
            position = pygame.mouse.get_pos()
            box_body = pymunk.Body(body_type=pymunk.Body.STATIC)
            box_shape = pymunk.Poly.create_box(box_body,  (30, 30), 1)
            box_body.position = position # (40, 400)
            self.space.add(box_body, box_shape)
            box_shape.color = pygame.color.THECOLORS["white"]

