import math
import random
import pymunk, pygame
import pymunk.pygame_util

from lib import categories
from lib.robot import Robot
from behaviours.avoid_single_sensor_behaviour import Behaviour

WIDTH = 880
HEIGHT = 880
FIELD_WIDTH = 2200.0
SCALE_FACTOR = WIDTH/FIELD_WIDTH

BARREL_AREA_WIDTH = 1600.0
BARREL_WIDTH_GAP = (FIELD_WIDTH - BARREL_AREA_WIDTH) / 2.0


robot = Robot()
robot.body.position = WIDTH/2, HEIGHT - (SCALE_FACTOR * 100)
robot.body.angle = -math.pi

running_behaviour = Behaviour(robot.controls).run()

space = pymunk.Space()
space.damping = 0.2
space.add(robot.body, robot.shape)
pymunk.pygame_util.positive_y_is_up = False


def make_walls():
    walls = [
        pymunk.Segment(space.static_body, (1, 0), (1, HEIGHT), 1),
        pymunk.Segment(space.static_body, (0, HEIGHT -1), (WIDTH, HEIGHT-1), 1),
        pymunk.Segment(space.static_body, (WIDTH - 1, 0), (WIDTH - 1, HEIGHT), 1),
        pymunk.Segment(space.static_body, (0, 1), (WIDTH, 1), 1)
    ]
    for wall in walls:
        wall.elasticity = 0.95
        wall.friction = 0.9
    space.add(walls)
    # Real with is 2200. 880 puts us at n * 0.4. 
    clean_zone = pymunk.Body(body_type=pymunk.Body.STATIC)
    clean_zone_box = pymunk.Poly.create_box(clean_zone,  (600*SCALE_FACTOR, 200*SCALE_FACTOR), 1)
    clean_zone_box.filter = categories.marker_filter
    clean_zone.position = (WIDTH/2 - (400 *SCALE_FACTOR), 0)
    clean_zone.sensor = True
    space.add(clean_zone, clean_zone_box)
    clean_zone_box.color = pygame.color.THECOLORS["blue"]

    contaminated_zone = pymunk.Body(body_type=pymunk.Body.STATIC)
    contaminated_zone_box = pymunk.Poly.create_box(contaminated_zone,  (600*SCALE_FACTOR, 200*SCALE_FACTOR), 1)
    contaminated_zone_box.filter = categories.marker_filter
    contaminated_zone.position = (WIDTH/2 + (400 *SCALE_FACTOR), 0)
    contaminated_zone.sensor = True
    space.add(contaminated_zone, contaminated_zone_box)
    contaminated_zone_box.color = pygame.color.THECOLORS["yellow"]


def generate_barrel(position):
    barrel = pymunk.Body(body_type=pymunk.Body.STATIC)
    barrel_shape = pymunk.Circle(barrel, 25)
    barrel_shape.color = pygame.color.THECOLORS["red"]
    barrel.position = position
    space.add(barrel, barrel_shape)


def generate_space():
    make_walls()
    barrel_field_min = int(BARREL_WIDTH_GAP * SCALE_FACTOR)
    barrel_field_max = int((FIELD_WIDTH - BARREL_WIDTH_GAP) * SCALE_FACTOR)
    print(barrel_field_min, barrel_field_max)
    for _ in range(10):
        position = (random.randint(barrel_field_min, barrel_field_max),
            random.randint(barrel_field_min, barrel_field_max))
        generate_barrel(position)
        print(repr(position))


def draw():
    screen.fill((0, 0, 0))
    draw_options = pymunk.pygame_util.DrawOptions(screen.surface)
    
    space.debug_draw(draw_options)
    robot.draw(screen)


def update():
    try:
        next(running_behaviour)
    except StopIteration:
        pass
    robot.update(space)
    space.step(0.5)
    robot.update(space)
    space.step(0.5)


# if __name__ == "__main__":
generate_space()
