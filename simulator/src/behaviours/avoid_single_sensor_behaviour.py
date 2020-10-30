class Behaviour:

    TURNING_DISTANCE = 160

    def __init__(self, robot):
        self.robot = robot

    def run(self):
        yield
        self.robot.set_left(100)
        self.robot.set_right(100)
        try:
            while True:
                if self.robot.dist_mid.get_distance() > Behaviour.TURNING_DISTANCE:
                    self.robot.set_left(100)
                    yield
                else:
                    while self.robot.dist_mid.get_distance() < Behaviour.TURNING_DISTANCE:
                        self.robot.set_left(-100)
                        yield
        finally:
            self.robot.stop()
