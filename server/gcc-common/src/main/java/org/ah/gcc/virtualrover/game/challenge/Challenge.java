package org.ah.gcc.virtualrover.game.challenge;

import com.badlogic.gdx.math.Polygon;

import org.ah.gcc.virtualrover.game.GCCGame;
import org.ah.gcc.virtualrover.game.GCCPlayer;
import org.ah.themvsus.engine.common.game.GameObjectWithPosition;
import org.ah.themvsus.engine.common.game.GameState;

import java.util.List;

public interface Challenge {

    void process(GCCGame gccGame, GameState newGameState);

    List<Polygon> getCollisionPolygons();

    void spawnedPlayer(GCCPlayer player);

    boolean checkForCollision(GameObjectWithPosition object, Iterable<GameObjectWithPosition> objects);
}
