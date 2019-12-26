import pymunk
import pygame
import math
import pprint
WIDTH = 800
HEIGHT = 800

space = pymunk.Space()

robot_body = pymunk.Body()
robot_sprite = Actor('robot.png')
robot_shape = pymunk.Poly.create_box(robot_body, size=(robot_sprite.width, robot_sprite.height))
half_sprite_height = robot_sprite.height/2
robot_shape.density = 0.1
sensor_shape = pymunk.Poly(robot_body, 
    [[-5, half_sprite_height+0], [-10, half_sprite_height+10], [10, half_sprite_height+10], [5, half_sprite_height+0]]
)
sensor_shape.sensor = True
robot_body.position = 400, 400

space.add(robot_body, robot_shape)
space.damping = 0.2

def draw_sensor():
    transformed_coordinates = [
        v.rotated(sensor_shape.body.angle) + sensor_shape.body.position for v in sensor_shape.get_vertices()
    ]
    pygame.draw.polygon(screen.surface,
        (255, 0, 0, 128),
        [
            (int(v.x), int(v.y)) for v in transformed_coordinates
        ]
    )

def draw():
    screen.fill((0, 0, 0))
    robot_sprite.center = (int(robot_body.position.x), int(robot_body.position.y))
    robot_sprite.angle  = 360 - int(math.degrees(robot_body.angle))
    print(robot_sprite.center)
    robot_sprite.draw()
    draw_sensor()

def update():
    space.step(1)
    if keyboard.e:
        robot_body.apply_impulse_at_local_point((0, 480), (-10, 0))
    if keyboard.q:
        robot_body.apply_impulse_at_local_point((0, 480), (10, 0))
    if keyboard.c:
        robot_body.apply_impulse_at_local_point((0, -480), (-10, 0))
    if keyboard.z:
        robot_body.apply_impulse_at_local_point((0, -480), (10, 0))
    pprint.pprint({
        "position": robot_body.position,
        "mass": robot_body.mass,
        "moment": robot_body.moment,
        "velocity": robot_body.velocity,
        "shape_rad": robot_shape.radius,
        "shape_mass": robot_shape.mass,
        "shape_moment": robot_shape.moment,
        "angle": math.degrees(robot_body.angle),
        "size": (robot_sprite.width, robot_sprite.height)
    })
