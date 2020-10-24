import pymunk
import pygame

from piwarssim.engine.simulation.objects import BarrelSimObject, BarrelColour, ToyCubeSimObject, ToyCubeColour, FishTowerSimObject, GolfBallSimObject

from piwarssim.engine.simulation.rovers import AbstractRoverSimObject


class AbstractWorld:
    def __init__(self, challenge_name, space, robot):
        self._width = 0
        self._length = 0
        self._mouse_pressed = False
        self._challenge_name = challenge_name
        self.space = space
        self.robot = robot
        self._challenge_dimensions = 0, 0, 0, 0

    def get_challenge_name(self):
        return self._challenge_name

    def get_width(self):
        return self._width

    def get_length(self):
        return self._length

    def synchronise_challenge(self, challenge):
        '''
        This method is called at the beginning to synchronise objects like walls, initial simulation objects and such
        from piwarssip to pymunk physics engine.
        :param challenge:
        :return:
        '''

        d = challenge.get_challenge_max_dimensions()
        self._challenge_dimensions = d
        self._width = d[1] - d[0]
        self._length = d[3] - d[2]

        polygon_vertices = [[self.translate_from_sim(p[0], p[1]) for p in p.local_vertices] for p in challenge.wall_polygons]
        # for vertices in polygon_vertices:
        #     vertices.reverse()

        walls = [pymunk.Poly(self.space.static_body, v, radius=1) for v in polygon_vertices]

        for wall in walls:
            wall.elasticity = 0.95
            wall.friction = 0.9
        self.space.add(walls)

        sim_rover = None
        sim_state = challenge.get_current_sim_state()
        for object_id in sim_state:
            object = sim_state[object_id]
            if isinstance(object, AbstractRoverSimObject):
                sim_rover = object
            else:
                self.to_pymunk_body(object, object_id)

        if sim_rover is None:
            sim_rover = self._find_rover(challenge)
        self.robot.body.position = self.translate_from_sim_2(sim_rover.get_position())  # self._width / 2, self._length / 2

    def to_pymunk_body(self, object, object_id):
        pass

    @staticmethod
    def _find_rover(challenge):
        sim_state = challenge.get_current_sim_state()
        for object_id in sim_state:
            object = sim_state[object_id]
            if isinstance(object, AbstractRoverSimObject):
                return object

        return None

    def translate_to_sim(self, x, y):
        return x + self._challenge_dimensions[0], - y - self._challenge_dimensions[2]

    def translate_from_sim(self, x, y):
        return x - self._challenge_dimensions[0], self._challenge_dimensions[3] - y

    def translate_from_sim_2(self, position):
        return position[0] - self._challenge_dimensions[0], self._challenge_dimensions[3] - position[1]

    def update(self, world_screen_rect):
        if pygame.mouse.get_pressed()[0] and not self._mouse_pressed:
            self._mouse_pressed = True

            x, y = pygame.mouse.get_pos()

            off_x = world_screen_rect.x
            off_y = world_screen_rect.y
            width = world_screen_rect.width
            height = world_screen_rect.height
            self.mouse_pressed(x * self._width / width - off_x, y * self._length / height - off_y)

        elif not pygame.mouse.get_pressed()[0] and self._mouse_pressed:
            self._mouse_pressed = False

    def mouse_pressed(self, x, y):
        pass
