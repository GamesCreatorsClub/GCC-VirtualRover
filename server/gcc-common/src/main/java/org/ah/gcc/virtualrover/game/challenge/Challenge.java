package org.ah.gcc.virtualrover.game.challenge;

import com.badlogic.gdx.math.Polygon;

import org.ah.themvsus.engine.common.game.GameObjectWithPosition;

import java.util.List;

public interface Challenge {

    void process();

    List<Polygon> getCollisionPolygons();

    boolean checkForCollision(GameObjectWithPosition object, Iterable<GameObjectWithPosition> objects);
}
