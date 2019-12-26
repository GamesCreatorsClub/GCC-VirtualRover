
class Engine:
    def __init__(self, challenge):
        self.challenge = challenge

    def process(self, timestamp):
        self.challenge.process(timestamp)

