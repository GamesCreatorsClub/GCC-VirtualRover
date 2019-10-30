package org.ah.gcc.virtualrover.game.challenge;

import com.badlogic.gdx.math.Polygon;

import org.ah.gcc.virtualrover.game.GCCPlayer;
import org.ah.themvsus.engine.common.game.GameObjectWithPosition;

import java.util.List;

public interface Challenge {

    List<Polygon> getCollisionPolygons();

    void spawnedPlayer(GCCPlayer player);

    boolean checkForCollision(GameObjectWithPosition object, Iterable<GameObjectWithPosition> objects);
}
