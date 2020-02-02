"""Simple Eco Disaster Example"""
import pymunk
import worlds.abstract_world


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
