package org.ah.piwars.virtualrover.game.challenge;

import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.utils.IntArray;

import org.ah.piwars.virtualrover.game.GameMessageObject;
import org.ah.piwars.virtualrover.game.PiWarsGame;
import org.ah.piwars.virtualrover.game.PiWarsGameTypeObject;
import org.ah.piwars.virtualrover.game.attachments.CameraAttachment;
import org.ah.piwars.virtualrover.game.objects.FishTowerObject;
import org.ah.piwars.virtualrover.game.objects.GolfBallObject;
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

public class FeedTheFishChallenge extends CameraAbstractChallenge implements Box2DPhysicalWorldSimulationChallenge {

    public static final float CHALLENGE_WIDTH = 1500;
    public static float WALL_HEIGHT = 200;

    public static final Polygon FLOOR_POLYGON = polygonFromBox(-CHALLENGE_WIDTH / 2, -CHALLENGE_WIDTH / 2, CHALLENGE_WIDTH / 2, CHALLENGE_WIDTH / 2);

    public static final List<Polygon> WALL_POLYGONS = asList(
            polygonFromBox(-CHALLENGE_WIDTH / 2, -CHALLENGE_WIDTH / 2 - 1,  CHALLENGE_WIDTH / 2, -CHALLENGE_WIDTH / 2),
            polygonFromBox(-CHALLENGE_WIDTH / 2 - 1, -CHALLENGE_WIDTH / 2, -CHALLENGE_WIDTH / 2,  CHALLENGE_WIDTH / 2),
            polygonFromBox(-CHALLENGE_WIDTH / 2,  CHALLENGE_WIDTH / 2,  CHALLENGE_WIDTH / 2,  CHALLENGE_WIDTH / 2 + 1),
            polygonFromBox( CHALLENGE_WIDTH / 2, -CHALLENGE_WIDTH / 2,  CHALLENGE_WIDTH / 2 + 1,  CHALLENGE_WIDTH / 2));

    private IntArray golfBalls = new IntArray();
    private int fishTowerId = 0;

    private StateMachine<FeedTheFishChallenge, ChallengeState> stateMachine = new StateMachine<FeedTheFishChallenge, ChallengeState>();

    public Box2DPhysicsWorld physicsWorld;

    public FeedTheFishChallenge(PiWarsGame game, String name) {
        super(game, name);
        setWallPolygons(WALL_POLYGONS);
        stateMachine.setCurrentState(ChallengeState.WAITING_START);

        physicsWorld = new Box2DPhysicsWorld(game, getCollisionPolygons());
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
            // orientation.setEulerAnglesRad(0f, 0f, (float)(Math.PI));
            orientation.setEulerAnglesRad(0f, 0f, 0f);
            player1.setPosition(-600, -500);
            player1.setOrientation(orientation);
        }
    }

    private void resetGolfBalls() {
        while (golfBalls.size < 5) {
            GolfBallObject golfBall = piwarsGame.getGameObjectFactory().newGameObjectWithId(PiWarsGameTypeObject.GolfBallObject, piwarsGame.newId());
            piwarsGame.addNewGameObjectImmediately(golfBall);
            golfBalls.add(golfBall.getId());
        }

        for (int i = 0; i < golfBalls.size; i++) {
            int id = golfBalls.get(i);
            GolfBallObject golfBall = piwarsGame.getCurrentGameState().get(id);
            golfBall.setPosition(-500 + i * 250, -200);
        }

        if (fishTowerId <= 0) {
            FishTowerObject fishTower = piwarsGame.getGameObjectFactory().newGameObjectWithId(PiWarsGameTypeObject.FishTowerObject, piwarsGame.newId());
            piwarsGame.addNewGameObjectImmediately(fishTower);
            fishTowerId = fishTower.getId();
            fishTower.setPosition(0f, 500f);
        }
        physicsWorld.updateObjectPositions();
    }

    @Override
    public boolean processPlayerInputs(int playerId, PlayerInputs playerInputs) {
        boolean doMove = stateMachine.getCurrentState().shouldMoveRovers();

        return doMove;
    }

    private enum ChallengeState implements State<FeedTheFishChallenge> {

        WAITING_START() {
            @Override public void enter(FeedTheFishChallenge challenge) {
                Game game = challenge.getGame();

                if (game.containsObject(challenge.playerId)) {
                    game.removeGameObject(challenge.playerId);
                    challenge.playerId = 0;
                }
                for (int barrelId : challenge.golfBalls.items) {
                    game.removeGameObject(barrelId);
                }
                if (challenge.fishTowerId > 0) {
                    game.removeGameObject(challenge.fishTowerId);
                }
                challenge.golfBalls.clear();

                GameMessageObject gameMessageObject = challenge.getGameMessage();
                gameMessageObject.setInGame(false);
                gameMessageObject.setWaiting(true);

                challenge.removeTimer();
            }

            @Override public void update(FeedTheFishChallenge challenge) {
                if (challenge.playerId != 0) {
                    challenge.stateMachine.toState(ChallengeState.GAME, challenge);
                }
            }

            @Override public void exit(FeedTheFishChallenge challenge) {
                challenge.stopRovers();
            }
        },

        GAME() {
            @Override public boolean shouldMoveRovers() { return true; }

            @Override public void enter(FeedTheFishChallenge challenge) {
                challenge.startTimer(3000);
                challenge.resetRover();
                challenge.resetGolfBalls();
                setTimer(1000);

                GameMessageObject gameMessageObject = challenge.getGameMessage();
                gameMessageObject.setMessage("GO!", false);
                gameMessageObject.setInGame(true);
                gameMessageObject.setWaiting(false);

                // TODO
                // challenge.soundManager.playFight();
            }

            @Override public void update(FeedTheFishChallenge challenge) {
                super.update(challenge);

                if (isTimerDone()) {
                    challenge.setMessage(null, false);
                }

                CameraAttachment player1Attachment = challenge.getCameraAttachment();

                GameMessageObject gameMessageObject = challenge.getGameMessage();
                if (gameMessageObject.getTimer() <= 0) {
                    challenge.stopTimer();
                    challenge.getGameMessage().setMessage("Time is up!", false);
                    challenge.stateMachine.toState(ChallengeState.END, challenge);
                }

                if (player1Attachment != null) {
                }
            }
        },

        END() {
            @Override public boolean shouldMoveRovers() { return true; }

            @Override public void enter(FeedTheFishChallenge challenge) {
                GameMessageObject gameMessageObject = challenge.getGameMessage();
                gameMessageObject.setInGame(false);
                gameMessageObject.setWaiting(false);
                challenge.stopTimer();

                setTimer(3000);
            }

            @Override public void update(FeedTheFishChallenge challenge) {
                if (isTimerDone()) {
                    challenge.stateMachine.toState(WAITING_START, challenge);
                }
            }

            @Override public void exit(FeedTheFishChallenge challenge) {
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

        @Override public void enter(FeedTheFishChallenge challenge) {}
        @Override public void update(FeedTheFishChallenge challenge) {}
        @Override public void exit(FeedTheFishChallenge s) {}

        public boolean shouldMoveRovers() { return false; }
    }
}
