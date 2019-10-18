package org.ah.gcc.virtualrover.utils;

import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.collision.BoundingBox;

import java.util.List;

public class MeshUtils {

    public static Mesh createRect(float x, float y, float width, float height) {
        Mesh mesh = new Mesh(true, 4, 6, VertexAttribute.Position(), VertexAttribute.ColorUnpacked(), VertexAttribute.TexCoords(0));

        mesh.setVertices(new float[] { -1 * width + x, -1, -1 * height + y, 1, 0, 1, 1, 0, 0, 1 * width + x, -1, -1 * height + y, 1, 0, 1, 1, 1, 0,
                1 * width + x, -1, 1 * height + y, 1, 0, 1, 1, 1, 1, -1 * width + x, -1, 1 * height + y, 1, 0, 1, 1, 0, 1 });

        mesh.setIndices(new short[] { 2, 1, 0, 0, 3, 2 });
        return mesh;
    }


    public static Polygon polygonFromModelInstance(ModelInstance modelInstance) {
        BoundingBox boundingBox = new BoundingBox();
        modelInstance.calculateBoundingBox(boundingBox);
        return polygonFromBoundingBox(boundingBox);
    }

    public static Polygon polygonFromBoundingBox(BoundingBox boundingBox) {
        float minX = boundingBox.min.x;
        float minZ = boundingBox.min.z;
        float maxX = boundingBox.max.x;
        float maxZ = boundingBox.max.z;

        float[] polygonVertices = new float[] {
                minX, minZ,
                minX, maxZ,
                maxX, maxZ,
                maxX, minZ };
        Polygon polygon = new Polygon(polygonVertices);
        polygon.setOrigin((maxX + minX)  / 2, (minZ + maxZ) / 2);
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
}
