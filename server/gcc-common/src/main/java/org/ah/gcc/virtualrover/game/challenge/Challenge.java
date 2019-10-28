package org.ah.gcc.virtualrover.game.challenge;

import com.badlogic.gdx.math.Polygon;

import java.util.List;

public interface Challenge {

    List<Polygon> getCollisionPolygons();

}
