import pymunk
import pygame


class AbstractWorld:
    def __init__(self, challenge, space, robot, width, height):
        self._challenge = challenge
        self._width = width
        self._height = height
        self._mouse_pressed = False
        self.space = space
        robot.body.position = self._width / 2, self._height / 2
        self.create_walls(self._width, self._height)

    def get_challenge(self):
        return self._challenge

    def get_width(self):
        return self._width

    def get_height(self):
        return self._height

    def create_walls(self, width, height):
        walls = [
            pymunk.Segment(self.space.static_body, (1, 0), (1, height), 1),
            pymunk.Segment(self.space.static_body, (0, height - 1), (width, height - 1), 1),
            pymunk.Segment(self.space.static_body, (width - 1, 0), (width - 1, height), 1),
            pymunk.Segment(self.space.static_body, (0, 1), (width, 1), 1)
        ]
        for wall in walls:
            wall.elasticity = 0.95
            wall.friction = 0.9
        self.space.add(walls)

    def update(self, screen_width, screen_height):
        if pygame.mouse.get_pressed()[0] and not self._mouse_pressed:
            self._mouse_pressed = True

            x, y = pygame.mouse.get_pos()

            self.mouse_pressed(x * self._width / screen_width, y * self._height / screen_height)

        elif not pygame.mouse.get_pressed()[0] and self._mouse_pressed:
            self._mouse_pressed = False

    def mouse_pressed(self, x, y):
        pass
