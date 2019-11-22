package org.ah.gcc.virtualrover.game.challenge;

import com.badlogic.gdx.math.Polygon;

import org.ah.themvsus.engine.common.game.GameObjectWithPosition;
import org.ah.themvsus.engine.common.input.PlayerInputs;

import java.util.List;

public interface Challenge {

    void process();

    List<Polygon> getCollisionPolygons();

    boolean checkForCollision(GameObjectWithPosition object, Iterable<GameObjectWithPosition> objects);

    boolean processPlayerInputs(int playerId, PlayerInputs playerInputs);

    String getName();
}
