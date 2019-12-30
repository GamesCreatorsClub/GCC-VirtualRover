package org.ah.gcc.virtualrover.game;


import com.badlogic.gdx.math.Polygon;

import org.ah.gcc.virtualrover.game.challenge.Challenge;
import org.ah.gcc.virtualrover.game.challenge.Challenges;
import org.ah.gcc.virtualrover.game.rovers.Rover;
import org.ah.gcc.virtualrover.game.rovers.RoverType;
import org.ah.themvsus.engine.common.game.Game;
import org.ah.themvsus.engine.common.game.GameObject;
import org.ah.themvsus.engine.common.game.GameObjectFactory;
import org.ah.themvsus.engine.common.game.GameObjectWithPosition;
import org.ah.themvsus.engine.common.input.PlayerInputs;

import java.util.List;

import static org.ah.gcc.virtualrover.engine.utils.CollisionUtils.polygonsOverlap;

public class GCCGame extends Game {

    public static int GAME_TICK_IN_us = 16000;

    private Challenge challenge;

    public GCCGame(String mapId) {
        super(GAME_TICK_IN_us);
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
    protected void postProcessGameState() {
        if (challenge != null) {
            try {
                challenge.process(getCurrentGameState());
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
        super.postProcessGameState();
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
            challenge.beforeGameObjectAdded(newGameObject);
        }

        super.fireGameObjectAdded(newGameObject);

        if (challenge != null) {
            challenge.afterGameObjectAdded(newGameObject);
        }
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
