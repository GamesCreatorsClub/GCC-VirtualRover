package org.ah.piwars.virtualrover.game.challenge;

import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Shape2D;

import org.ah.piwars.virtualrover.game.GameMessageObject;
import org.ah.piwars.virtualrover.game.PiWarsGame;
import org.ah.piwars.virtualrover.game.PiWarsGameTypeObject;
import org.ah.piwars.virtualrover.game.rovers.Rover;
import org.ah.themvsus.engine.common.game.Game;
import org.ah.themvsus.engine.common.game.GameObject;
import org.ah.themvsus.engine.common.game.GameObjectWithPosition;
import org.ah.themvsus.engine.common.game.GameState;

import java.util.List;

import static org.ah.piwars.virtualrover.engine.utils.CollisionUtils.asListOfShape2D;
import static org.ah.piwars.virtualrover.engine.utils.CollisionUtils.polygonsOverlap;

import static java.util.Collections.emptyList;

public abstract class AbstractChallenge implements Challenge {

    protected PiWarsGame piwarsGame;

    protected String name;

    protected int playerId;
    protected int gameMessageId;
    protected GameMessageObject cachedGameMessageObject;
    private GameState gameStateGameMessageIsDefinedOn;

    protected Quaternion orientation = new Quaternion();

    protected List<Shape2D> wallPolygons = emptyList();

    protected AbstractChallenge(PiWarsGame piwarsGame, String name) {
        this.piwarsGame = piwarsGame;
        this.name = name;
    }

    protected void setWallPolygons(List<? extends Shape2D> wallPolygons) {
        this.wallPolygons = asListOfShape2D(wallPolygons);
    }

    @Override
    public List<Shape2D> getCollisionPolygons() {
        return wallPolygons;
    }

    public Game getGame() {
        return piwarsGame;
    }

    @Override
    public String getName() {
        return name;
    }

    protected void setMessage(String message, boolean flashing) {
        getGameMessage().setMessage(message, flashing);
    }

    protected GameMessageObject getGameMessage() {
        GameMessageObject gameMessageObject = null;

        if (gameMessageId != 0) {
            GameState currentGameState = piwarsGame.getCurrentGameState();
            gameMessageObject = piwarsGame.getCurrentGameState().get(gameMessageId);
            if (gameMessageObject != null && gameStateGameMessageIsDefinedOn != null) {
                gameStateGameMessageIsDefinedOn = null;
                cachedGameMessageObject = null;
            } else {
                if (gameStateGameMessageIsDefinedOn != null) {
                    if (currentGameState == gameStateGameMessageIsDefinedOn) {
                        gameMessageObject = cachedGameMessageObject;
                    } else {
                        gameStateGameMessageIsDefinedOn = null;
                        cachedGameMessageObject = null;
                    }
                }
            }
        }

        if (gameMessageObject == null) {
            gameStateGameMessageIsDefinedOn = piwarsGame.getCurrentGameState();
            gameMessageObject = (GameMessageObject) piwarsGame.getGameObjectFactory().newGameObjectWithId(PiWarsGameTypeObject.GameMessageObject, piwarsGame.newId());
            cachedGameMessageObject = gameMessageObject;
            piwarsGame.addNewGameObject(gameMessageObject);
            gameMessageId = gameMessageObject.getId();
        }
        return gameMessageObject;
    }

    protected Rover getRover() {
        if (playerId != 0) {
            return piwarsGame.getCurrentGameState().get(playerId);
        }
        return null;
    }

    protected void stopRovers() {
        Rover player1 = getRover();
        if (player1 != null) {
            player1.setVelocity(0, 0);
            player1.setTurnSpeed(0);
            player1.setSpeed(0);
        }
    }

    @Override
    public void beforeGameObjectAdded(GameObject gameObject) {
    }

    @Override
    public void afterGameObjectAdded(GameObject gameObject) {
        if (gameObject instanceof Rover) {
            playerId = gameObject.getId();
            resetRover();
        }
    }

    @Override
    public void gameObjectRemoved(GameObject gameObject) {
        if (gameObject instanceof Rover) {
            playerId = 0;
        }
    }

    @Override
    public boolean checkForCollision(GameObjectWithPosition object, Iterable<GameObjectWithPosition> objects) {
        if (object instanceof Rover) {
            Rover rover = (Rover)object;

            // Check going outside of arena
            if (polygonsOverlap(getCollisionPolygons(), rover.getCollisionPolygons())) {
                return true;
            }
            return tryMovingRover(rover, objects);
        }

        return false;
    }

    /**
     *
     * @param rover
     * @param objects
     * @return <code>false</code> if there are not obstacles
     */
    protected boolean tryMovingRover(Rover rover, Iterable<GameObjectWithPosition> objects) {
        return false;
    }

    protected abstract void resetRover();
}
