 package org.ah.gcc.virtualrover.game;

import com.badlogic.gdx.math.Polygon;

import java.util.List;

public interface GCCCollidableObject {

    List<Polygon> getCollisionPolygons();
}
