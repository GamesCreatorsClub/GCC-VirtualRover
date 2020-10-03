package org.ah.piwars.virtualrover.game.challenge;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.IntArray;

import org.ah.piwars.virtualrover.game.GameMessageObject;
import org.ah.piwars.virtualrover.game.PiWarsGame;
import org.ah.piwars.virtualrover.game.PiWarsGameTypeObject;
import org.ah.piwars.virtualrover.game.objects.BarrelObject;
import org.ah.piwars.virtualrover.game.objects.BarrelObject.BarrelColour;
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
import java.util.Random;

import static org.ah.piwars.virtualrover.engine.utils.CollisionUtils.polygonFromBox;

import static java.util.Arrays.asList;

public class EcoDisasterChallenge extends CameraAbstractChallenge implements Box2DPhysicalWorldSimulationChallenge {

    public static final float CHALLENGE_WIDTH = 2200;
    public static final float BARRELS_AREA = 1600;

    public static final List<Polygon> WALL_POLYGONS = asList(
            polygonFromBox(-CHALLENGE_WIDTH / 2, -CHALLENGE_WIDTH / 2 - 1,  CHALLENGE_WIDTH / 2, -CHALLENGE_WIDTH / 2),
            polygonFromBox(-CHALLENGE_WIDTH / 2 - 1, -CHALLENGE_WIDTH / 2, -CHALLENGE_WIDTH / 2,  CHALLENGE_WIDTH / 2),
            polygonFromBox(-CHALLENGE_WIDTH / 2,  CHALLENGE_WIDTH / 2,  CHALLENGE_WIDTH / 2,  CHALLENGE_WIDTH / 2 + 1),
            polygonFromBox( CHALLENGE_WIDTH / 2, -CHALLENGE_WIDTH / 2,  CHALLENGE_WIDTH / 2 + 1,  CHALLENGE_WIDTH / 2));

    private int numberOfBarrels = 12;
    private IntArray barrels = new IntArray();

    private Random random = new Random();

    private StateMachine<EcoDisasterChallenge, ChallengeState> stateMachine = new StateMachine<EcoDisasterChallenge, ChallengeState>();

    private Box2DPhysicsWorld physicsWorld;

    private Rectangle greenArea;
    private Rectangle redArea;

    public EcoDisasterChallenge(PiWarsGame game, String name) {
        super(game, name);
        setWallPolygons(WALL_POLYGONS);
        stateMachine.setCurrentState(ChallengeState.WAITING_START);

        physicsWorld = new Box2DPhysicsWorld(game, getCollisionPolygons());

        greenArea = new Rectangle( -700, CHALLENGE_WIDTH / 2 - 210, 600, 210);
        redArea = new Rectangle( 100, CHALLENGE_WIDTH / 2 - 210, 600, 210);
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
        if (gameObject instanceof Rover) {

        }
    }

    @Override
    public void gameObjectRemoved(GameObject gameObject) {
        super.gameObjectRemoved(gameObject);
    }

    @Override
    protected void resetRover() {
        Rover player1 = getRover();
        if (player1 != null) {
            orientation.setEulerAnglesRad(0f, 0f, (float)(Math.PI + Math.PI / 4f));
            player1.setPosition(0, 700);
            player1.setOrientation(orientation);
        }
    }

    private void resetBarrels() {
        boolean odd = true;

        while (barrels.size < numberOfBarrels) {
            BarrelObject barrel = piwarsGame.getGameObjectFactory().newGameObjectWithId(PiWarsGameTypeObject.BarrelObject, piwarsGame.newId());
            if (odd) {
                barrel.setBarrelColour(BarrelColour.GREEN);
            } else {
                barrel.setBarrelColour(BarrelColour.RED);
            }

            float x = random.nextInt((int)BARRELS_AREA) - BARRELS_AREA / 2;
            float y = random.nextInt((int)BARRELS_AREA) - BARRELS_AREA / 2;

            while (overlapsOtherBarrles(x, y, barrel.getCirle().radius * 3)) {
                x = random.nextInt((int)BARRELS_AREA) - BARRELS_AREA / 2;
                y = random.nextInt((int)BARRELS_AREA) - BARRELS_AREA / 2;
            }
            barrel.setPosition(x, y);
            // check overlaping barrels

            piwarsGame.addNewGameObjectImmediately(barrel);
            barrels.add(barrel.getId());
            odd = !odd;
        }
        physicsWorld.updateObjectPositions();
    }

