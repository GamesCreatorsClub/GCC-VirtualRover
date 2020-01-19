

def polygon_from_box(minX, minY, maxX, maxY):
    return Polygon([
        minX, minY,
        minX, maxY,
        maxX, maxY,
        maxX, minY])


class Polygon:
    def __init__(self, vertices):
        self.local_vertices = vertices