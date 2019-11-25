package org.ah.gcc.virtualrover.game;


import com.badlogic.gdx.math.Polygon;

import org.ah.gcc.virtualrover.game.challenge.Challenge;
import org.ah.gcc.virtualrover.game.challenge.Challenges;
import org.ah.gcc.virtualrover.game.rovers.RoverType;
import org.ah.themvsus.engine.common.game.Game;
import org.ah.themvsus.engine.common.game.GameObject;
import org.ah.themvsus.engine.common.game.GameObjectFactory;
import org.ah.themvsus.engine.common.game.GameObjectWithPosition;
import org.ah.themvsus.engine.common.game.GameState;
import org.ah.themvsus.engine.common.input.PlayerInputs;

import java.util.List;

import static org.ah.gcc.virtualrover.engine.utils.CollisionUtils.polygonsOverlap;

public class GCCGame extends Game {

    private Challenge challenge;
    public static final long ENGINE_LOOP_TIME_us = 8500;

    public GCCGame(String mapId) {
        super();
        if (mapId != null) {
            this.challenge = Challenges.createChallenge(this, mapId);
        }
    }

    public Rover spawnRover(int id, String alias, RoverType roverType) {
        Rover rover = getGameObjectFactory().newGameObjectWithId(roverType.getGameObjectType(), id);
        rover.updateAlias(alias);

        addNewGameObject(rover);

        players.add(id);
        return rover;
    }

    @Override
    protected GameObjectFactory createGameFactory() {
        return new GCCGameObjectFactory();
    }

    @Override
    public boolean checkForCollision(GameObjectWithPosition object, Iterable<GameObjectWithPosition> objects) {
        if (challenge != null && challenge.checkForCollision(object, objects)) {
            return true;
        }

        if (object instanceof GCCCollidableObject) {
            List<Polygon> roverPolygon = ((GCCCollidableObject)object).getCollisionPolygons();
            for (GameObjectWithPosition o : objects) {
                if (o != object && o instanceof Rover) {
                    List<Polygon> otherRoverPolygon = ((GCCCollidableObject)o).getCollisionPolygons();
                    if (polygonsOverlap(roverPolygon, otherRoverPolygon)) {
                        return true;
                    }
                }
            }
        }

        return super.checkForCollision(object, objects);
    }

    @Override
    public GameState process() { // ServerEngine.mainloop
        if (challenge != null) {
            challenge.process(getCurrentGameState());
        }
        return super.process();
    }

    @Override
    public void processPlayerInputs(int playerId, PlayerInputs playerInputs) {
        if (challenge == null || challenge.processPlayerInputs(playerId, playerInputs)) {
            super.processPlayerInputs(playerId, playerInputs);
        }
    }

    @Override
    protected void fireGameObjectAdded(GameObject newGameObject) {
        if (challenge != null) {
            challenge.gameObjectAdded(newGameObject);
        }
        super.fireGameObjectAdded(newGameObject);
    }

    @Override
    protected void fireObjectRemoved(GameObject objectToRemove) {
        if (challenge != null) {
            challenge.gameObjectRemoved(objectToRemove);
        }
        super.fireObjectRemoved(objectToRemove);
    }

    public Challenge getChallenge() {
        return this.challenge;
    }
}