    private boolean overlapsOtherBarrles(float x, float y, float distance) {
        if (barrels.size > 0) {
            for (int i = 0; i < barrels.size; i++) {
                BarrelObject barrel = piwarsGame.getCurrentGameState().get(barrels.get(i));
                Circle barrelCircle = barrel.getCirle();
                if (distance2(x, y, barrelCircle.x, barrelCircle.y) < distance * distance) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean processPlayerInputs(int playerId, PlayerInputs playerInputs) {
        boolean doMove = stateMachine.getCurrentState().shouldMoveRovers();

        return doMove;
    }

    private boolean isComplete() {
        for (int i = 0; i < barrels.size; i++) {
            int barrelId = barrels.get(i);

            BarrelObject barrelObject = piwarsGame.getCurrentGameState().get(barrelId);

            if (barrelObject.getColour() == BarrelColour.GREEN
                    && !greenArea.contains(barrelObject.getPosition().x, barrelObject.getPosition().y)) {
                return false;
            } else if (barrelObject.getColour() == BarrelColour.RED
                    && !redArea.contains(barrelObject.getPosition().x, barrelObject.getPosition().y)) {
                return false;
            }
        }
        return true;
    }

    private enum ChallengeState implements State<EcoDisasterChallenge> {

        WAITING_START() {
            @Override public void enter(EcoDisasterChallenge challenge) {
                Game game = challenge.getGame();

                if (game.containsObject(challenge.playerId)) {
                    game.removeGameObject(challenge.playerId);
                    challenge.playerId = 0;
                }
                for (int barrelId : challenge.barrels.items) {
                    GameObject barrel = game.getCurrentGameState().get(barrelId);
                    if (barrel != null) {
                        game.removeGameObject(barrelId);
                    }
                }
                challenge.barrels.clear();

                GameMessageObject gameMessageObject = challenge.getGameMessage();
                gameMessageObject.setInGame(false);
                gameMessageObject.setWaiting(true);

                challenge.removeTimer();
            }

            @Override public void update(EcoDisasterChallenge challenge) {
                if (challenge.playerId != 0) {
                    challenge.stateMachine.toState(ChallengeState.GAME, challenge);
                }
            }

            @Override public void exit(EcoDisasterChallenge challenge) {
                challenge.stopRovers();
            }
        },

        GAME() {
            @Override public boolean shouldMoveRovers() { return true; }

            @Override public void enter(EcoDisasterChallenge challenge) {
                challenge.startTimer(3000);
                challenge.resetRover();
                challenge.resetBarrels();
                setTimer(1000);

                GameMessageObject gameMessageObject = challenge.getGameMessage();
                gameMessageObject.setMessage("GO!", false);
                gameMessageObject.setInGame(true);
                gameMessageObject.setWaiting(false);

                // TODO
                // challenge.soundManager.playFight();
            }

            @Override public void update(EcoDisasterChallenge challenge) {
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
                    challenge.getGameMessage().setMessage("You have sorted waste! Well done!", false);
                    challenge.stateMachine.toState(ChallengeState.END, challenge);
                }
            }
        },

        END() {
            @Override public boolean shouldMoveRovers() { return true; }

            @Override public void enter(EcoDisasterChallenge challenge) {
                GameMessageObject gameMessageObject = challenge.getGameMessage();
                gameMessageObject.setInGame(false);
                gameMessageObject.setWaiting(false);
                challenge.stopTimer();

                setTimer(3000);
            }

            @Override public void update(EcoDisasterChallenge challenge) {
                if (isTimerDone()) {
                    challenge.stateMachine.toState(WAITING_START, challenge);
                }
            }

            @Override public void exit(EcoDisasterChallenge challenge) {
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

        @Override public void enter(EcoDisasterChallenge challenge) {}
        @Override public void update(EcoDisasterChallenge challenge) {}
        @Override public void exit(EcoDisasterChallenge s) {}

        public boolean shouldMoveRovers() { return false; }
    }

    private static float distance2(float x1, float y1, float x2, float y2) {
        return (x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2);
    }
}
