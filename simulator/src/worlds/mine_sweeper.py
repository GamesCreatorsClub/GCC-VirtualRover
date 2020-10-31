"""Simple Mine Sweeper Example"""

from piwarssim.engine.simulation import PiWarsSimObjectTypes
from worlds.abstract_world import PymunkAbstractWorld


WIDTH = 2200
HEIGHT = 2200


class WorldPymunk(PymunkAbstractWorld):
    def __init__(self):
        super(WorldPymunk, self).__init__("MineSweeper", WIDTH, HEIGHT)

    def update(self, world_screen_rect):
        super(WorldPymunk, self).update(world_screen_rect)

    # def mouse_pressed(self, x, y):
    #     self._green_barrel = not self._green_barrel
    #     box_body = BarrelBody(self._green_barrel, body_type=pymunk.Body.STATIC)
    #     box_shape = pymunk.Circle(box_body, 25)
    #     box_body.position = (x, y)
    #     self.space.add(box_body, box_shape)
    #     box_shape.color = pygame.color.THECOLORS["white"]
