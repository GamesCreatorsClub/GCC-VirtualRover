package org.ah.piwars.virtualrover.game.challenge;

import com.badlogic.gdx.math.Shape2D;

import org.ah.themvsus.engine.common.game.GameObject;
import org.ah.themvsus.engine.common.game.GameObjectWithPosition;
import org.ah.themvsus.engine.common.game.GameState;
import org.ah.themvsus.engine.common.input.PlayerInputs;

import java.util.List;

public interface Challenge {

    void process(GameState currentGameState);

    List<? extends Shape2D> getCollisionPolygons();

    boolean checkForCollision(GameObjectWithPosition object, Iterable<GameObjectWithPosition> objects);

    boolean processPlayerInputs(int playerId, PlayerInputs playerInputs);

    String getName();

    void beforeGameObjectAdded(GameObject gameObject);

    void afterGameObjectAdded(GameObject gameObject);

    void gameObjectRemoved(GameObject gameObject);
}
