package org.ah.piwars.virtualrover.game.challenge;

import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.IntArray;

import org.ah.piwars.virtualrover.game.GameMessageObject;
import org.ah.piwars.virtualrover.game.PiWarsGame;
import org.ah.piwars.virtualrover.game.PiWarsGameTypeObject;
import org.ah.piwars.virtualrover.game.objects.ToyCubeObject;
import org.ah.piwars.virtualrover.game.objects.ToyCubeObject.ToyCubeColour;
import org.ah.piwars.virtualrover.game.physics.Box2DPhysicsWorld;
import org.ah.piwars.virtualrover.game.rovers.Rover;
import org.ah.themvsus.engine.common.game.Game;
import org.ah.themvsus.engine.common.game.GameObject;
import org.ah.themvsus.engine.common.game.GameObjectWithPosition;
import org.ah.themvsus.engine.common.game.GameState;
import org.ah.themvsus.engine.common.input.PlayerInputs;
import org.ah.themvsus.engine.common.statemachine.State;
import org.ah.themvsus.engine.common.statemachine.StateMachine;

import java.util.List;

import static org.ah.piwars.virtualrover.engine.utils.CollisionUtils.polygonFromBox;

import static java.util.Arrays.asList;

public class TidyUpTheToysChallenge extends CameraAbstractChallenge implements Box2DPhysicalWorldSimulationChallenge {

    public static final float CHALLENGE_WIDTH = 1500;
    public static float WALL_HEIGHT = 200;

    public static final Polygon FLOOR_POLYGON = polygonFromBox(-CHALLENGE_WIDTH / 2, -CHALLENGE_WIDTH / 2, CHALLENGE_WIDTH / 2, CHALLENGE_WIDTH / 2);

    public static final List<Polygon> WALL_POLYGONS = asList(
            polygonFromBox(-CHALLENGE_WIDTH / 2, -CHALLENGE_WIDTH / 2 - 1,  CHALLENGE_WIDTH / 2, -CHALLENGE_WIDTH / 2),
            polygonFromBox(-CHALLENGE_WIDTH / 2 - 1, -CHALLENGE_WIDTH / 2, -CHALLENGE_WIDTH / 2,  CHALLENGE_WIDTH / 2),
            polygonFromBox(-CHALLENGE_WIDTH / 2,  CHALLENGE_WIDTH / 2,  CHALLENGE_WIDTH / 2,  CHALLENGE_WIDTH / 2 + 1),
            polygonFromBox( CHALLENGE_WIDTH / 2, -CHALLENGE_WIDTH / 2,  CHALLENGE_WIDTH / 2 + 1,  CHALLENGE_WIDTH / 2));

    private IntArray cubes = new IntArray();
    private int redCubeId;
    private int greenCubeId;
    private int blueCubeId;

    private StateMachine<TidyUpTheToysChallenge, ChallengeState> stateMachine = new StateMachine<TidyUpTheToysChallenge, ChallengeState>();

    public Box2DPhysicsWorld physicsWorld;

    private Rectangle tidyUpArea;

    public TidyUpTheToysChallenge(PiWarsGame game, String name) {
        super(game, name);
        setWallPolygons(WALL_POLYGONS);
        stateMachine.setCurrentState(ChallengeState.WAITING_START);

        physicsWorld = new Box2DPhysicsWorld(game, getCollisionPolygons());

        tidyUpArea = new Rectangle(-750, -300, 150, 350);
    }

    @Override
    public Box2DPhysicsWorld getBox2DPhysicalWorld() {
        return physicsWorld;
    }

    @Override
    protected boolean tryMovingRover(Rover rover, Iterable<GameObjectWithPosition> objects) {
        return physicsWorld.tryMovingRover(rover, objects);
    }

    @Override
    public void process(GameState currentGameState) {
        stateMachine.update(this);
        physicsWorld.updateWorld();
    }

    @Override
    public void beforeGameObjectAdded(GameObject gameObject) {
        super.beforeGameObjectAdded(gameObject);
    }

    @Override
    public void afterGameObjectAdded(GameObject gameObject) {
        super.afterGameObjectAdded(gameObject);
    }

    @Override
    public void gameObjectRemoved(GameObject gameObject) {
        super.gameObjectRemoved(gameObject);
    }

    @Override
    protected void resetRover() {
        Rover player1 = getRover();
        if (player1 != null) {
            orientation.setEulerAnglesRad(0f, 0f, (float)(Math.PI + Math.PI / 2f));
            player1.setPosition(0, -600);
            player1.setOrientation(orientation);
        }
    }

    private ToyCubeObject fetchObjectFromId(int id, ToyCubeColour color) {
        ToyCubeObject toyCube = null;
        if (id != 0) {
            toyCube = piwarsGame.getCurrentGameState().get(id);
        }
        if (toyCube == null) {
            toyCube = piwarsGame.getGameObjectFactory().newGameObjectWithId(PiWarsGameTypeObject.ToyCubeObject, piwarsGame.newId());
            toyCube.setColour(color);
            cubes.add(toyCube.getId());
            piwarsGame.addNewGameObjectImmediately(toyCube);
        } else {
            toyCube.setColour(color);
        }
        return toyCube;
    }

