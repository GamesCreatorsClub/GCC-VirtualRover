 package org.ah.gcc.virtualrover.game;

import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector3;

import java.util.List;

public interface GCCCollidableObject {

    static Vector3 Z_AXIS = new Vector3(0f, 0f, 1f);

    List<Polygon> getCollisionPolygons();
}
