 package org.ah.piwars.virtualrover.game;

import com.badlogic.gdx.math.Shape2D;

import java.util.List;

public interface PiWarsCollidableObject {

    List<Shape2D> getCollisionPolygons();
}
