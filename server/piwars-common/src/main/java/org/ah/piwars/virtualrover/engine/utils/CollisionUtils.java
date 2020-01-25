package org.ah.piwars.virtualrover.engine.utils;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Shape2D;
import com.badlogic.gdx.math.Vector2;

import java.util.Arrays;
import java.util.List;

public class CollisionUtils {

    public static Polygon polygonFromBox(float minX, float minY, float maxX, float maxY) {
        Polygon polygon = new Polygon(new float[] {
                minX, minY,
                minX, maxY,
                maxX, maxY,
                maxX, minY });
        return polygon;
    }

    public static boolean polygonsOverlap(List<Shape2D> ps1, List<Shape2D> ps2) {
        for (int i = 0; i < ps1.size(); i++) {
            for (int j = 0; j < ps2.size(); j++) {
                if (overlaps(ps1.get(i), ps2.get(j))) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean polygonsOverlap(Shape2D polygon, List<? extends Shape2D> ps2) {
        for (int i = 0; i < ps2.size(); i++) {
            if (overlaps(polygon, ps2.get(i))) {
                return true;
            }
        }
        return false;
    }

    public static boolean overlaps(Shape2D s1, Shape2D s2) {
        if (s1 instanceof Polygon && s2 instanceof Polygon) {
            if (Intersector.overlapConvexPolygons((Polygon)s1, (Polygon)s2)) {
                return true;
            }
        } else if (s1 instanceof Polygon && s2 instanceof Circle) {
            if (circleOverlapsPolygon((Circle)s2, (Polygon)s1)) {
                return true;
            }
        } else if (s1 instanceof Circle && s2 instanceof Polygon) {
            if (circleOverlapsPolygon((Circle)s1, (Polygon)s2)) {
                return true;
            }
        } else {
            // Not supported!
        }
        return false;
    }

    public static boolean circleOverlapsPolygon(Circle c, Polygon p) {
        float[] vertices = p.getTransformedVertices();
        int l = vertices.length;
        for (int i = 0; i < vertices.length - 1; i = i + 2) {
            float d;
            if (i == 0) {
                d = Intersector.distanceSegmentPoint(vertices[l - 2], vertices[l - 1], vertices[0], vertices[1], c.x, c.y);
            } else {
                d = Intersector.distanceSegmentPoint(vertices[i - 2], vertices[i - 1], vertices[i], vertices[i + 1], c.x, c.y);
            }
            if (d < c.radius) {
                return true;
            }
        }
        return false;
    }

    public static float getCircleOverlapsPolygonSeqment(Circle c, Polygon p, Vector2 start, Vector2 end) {
        float x1;
        float y1;
        float x2;
        float y2;

        float[] vertices = p.getTransformedVertices();
        int l = vertices.length;
        for (int i = 0; i < vertices.length - 1; i = i + 2) {
            float d;
            if (i == 0) {
                x1 = vertices[l - 2];
                y1 = vertices[l - 1];
                x2 = vertices[0];
                y2 = vertices[1];
            } else {
                x1 = vertices[i - 2];
                y1 = vertices[i - 1];
                x2 = vertices[i];
                y2 = vertices[i + 1];
            }
            d = Intersector.distanceSegmentPoint(x1, y1, x2, y2, c.x, c.y);
            if (d < c.radius) {
                start.x = x1;
                start.y = y1;
                end.x = x2;
                end.y = y2;
                return d;
            }
        }
        return -1;
    }

    public static boolean pointInPolygons(float x, float y, List<Polygon> ps2) {
        for (int i = 0; i < ps2.size(); i++) {
            if (ps2.get(i).contains(x, y)) {
                return true;
            }
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    public static List<Shape2D> asListOfShape2D(List<? extends Shape2D> list) {
        return (List<Shape2D>)list;
    }

    public static <T extends Shape2D> List<Shape2D> asShape2DList(T shape) {
        return Arrays.<Shape2D>asList(shape);
    }

    public static Shape2D findCollidedShape(Shape2D shape2d, List<Shape2D> shapes) {
        for (int i = 0; i < shapes.size(); i++) {
            Shape2D otherShape = shapes.get(i);
            if (overlaps(shape2d, otherShape)) {
                return otherShape;
            }
        }
        return null;
    }
}
