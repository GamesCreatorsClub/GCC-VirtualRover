 package org.ah.piwars.virtualrover.game;

import com.badlogic.gdx.math.Polygon;

import java.util.List;

public interface PiWarsCollidableObject {

    List<Polygon> getCollisionPolygons();
}
