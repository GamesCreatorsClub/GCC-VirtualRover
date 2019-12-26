from .pid_controller import PIDController
import logging

logger = logging.getLogger(__name__)


class Behaviour:
    def __init__(self, robot):
        self.robot = robot
        self.mode = 'seek'
    
    def set_mode(self, mode):
        logger.info(f"set mode: {mode}")
        self.mode = mode

    def run(self):
        pid = PIDController(1, 0.001, 0.8)
        yield
        base_speed = 50
        self.robot.set_left(base_speed)
        self.robot.set_right(base_speed)
        # Trying to maintain a distance from wall
        set_point = 80
        try:
            while True:
                distance = self.robot.dist_mid.get_distance()
                if self.mode is 'seek':
                    if distance <= set_point:
                        self.set_mode('follow')
                    else:
                        self.robot.set_left(base_speed)
                        self.robot.set_right(base_speed)
                elif self.mode is 'reverse':
                    self.robot.set_left(-base_speed)
                    self.robot.set_right(-base_speed)
                    if distance == 120:
                        self.set_mode('follow')
                else:
                    if distance < 10:
                        self.set_mode('reverse')
                        pid.reset()
                        continue
                    # if distance == 120:
                    #     self.set_mode('seek')
                    #     pid.reset()
                    #     continue
                    error = set_point - distance
                    value = pid.get_value(error)
                    if abs(value) > 50:
                        logger.warning("value saturated")
                    speed_l = max(-100, min(100, base_speed - value))
                    speed_r = max(-100, min(100, base_speed + value))

                    self.robot.set_left(speed_l)
                    self.robot.set_right(speed_r)
                yield
        finally:
            self.robot.stop()
