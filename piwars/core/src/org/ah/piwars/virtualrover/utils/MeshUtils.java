package org.ah.piwars.virtualrover.utils;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.g3d.utils.shapebuilders.BaseShapeBuilder;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.collision.BoundingBox;

import java.util.List;

public class MeshUtils extends BaseShapeBuilder {

    public static Mesh createRect(float x, float y, float z, float width, float height) {
        Mesh mesh = new Mesh(true, 4, 6, VertexAttribute.Position(), VertexAttribute.ColorUnpacked(), VertexAttribute.TexCoords(0));

        mesh.setVertices(new float[] {
                -1 * width + x, z, -1 * height + y, 1, 0, 1, 1, 0, 0,
                 1 * width + x, z, -1 * height + y, 1, 0, 1, 1, 1, 0,
                 1 * width + x, z,  1 * height + y, 1, 0, 1, 1, 1, 1,
                -1 * width + x, z,  1 * height + y, 1, 0, 1, 1, 0, 1
            });

        mesh.setIndices(new short[] { 2, 1, 0, 0, 3, 2 });
        return mesh;
    }

    public static Mesh createRect(float x, float y, float z, float width, float height, Color color) {
        Mesh mesh = new Mesh(true, 4, 6, VertexAttribute.Position(), VertexAttribute.ColorUnpacked(), VertexAttribute.TexCoords(0));

        mesh.setVertices(new float[] {
                -1 * width + x, z, -1 * height + y, color.r, color.g, color.b, color.a, 0, 1,
                 1 * width + x, z, -1 * height + y, color.r, color.g, color.b, color.a, 1, 1,
                 1 * width + x, z,  1 * height + y, color.r, color.g, color.b, color.a, 1, 0,
                -1 * width + x, z,  1 * height + y, color.r, color.g, color.b, color.a, 0, 0
            });

        mesh.setIndices(new short[] { 2, 1, 0, 0, 3, 2 });
        return mesh;
    }

    public static Model flatPolygon(ModelBuilder modelBuilder, Polygon polygon, float y, int attributes, Material material) {
        modelBuilder.begin();
        MeshPartBuilder meshPartBuilder = modelBuilder.part("box", GL20.GL_TRIANGLES, attributes, material);
        flatPolygon(meshPartBuilder, polygon, y);
        return modelBuilder.end();
    }

    public static Model extrudePolygonY(ModelBuilder modelBuilder, Polygon polygon, float depth, int attributes, Material material) {
        modelBuilder.begin();
        MeshPartBuilder meshPartBuilder = modelBuilder.part("box", GL20.GL_TRIANGLES, attributes, material);
        extrudePolygonY(meshPartBuilder, polygon, depth);
        return modelBuilder.end();
    }

    public static void flatPolygon(MeshPartBuilder builder, Polygon polygon, float y) {
        float[] polygon_vertices = polygon.getVertices();
        int polygon_points = polygon_vertices.length / 2;
        builder.ensureVertices(polygon_points);
        builder.ensureRectangleIndices(polygon_points - 2);

        Rectangle boundingRectangle = polygon.getBoundingRectangle();
        float minx = boundingRectangle.x;
        float miny = boundingRectangle.y;
        float width = boundingRectangle.width;
        float height = boundingRectangle.height;
        float y1 = 0f;
        float x1 = polygon_vertices[0];
        float z1 = - polygon_vertices[1];
        for (int i = 1; i < polygon_points - 1; i++) {
            float x2 = polygon_vertices[i * 2];
            float z2 = - polygon_vertices[i * 2 + 1];
            float x3 = polygon_vertices[i * 2 + 2];
            float z3 = - polygon_vertices[i * 2 + 3];

            vertTmp1.setPos(x1, y1, z1).setNor(0f, 1f, 0f).setUV((x1 - minx) / width, (z1 - miny) / height);
            vertTmp2.setPos(x2, y1, z2).setNor(0f, 1f, 0f).setUV((x2 - minx) / width, (z2 - miny) / height);
            vertTmp3.setPos(x3, y1, z3).setNor(0f, 1f, 0f).setUV((x3 - minx) / width, (z3 - miny) / height);
            builder.triangle(vertTmp2, vertTmp1, vertTmp3);
        }
    }

    public static void extrudePolygonY(MeshPartBuilder builder, Polygon polygon, float depth) {
        float[] polygon_vertices = polygon.getVertices();
        int polygon_points = polygon_vertices.length / 2;
        builder.ensureVertices(polygon_points * 4);
        builder.ensureRectangleIndices((polygon_points - 2) * 2 + polygon_points * 2);

        Rectangle boundingRectangle = polygon.getBoundingRectangle();
        float minx = boundingRectangle.x;
        float miny = boundingRectangle.y;
        float width = boundingRectangle.width;
        float height = boundingRectangle.height;
        float y1 = depth / 2.0f;
        float y2 = - depth / 2.0f;
        float x1 = polygon_vertices[0];
        float z1 = - polygon_vertices[1];
        for (int i = 1; i < polygon_points - 1; i++) {
            float x2 = polygon_vertices[i * 2];
            float z2 = - polygon_vertices[i * 2 + 1];
            float x3 = polygon_vertices[i * 2 + 2];
            float z3 = - polygon_vertices[i * 2 + 3];

            vertTmp1.setPos(x1, y1, z1).setNor(0f, 1f, 0f).setUV((x1 - minx) / width, (z1 - miny) / height);
            vertTmp2.setPos(x2, y1, z2).setNor(0f, 1f, 0f).setUV((x2 - minx) / width, (z2 - miny) / height);
            vertTmp3.setPos(x3, y1, z3).setNor(0f, 1f, 0f).setUV((x3 - minx) / width, (z3 - miny) / height);
            builder.triangle(vertTmp2, vertTmp1, vertTmp3);

            vertTmp1.setPos(x1, y2, z1).setNor(0f, -1f, 0f).setUV((x1 - minx) / width, (z1 - miny) / height);
            vertTmp2.setPos(x2, y2, z2).setNor(0f, -1f, 0f).setUV((x2 - minx) / width, (z2 - miny) / height);
            vertTmp3.setPos(x3, y2, z3).setNor(0f, -1f, 0f).setUV((x3 - minx) / width, (z3 - miny) / height);
            builder.triangle(vertTmp1, vertTmp2, vertTmp3);
        }
        for (int i = 0; i < polygon_points; i++) {
            x1 = polygon_vertices[i * 2];
            z1 = - polygon_vertices[i * 2 + 1];
            int j = i + 1;
            if (j >= polygon_points) {
                j = 0;
            }
            float x2 = polygon_vertices[j * 2];
            float z2 = - polygon_vertices[j * 2 + 1];

            float dx = x2 - x1;
            float dz = z2 - z1;

            tmpV1.set(dz, 0, -dx).nor();

            builder.rect(
                    x1, y1, z1, x2, y1, z2, x2, y2, z2, x1, y2, z1,
                    tmpV1.x, 0f, tmpV1.z);
        }
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
