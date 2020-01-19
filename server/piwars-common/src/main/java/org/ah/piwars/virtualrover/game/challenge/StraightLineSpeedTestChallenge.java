package org.ah.piwars.virtualrover.game.challenge;

import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Quaternion;

import org.ah.piwars.virtualrover.game.PiWarsCollidableObject;
import org.ah.piwars.virtualrover.game.PiWarsGame;
import org.ah.piwars.virtualrover.game.attachments.CameraAttachment;
import org.ah.piwars.virtualrover.game.rovers.Rover;
import org.ah.themvsus.engine.common.game.Game;
import org.ah.themvsus.engine.common.game.GameObject;
import org.ah.themvsus.engine.common.game.GameObjectWithPosition;
import org.ah.themvsus.engine.common.game.GameState;
import org.ah.themvsus.engine.common.input.PlayerInputs;
import org.ah.themvsus.engine.common.statemachine.State;
import org.ah.themvsus.engine.common.statemachine.StateMachine;

import java.util.ArrayList;
import java.util.List;

import static org.ah.piwars.virtualrover.engine.utils.CollisionUtils.polygonFromBox;
import static org.ah.piwars.virtualrover.engine.utils.CollisionUtils.polygonsOverlap;

import static java.util.Arrays.asList;

public class StraightLineSpeedTestChallenge extends CameraAbstractChallenge {

    public static int COURSE_WIDTH = 630 * 2; // 7200
    public static int COURSE_LENGTH = 4800; // 7200
    public static int CHICANE_LENGTH = 800;
    public static float CHICANE_WIDTH = 38;
    public static float CUT_MODIFIER = 1.5f;
    public static float WALL_HEIGHT = 64;

    public static final Polygon FLOOR_POLYGON = polygonFromBox(-COURSE_LENGTH / 2, -315, COURSE_LENGTH / 2, 315);

    public static final List<Polygon> CHICANES_POLYGONS = asList(
            polygonForChicane(-COURSE_LENGTH / 4, -305),
            polygonForChicane(-COURSE_LENGTH / 4, 305),
            polygonForChicane(COURSE_LENGTH / 4, -305),
            polygonForChicane(COURSE_LENGTH / 4, 305)
    );

    public static final List<Polygon> WALL_POLYGONS = asList(
            polygonFromBox(-COURSE_LENGTH / 2, -315, COURSE_LENGTH / 2, -305),
            polygonFromBox(-COURSE_LENGTH / 2, 305, COURSE_LENGTH / 2, 315)
    );

    public static final Polygon START_POLIGON = polygonFromBox(-COURSE_LENGTH / 2 - 10, -315, -COURSE_LENGTH / 2, 315);
    public static final Polygon END_POLIGON = polygonFromBox(COURSE_LENGTH / 2, -315, COURSE_LENGTH / 2 + 10, 315);

    private Quaternion orientation = new Quaternion();

    private StateMachine<StraightLineSpeedTestChallenge, ChallengeState> stateMachine = new StateMachine<StraightLineSpeedTestChallenge, ChallengeState>();

    public StraightLineSpeedTestChallenge(PiWarsGame game, String name) {
        super(game, name);
        stateMachine.setCurrentState(ChallengeState.WAITING_START);

        wallPolygons = new ArrayList<Polygon>();
        wallPolygons.addAll(WALL_POLYGONS);
        wallPolygons.addAll(CHICANES_POLYGONS);
    }

