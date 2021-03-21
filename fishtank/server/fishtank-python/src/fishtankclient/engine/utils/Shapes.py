

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
    def __init__(self, radius, x=0, y=0):
        super(Circle, self).__init__()
        self.x = x
        self.y = y
        self.radius = radius

    def get_x(self):
        return self.x

    def get_y(self):
        return self.y

    def get_radius(self):
        return self.radius


class Polygon:
    def __init__(self, vertices):
        super(Polygon, self).__init__()
        self.local_vertices = vertices

    def get_vertices(self):
        return self.local_vertices

    @staticmethod
    def box(width, height):
        half_width = width // 2
        half_height = height // 2
        return Polygon([-half_width, half_height, half_width, half_height, half_width, -half_height, -half_width, -half_height])