    private void resetKubes() {
        ToyCubeObject redCube = fetchObjectFromId(redCubeId, ToyCubeColour.RED);
        redCubeId = redCube.getId();

        ToyCubeObject greenCube = fetchObjectFromId(greenCubeId, ToyCubeColour.GREEN);
        greenCubeId = greenCube.getId();

        ToyCubeObject blueCube = fetchObjectFromId(blueCubeId, ToyCubeColour.BLUE);
        blueCubeId = blueCube.getId();

        redCube.setPosition(0,  350);
        greenCube.setPosition(-400,  350);
        blueCube.setPosition(400,  350);

        physicsWorld.updateObjectPositions();
    }

    @Override
    public boolean processPlayerInputs(int playerId, PlayerInputs playerInputs) {
        boolean doMove = stateMachine.getCurrentState().shouldMoveRovers();

        return doMove;
    }

    private boolean isComplete() {

        if (redCubeId <= 0 || greenCubeId <= 0 || blueCubeId <= 0) {
            return false;
        }

        ToyCubeObject redObject = piwarsGame.getCurrentGameState().get(redCubeId);
        ToyCubeObject greenObject = piwarsGame.getCurrentGameState().get(greenCubeId);
        ToyCubeObject blueObject = piwarsGame.getCurrentGameState().get(blueCubeId);

        if (redObject == null || greenObject == null || blueObject == null) {
            return false;
        }

        if (!tidyUpArea.contains(redObject.getPosition().x, redObject.getPosition().y)
                || !tidyUpArea.contains(greenObject.getPosition().x, greenObject.getPosition().y)
                || !tidyUpArea.contains(blueObject.getPosition().x, blueObject.getPosition().y)) {
            return false;
        }

        if (blueObject.getPosition().y < greenObject.getPosition().y) {
            return false;
        }

        if (greenObject.getPosition().y < redObject.getPosition().y) {
            return false;
        }

        return true;
    }

    private enum ChallengeState implements State<TidyUpTheToysChallenge> {

        WAITING_START() {
            @Override public void enter(TidyUpTheToysChallenge challenge) {
                Game game = challenge.getGame();

                if (game.containsObject(challenge.playerId)) {
                    game.removeGameObject(challenge.playerId);
                    challenge.playerId = 0;
                }
                for (int objectId : challenge.cubes.items) {
                    GameObject gameObject = game.getCurrentGameState().get(objectId);
                    if (gameObject != null) {
                        game.removeGameObject(objectId);
                    }
                }
                 challenge.cubes.clear();

                GameMessageObject gameMessageObject = challenge.getGameMessage();
                gameMessageObject.setInGame(false);
                gameMessageObject.setWaiting(true);

                challenge.removeTimer();
            }

            @Override public void update(TidyUpTheToysChallenge challenge) {
                if (challenge.playerId != 0) {
                    challenge.stateMachine.toState(ChallengeState.GAME, challenge);
                }
            }

            @Override public void exit(TidyUpTheToysChallenge challenge) {
                challenge.stopRovers();
            }
        },

        GAME() {
            @Override public boolean shouldMoveRovers() { return true; }

            @Override public void enter(TidyUpTheToysChallenge challenge) {
                challenge.startTimer(3000);
                //challenge.startTimer(100);
                challenge.resetRover();
                challenge.resetKubes();
                setTimer(1000);

                GameMessageObject gameMessageObject = challenge.getGameMessage();
                gameMessageObject.setMessage("GO!", false);
                gameMessageObject.setInGame(true);
                gameMessageObject.setWaiting(false);

                // TODO
                // challenge.soundManager.playFight();
            }

            @Override public void update(TidyUpTheToysChallenge challenge) {
                super.update(challenge);

                if (isTimerDone()) {
                    challenge.setMessage(null, false);
                }

                GameMessageObject gameMessageObject = challenge.getGameMessage();
                if (gameMessageObject.getTimer() <= 0) {
                    challenge.stopTimer();
                    challenge.getGameMessage().setMessage("Time is up!", false);
                    challenge.stateMachine.toState(ChallengeState.END, challenge);
                }

                if (challenge.isComplete()) {
                    challenge.stopTimer();
                    challenge.getGameMessage().setMessage("You have tidied the toys! Well done!", false);
                    challenge.stateMachine.toState(ChallengeState.END, challenge);
                }
            }
        },

        END() {
            @Override public boolean shouldMoveRovers() { return true; }

            @Override public void enter(TidyUpTheToysChallenge challenge) {
                GameMessageObject gameMessageObject = challenge.getGameMessage();
                gameMessageObject.setInGame(false);
                gameMessageObject.setWaiting(false);
                challenge.stopTimer();

                setTimer(3000);
            }

            @Override public void update(TidyUpTheToysChallenge challenge) {
                if (isTimerDone()) {
                    challenge.stateMachine.toState(WAITING_START, challenge);
                }
            }

            @Override public void exit(TidyUpTheToysChallenge challenge) {
                Rover rover = challenge.getRover();
                if (rover != null) {
                    challenge.piwarsGame.removeGameObject(rover.getId());
                    challenge.playerId = 0;
                }
                challenge.setMessage(null, false);
                challenge.removeTimer();
            }
        };

        long timer;

        protected void setTimer(int millis) {
            timer = System.currentTimeMillis() + millis;
        }

        protected boolean isTimerDone() {
            return (timer <= System.currentTimeMillis());
        }

        @Override public void enter(TidyUpTheToysChallenge challenge) {}
        @Override public void update(TidyUpTheToysChallenge challenge) {}
        @Override public void exit(TidyUpTheToysChallenge s) {}

        public boolean shouldMoveRovers() { return false; }
    }
}
