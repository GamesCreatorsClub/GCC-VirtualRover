

def polygon_from_box(minX, minY, maxX, maxY):
    return Polygon([
        (minX, minY),
        (minX, maxY),
        (maxX, maxY),
        (maxX, minY)])


class Shape2D:
    def __init__(self):
        pass


class Circle(Shape2D):
    def __init__(self, x, y, radius):
        super(Circle, self).__init__()
        self.x = x
        self.y = y
        self.radius = radius


class Polygon:
    def __init__(self, vertices):
        super(Polygon, self).__init__()
        self.local_vertices = vertices