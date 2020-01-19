package org.ah.piwars.virtualrover.engine.utils;

import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Polygon;

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

    public static boolean polygonsOverlap(List<Polygon> ps1, List<Polygon> ps2) {
        for (int i = 0; i < ps1.size(); i++) {
            for (int j = 0; j < ps2.size(); j++) {
                if (Intersector.overlapConvexPolygons(ps1.get(i), ps2.get(j))) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean polygonsOverlap(Polygon poligon, List<Polygon> ps2) {
        for (int i = 0; i < ps2.size(); i++) {
            if (Intersector.overlapConvexPolygons(poligon, ps2.get(i))) {
                return true;
            }
        }
        return false;
    }

    public static boolean pointInPolygons(float x, float y, List<Polygon> ps2) {
        for (int i = 0; i < ps2.size(); i++) {
            if (ps2.get(i).contains(x, y)) {
                return true;
            }
        }
        return false;
    }

}
