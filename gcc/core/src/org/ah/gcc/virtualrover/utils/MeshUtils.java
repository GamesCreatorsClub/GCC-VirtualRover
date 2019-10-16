package org.ah.gcc.virtualrover.utils;

import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;

public class MeshUtils {

    public static Mesh createRect(float x, float y, float width, float height) {
        Mesh mesh = new Mesh(true, 4, 6, VertexAttribute.Position(), VertexAttribute.ColorUnpacked(), VertexAttribute.TexCoords(0));

        mesh.setVertices(new float[] { -1 * width + x, -1, -1 * height + y, 1, 0, 1, 1, 0, 0, 1 * width + x, -1, -1 * height + y, 1, 0, 1, 1, 1, 0,
                1 * width + x, -1, 1 * height + y, 1, 0, 1, 1, 1, 1, -1 * width + x, -1, 1 * height + y, 1, 0, 1, 1, 0, 1 });

        mesh.setIndices(new short[] { 2, 1, 0, 0, 3, 2 });
        return mesh;
    }
}
