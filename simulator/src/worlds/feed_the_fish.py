"""Simple Eco Disaster Example"""
import pymunk

import worlds.abstract_world
from lib import categories
from piwarssim.engine.simulation.objects import GolfBallSimObject, FishTowerSimObject

TOWER_WIDTH = 200
TOWER_HALF_WIDTH = TOWER_WIDTH // 2
GOLF_BALL_DIAMETER = 42


class GolfBallBody(worlds.abstract_world.PymunkBody):
    def __init__(self, sim_object, pos):
        super(GolfBallBody, self).__init__()

        self.set_local_object(sim_object.get_id())
        self.position = pos
        self.shape = pymunk.Circle(self, GOLF_BALL_DIAMETER // 2)
        self.shape.mass = 10
        self.shape.filter = categories.object_filter
        self.shape.elasticity = 0.9999999
        self.shape.friction = 0.5


class FishTowerBody(worlds.abstract_world.PymunkBody):
    def __init__(self, sim_object, pos):
        super(FishTowerBody, self).__init__()

        self.set_local_object(sim_object.get_id())
        self.position = pos
        self.shape = pymunk.Poly.create_box(self, (TOWER_WIDTH, TOWER_WIDTH))
        self.shape.mass = 1000
        self.shape.filter = categories.object_filter
        self.shape.elasticity = 0.9999999
        self.shape.friction = 0.5


class World(worlds.abstract_world.AbstractWorld):
    def __init__(self, space, robot):
        super(World, self).__init__("FeedTheFish", space, robot)
        # self._green_barrel = True

    def update(self, world_screen_rect):
        super(World, self).update(world_screen_rect)

    def to_pymunk_body(self, sim_object, object_id):
        if isinstance(sim_object, GolfBallSimObject):
            golf_ball_body = GolfBallBody(sim_object, self.translate_from_sim_2(sim_object.get_position()))
            self.space.add(golf_ball_body, golf_ball_body.shape)
        elif isinstance(sim_object, FishTowerSimObject):
            fish_tower_body = FishTowerBody(sim_object, self.translate_from_sim_2(sim_object.get_position()))
            self.space.add(fish_tower_body, fish_tower_body.shape)