    @Override
    public boolean checkForCollision(GameObjectWithPosition object, Iterable<GameObjectWithPosition> objects) {
        if (object instanceof PiWarsCollidableObject) {
            if (polygonsOverlap(getCollisionPolygons(), ((PiWarsCollidableObject)object).getCollisionPolygons())) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void process(GameState currentGameState) {
        stateMachine.update(this);
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
            orientation.setEulerAnglesRad(0f, 0f, 0f); // (float)(Math.PI / 2f));
            player1.setPosition(-COURSE_LENGTH / 2 + 150, 0);
            player1.setOrientation(orientation);
        }
    }

    @Override
    public boolean processPlayerInputs(int playerId, PlayerInputs playerInputs) {
        boolean doMove = stateMachine.getCurrentState().shouldMoveRovers();

        return doMove;
    }

    private enum ChallengeState implements State<StraightLineSpeedTestChallenge> {

        WAITING_START() {
            @Override public void enter(StraightLineSpeedTestChallenge challenge) {
                Game game = challenge.getGame();

                if (game.containsObject(challenge.playerId)) {
                    game.removeGameObject(challenge.playerId);
                    challenge.playerId = 0;
                }

                challenge.getGameMessage().setInGame(false);
                challenge.getGameMessage().setWaiting(true);
            }

            @Override public void update(StraightLineSpeedTestChallenge challenge) {
                if (challenge.playerId != 0) {
                    challenge.stateMachine.toState(ChallengeState.GAME, challenge);
                }
            }

            @Override public void exit(StraightLineSpeedTestChallenge challenge) {
                challenge.resetRover();
                challenge.stopRovers();
            }
        },

        GAME() {
            @Override public boolean shouldMoveRovers() { return true; }

            @Override public void enter(StraightLineSpeedTestChallenge challenge) {
                challenge.resetRover();
                setTimer(1000);
                challenge.setMessage("GO!", false);
                challenge.getGameMessage().setInGame(true);
                challenge.getGameMessage().setWaiting(false);

                // TODO
                // challenge.soundManager.playFight();
            }

            @Override public void update(StraightLineSpeedTestChallenge challenge) {
                super.update(challenge);

                if (isTimerDone()) {
                    challenge.setMessage(null, false);
                }

                Rover rover = challenge.getRover();
                if (polygonsOverlap(START_POLIGON, rover.getCollisionPolygons())) {
                    challenge.getGameMessage().setMessage("Wrong way!", false);
                    challenge.stateMachine.toState(ChallengeState.END, challenge);
                } else if (polygonsOverlap(END_POLIGON, rover.getCollisionPolygons())) {
                    challenge.getGameMessage().setMessage("You have finished course! Well done!", false);
                    challenge.stateMachine.toState(ChallengeState.END, challenge);
                }

                CameraAttachment player1Attachment = challenge.getCameraAttachment();

                if (player1Attachment != null) {
                }
            }
        },

        END() {
            @Override public boolean shouldMoveRovers() { return true; }

            @Override public void enter(StraightLineSpeedTestChallenge challenge) {
                // PiNoonAttachment player1Attachment = challenge.getPlayerOneAttachment();

                challenge.getGameMessage().setInGame(false);
                challenge.getGameMessage().setWaiting(false);
                setTimer(3000);
            }

            @Override public void update(StraightLineSpeedTestChallenge challenge) {
                if (isTimerDone()) {
                    challenge.stateMachine.toState(WAITING_START, challenge);
                }
            }

            @Override public void exit(StraightLineSpeedTestChallenge challenge) {
                Rover rover = challenge.getRover();
                if (rover != null) {
                    challenge.piwarsGame.removeGameObject(rover.getId());
                    challenge.playerId = 0;
                }
                challenge.setMessage(null, false);
            }
        };

        long timer;

        protected void setTimer(int millis) {
            timer = System.currentTimeMillis() + millis;
        }

        protected boolean isTimerDone() {
            return (timer <= System.currentTimeMillis());
        }

        @Override public void enter(StraightLineSpeedTestChallenge challenge) {}
        @Override public void update(StraightLineSpeedTestChallenge challenge) {}
        @Override public void exit(StraightLineSpeedTestChallenge s) {}

        public boolean shouldMoveRovers() { return false; }
    }

    public static Polygon polygonForChicane(float x, float y) {
        if (y > 0) {
            Polygon polygon = new Polygon(new float[] {
                    x - CHICANE_LENGTH / 2, y - CHICANE_WIDTH,
                    x - CHICANE_LENGTH / 2 - CHICANE_WIDTH * CUT_MODIFIER, y,
                    x + CHICANE_LENGTH / 2 + CHICANE_WIDTH * CUT_MODIFIER, y,
                    x + CHICANE_LENGTH / 2, y - CHICANE_WIDTH });
            return polygon;
        } else {
            Polygon polygon = new Polygon(new float[] {
                    x - CHICANE_LENGTH / 2 - CHICANE_WIDTH * CUT_MODIFIER, y,
                    x - CHICANE_LENGTH / 2, y + CHICANE_WIDTH,
                    x + CHICANE_LENGTH / 2, y + CHICANE_WIDTH,
                    x + CHICANE_LENGTH / 2 + CHICANE_WIDTH * CUT_MODIFIER, y });
            return polygon;
        }
    }

}
