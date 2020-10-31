import pymunk
import pygame

from lib import categories
from lib.robot import Robot

from piwarssim.engine.simulation.rovers import AbstractRoverSimObject


class PymunkBody(pymunk.Body):
    def __init__(self, mass=1, moment=10, body_type=pymunk.Body.DYNAMIC):
        super(PymunkBody, self).__init__(mass=mass, moment=moment, body_type=body_type)
        self._local_object = None
        self.shape = None

    def get_local_object(self):
        return self._local_object

    def set_local_object(self, local_object):
        self._local_object = local_object


class PymunkAbstractWorld:
    def __init__(self, challenge_name):
        self._width = 0
        self._length = 0
        self._mouse_pressed = False
        self._challenge_name = challenge_name
        self.space = pymunk.Space()
        self.space.damping = 0.01
        self.robot = None
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

        walls = [pymunk.Poly(self.space.static_body, v, radius=1) for v in polygon_vertices]

        for wall in walls:
            wall.elasticity = 0.95
            wall.friction = 0.9
            wall.filter = categories.wall_filter
        self.space.add(walls)

        sim_state = challenge.get_current_sim_state()
        for object_id in sim_state:
            self.to_pymunk_body(sim_state[object_id], object_id)

    def to_pymunk_body(self, sim_object, object_id):
        if isinstance(sim_object, AbstractRoverSimObject):
            self.robot = Robot(sim_object)
            self.robot.position = self.translate_from_sim_2(sim_object.get_position())  # self._width / 2, self._length / 2
            self.space.add(self.robot, self.robot.shape)

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

    def draw(self, surface):
        self.robot.draw(surface)

    def mouse_pressed(self, x, y):
        pass
