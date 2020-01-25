package org.ah.piwars.virtualrover.game;


import com.badlogic.gdx.math.Shape2D;

import org.ah.piwars.virtualrover.game.challenge.Challenge;
import org.ah.piwars.virtualrover.game.challenge.Challenges;
import org.ah.piwars.virtualrover.game.rovers.Rover;
import org.ah.piwars.virtualrover.game.rovers.RoverType;
import org.ah.themvsus.engine.common.game.Game;
import org.ah.themvsus.engine.common.game.GameObject;
import org.ah.themvsus.engine.common.game.GameObjectFactory;
import org.ah.themvsus.engine.common.game.GameObjectWithPosition;
import org.ah.themvsus.engine.common.input.PlayerInputs;

import java.util.List;

import static org.ah.piwars.virtualrover.engine.utils.CollisionUtils.polygonsOverlap;

public class PiWarsGame extends Game {

    public static int GAME_TICK_IN_us = 16000;

    private Challenge challenge;

    public PiWarsGame(String mapId) {
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
        return new PiWarsGameObjectFactory();
    }

    @Override
    public boolean checkForCollision(GameObjectWithPosition object, Iterable<GameObjectWithPosition> objects) {
        if (challenge != null && challenge.checkForCollision(object, objects)) {
            return true;
        }

        if (object instanceof PiWarsCollidableObject) {
            List<Shape2D> roverPolygon = ((PiWarsCollidableObject)object).getCollisionPolygons();
            for (GameObjectWithPosition o : objects) {
                if (o != object && o instanceof PiWarsCollidableObject) {
                    List<Shape2D> otherObjectPolygon = ((PiWarsCollidableObject)o).getCollisionPolygons();
                    if (polygonsOverlap(roverPolygon, otherObjectPolygon)) {
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
