class Behaviour:
    def __init__(self, robot):
        self.robot = robot

    def run(self):
        yield
        self.robot.set_left(100)
        self.robot.set_right(100)
        while self.robot.dist_mid.get_distance() > 20:
            yield
        print("Stopping")
        self.robot.stop()
        print("Done")

