package org.ah.piwars.virtualrover.game.challenge;

import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Shape2D;

import org.ah.piwars.virtualrover.game.GameMessageObject;
import org.ah.piwars.virtualrover.game.PiWarsGame;
import org.ah.piwars.virtualrover.game.attachments.CameraAttachment;
import org.ah.piwars.virtualrover.game.rovers.Rover;
import org.ah.themvsus.engine.common.game.Game;
import org.ah.themvsus.engine.common.game.GameObject;
import org.ah.themvsus.engine.common.game.GameState;
import org.ah.themvsus.engine.common.input.PlayerInputs;
import org.ah.themvsus.engine.common.statemachine.State;
import org.ah.themvsus.engine.common.statemachine.StateMachine;

import java.util.List;

import static org.ah.piwars.virtualrover.engine.utils.CollisionUtils.polygonFromBox;
import static org.ah.piwars.virtualrover.engine.utils.CollisionUtils.polygonsOverlap;

import static java.util.Arrays.asList;

public class BlastOffChallenge extends CameraAbstractChallenge {

    public static float SQRT2 = (float) Math.sqrt(2);

    public static int COURSE_LENGTH = 7200; // 7200
    public static float COURSE_WIDTH = 550;
    public static float WALL_HEIGHT = 70;

    public static float BOTTOM_WALL_ADJUST = (SQRT2 - 1f) * COURSE_WIDTH;

    public static final List<Polygon> FLOOR_POLYGONS = asList(
            polygonForWall1(0, COURSE_WIDTH),
            polygonForWall2(0, COURSE_WIDTH),
            polygonForWall3(0, COURSE_WIDTH),
            polygonForWall4(0, COURSE_WIDTH),
            polygonForWall5(0, COURSE_WIDTH)
    );

    public static final List<Polygon> LINE_POLYGONS = asList(
            polygonForWall1(0, 19),
            polygonForWall2(0, 19),
            polygonForWall3(0, 19),
            polygonForWall4(0, 19),
            polygonForWall5(0, 19)
    );

    public static final List<Polygon> WALLS_POLYGONS = asList(
            polygonForWall1(COURSE_WIDTH / 2, 10),
            polygonForWall2(COURSE_WIDTH / 2, 10),
            polygonForWall3(COURSE_WIDTH / 2, 10),
            polygonForWall4(COURSE_WIDTH / 2, 10),
            polygonForWall5(COURSE_WIDTH / 2, 10),
            polygonForWall1(-COURSE_WIDTH / 2, 10),
            polygonForWall2(-COURSE_WIDTH / 2, 10),
            polygonForWall3(-COURSE_WIDTH / 2, 10),
            polygonForWall4(-COURSE_WIDTH / 2, 10),
            polygonForWall5(-COURSE_WIDTH / 2, 10)
    );

    public static final Shape2D START_POLYGON = polygonFromBox(-COURSE_LENGTH / 2 - 10, -COURSE_WIDTH, -COURSE_LENGTH / 2, COURSE_WIDTH);
    public static final Shape2D END_POLYGON = polygonFromBox(COURSE_LENGTH / 2, -COURSE_WIDTH, COURSE_LENGTH / 2 + 10, COURSE_WIDTH);

    private StateMachine<BlastOffChallenge, ChallengeState> stateMachine = new StateMachine<BlastOffChallenge, ChallengeState>();

