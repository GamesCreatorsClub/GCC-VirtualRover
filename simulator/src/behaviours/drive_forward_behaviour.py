class Behaviour:
    def __init__(self, robot):
        self.robot = robot

    def run(self):
        yield
        self.robot.set_left(100)
        self.robot.set_right(100)        
        for n in range(100):
            # print(self.robot.dist_mid.get_distance())
            yield
        self.robot.stop()
        print("Done")

