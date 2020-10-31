"""Simple Eco Disaster Example"""
import pygame
import pymunk

import worlds.abstract_world
from lib import categories
from piwarssim.engine.simulation.objects import ToyCubeSimObject, ToyCubeColour


class World(worlds.abstract_world.PymunkAbstractWorld):
    def __init__(self):
        super(World, self).__init__("UpTheGardenPath")
