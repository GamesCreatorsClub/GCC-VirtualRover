import pymunk
import pygame

from piwarssim.engine.simulation.objects import BarrelSimObject, BarrelColour
from piwarssim.engine.simulation.rovers import AbstractRoverSimObject
import worlds.eco_disaster
from lib import categories


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

        barrel_colour = BarrelColour.Green
        sim_rover = None
        sim_state = challenge.get_current_sim_state()
        for object_id in sim_state:
            object = sim_state[object_id]
            if isinstance(object, AbstractRoverSimObject):
                sim_rover = object
            elif isinstance(object, BarrelSimObject):
                pos = self.translate_from_sim_2(object.get_position())
                box_body = worlds.eco_disaster.BarrelBody(barrel_colour, body_type=pymunk.Body.DYNAMIC)
                box_body.set_local_object(object_id)
                box_body.position = pos
                box_shape = pymunk.Circle(box_body, 25)
                box_shape.density = 0.1
                box_shape.mass = 1
                box_shape.friction = 0.7
                box_shape.elasticity = 0.9999999
                self.space.add(box_body, box_shape)
                box_shape.filter = categories.marker_filter
                if barrel_colour == BarrelColour.Green:
                    box_shape.color = pygame.color.THECOLORS["green"]
                    barrel_colour = BarrelColour.Red
                else:
                    box_shape.color = pygame.color.THECOLORS["red"]
                    barrel_colour = BarrelColour.Green

                pivot = pymunk.PivotJoint(self.space.static_body, box_body, (0, 0), (0, 0))
                self.space.add(pivot)
                pivot.max_bias = 0  # disable joint correction
                pivot.max_Force = 1000  # emulate linear friction

                gear = pymunk.GearJoint(self.space.static_body, box_body, 0.0, 1.0)
                self.space.add(gear)
                gear.max_bias = 0  # disable joint correction
                gear.max_force = 5000  # emulate angular friction

        sim_rover = self._find_rover(challenge)
        self.robot.body.position = self.translate_from_sim_2(sim_rover.get_position())  # self._width / 2, self._length / 2

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