    public BlastOffChallenge(PiWarsGame game, String name) {
        super(game, name);
        setWallPolygons(WALLS_POLYGONS);
        stateMachine.setCurrentState(ChallengeState.WAITING_START);
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

    private enum ChallengeState implements State<BlastOffChallenge> {

        WAITING_START() {
            @Override public void enter(BlastOffChallenge challenge) {
                Game game = challenge.getGame();

                if (game.containsObject(challenge.playerId)) {
                    game.removeGameObject(challenge.playerId);
                    challenge.playerId = 0;
                }

                GameMessageObject gameMessageObject = challenge.getGameMessage();
                gameMessageObject.setInGame(false);
                gameMessageObject.setWaiting(true);
            }

            @Override public void update(BlastOffChallenge challenge) {
                if (challenge.playerId != 0) {
                    challenge.stateMachine.toState(ChallengeState.GAME, challenge);
                }
            }

            @Override public void exit(BlastOffChallenge challenge) {
                challenge.resetRover();
                challenge.stopRovers();
            }
        },

        GAME() {
            @Override public boolean shouldMoveRovers() { return true; }

            @Override public void enter(BlastOffChallenge challenge) {
                challenge.resetRover();
                challenge.startTimer(3000);
                setTimer(1000);
                GameMessageObject gameMessageObject = challenge.getGameMessage();
                gameMessageObject.setMessage("GO!", false);
                gameMessageObject.setInGame(true);
                gameMessageObject.setWaiting(false);

                // TODO
                // challenge.soundManager.playFight();
            }

            @Override public void update(BlastOffChallenge challenge) {
                super.update(challenge);

                GameMessageObject gameMessageObject = challenge.getGameMessage();
                if (isTimerDone()) {
                    gameMessageObject.setMessage(null, false);
                }

                Rover rover = challenge.getRover();
                if (polygonsOverlap(START_POLYGON, rover.getCollisionPolygons())) {
                    challenge.getGameMessage().setMessage("Wrong way!", false);
                    challenge.stateMachine.toState(ChallengeState.END, challenge);
                } else if (polygonsOverlap(END_POLYGON, rover.getCollisionPolygons())) {
                    challenge.getGameMessage().setMessage("You have finished course! Well done!", false);
                    challenge.stateMachine.toState(ChallengeState.END, challenge);
                } else if (gameMessageObject.getTimer() <= 0) {
                    challenge.stopTimer();
                    challenge.getGameMessage().setMessage("Time is up!", false);
                    challenge.stateMachine.toState(ChallengeState.END, challenge);
                }

                CameraAttachment player1Attachment = challenge.getCameraAttachment();

                if (player1Attachment != null) {
                }
            }
        },

        END() {
            @Override public boolean shouldMoveRovers() { return true; }

            @Override public void enter(BlastOffChallenge challenge) {
                // PiNoonAttachment player1Attachment = challenge.getPlayerOneAttachment();

                GameMessageObject gameMessageObject = challenge.getGameMessage();
                gameMessageObject.setInGame(false);
                gameMessageObject.setWaiting(false);
                setTimer(3000);
                challenge.stopTimer();
            }

            @Override public void update(BlastOffChallenge challenge) {
                if (isTimerDone()) {
                    challenge.stateMachine.toState(WAITING_START, challenge);
                }
            }

            @Override public void exit(BlastOffChallenge challenge) {
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

        @Override public void enter(BlastOffChallenge challenge) {}
        @Override public void update(BlastOffChallenge challenge) {}
        @Override public void exit(BlastOffChallenge s) {}

        public boolean shouldMoveRovers() { return false; }
    }

    public static Polygon polygonForWall1(float y, float width) {
        Polygon polygon = new Polygon(new float[] {
                - COURSE_LENGTH / 2, y + width / 2,
                - COURSE_LENGTH / 6, y + width / 2,
                - COURSE_LENGTH / 6, y - width / 2,
                - COURSE_LENGTH / 2, y - width / 2,
        });
        return polygon;
    }

    public static Polygon polygonForWall2(float y, float width) {
        Polygon polygon = new Polygon(new float[] {
                - COURSE_LENGTH / 6, y + width / 2,
                - COURSE_LENGTH / 6 + COURSE_WIDTH, y + width / 2 + COURSE_WIDTH,
                - COURSE_LENGTH / 6 + COURSE_WIDTH, y - width / 2 + COURSE_WIDTH,
                - COURSE_LENGTH / 6, y - width / 2,
        });
        return polygon;
    }

    public static Polygon polygonForWall3(float y, float width) {
        Polygon polygon = new Polygon(new float[] {
                - COURSE_LENGTH / 6 + COURSE_WIDTH, y + width / 2 + COURSE_WIDTH,
                COURSE_LENGTH / 6 - COURSE_WIDTH, y + width / 2 + COURSE_WIDTH,
                COURSE_LENGTH / 6 - COURSE_WIDTH, y - width / 2 + COURSE_WIDTH,
                - COURSE_LENGTH / 6 + COURSE_WIDTH, y - width / 2 + COURSE_WIDTH,
        });
        return polygon;
    }

    public static Polygon polygonForWall4(float y, float width) {
        Polygon polygon = new Polygon(new float[] {
                COURSE_LENGTH / 6 - COURSE_WIDTH, y + width / 2 + COURSE_WIDTH,
                COURSE_LENGTH / 6, y + width / 2,
                COURSE_LENGTH / 6, y - width / 2,
                COURSE_LENGTH / 6 - COURSE_WIDTH, y - width / 2 + COURSE_WIDTH,
        });
        return polygon;
    }

    public static Polygon polygonForWall5(float y, float width) {
        Polygon polygon = new Polygon(new float[] {
                COURSE_LENGTH / 6, y + width / 2,
                COURSE_LENGTH / 2, y + width / 2,
                COURSE_LENGTH / 2, y - width / 2,
                COURSE_LENGTH / 6, y - width / 2,
        });
        return polygon;
    }

}
