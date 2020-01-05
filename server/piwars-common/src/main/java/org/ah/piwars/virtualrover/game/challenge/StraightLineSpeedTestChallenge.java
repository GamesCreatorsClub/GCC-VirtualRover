package org.ah.piwars.virtualrover.game.challenge;

import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Quaternion;

import org.ah.piwars.virtualrover.game.GameMessageObject;
import org.ah.piwars.virtualrover.game.PiWarsCollidableObject;
import org.ah.piwars.virtualrover.game.PiWarsGame;
import org.ah.piwars.virtualrover.game.PiWarsGameTypeObject;
import org.ah.piwars.virtualrover.game.attachments.CameraAttachment;
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
import static org.ah.piwars.virtualrover.engine.utils.CollisionUtils.polygonsOverlap;

import static java.util.Arrays.asList;

public class StraightLineSpeedTestChallenge extends AbstractChallenge {

    public static int COURSE_LENGTH = 4800; // 7200
    public static int CHICANE_LENGTH = 800;
    public static float CHICANE_WIDTH = 38;
    public static float CUT_MODIFIER = 1.5f;

    private List<Polygon> piNoonPolygons = asList(
                    polygonFromBox(-COURSE_LENGTH / 2, -315, COURSE_LENGTH / 2, -305),
                    polygonFromBox(-COURSE_LENGTH / 2, 305, COURSE_LENGTH / 2, 315),
                    polygonForChicane(-COURSE_LENGTH / 4, -305),
                    polygonForChicane(-COURSE_LENGTH / 4, 305),
                    polygonForChicane(COURSE_LENGTH / 4, -305),
                    polygonForChicane(COURSE_LENGTH / 4, 305)
            );

    private int gameMessageId;
    private GameState gameStateGameMessageIsDefinedOn;
    private GameMessageObject cachedGameMessageObject;

    private Quaternion orientation = new Quaternion();

    private int playerId;
    private int cameraId;

    private StateMachine<StraightLineSpeedTestChallenge, ChallengeState> stateMachine = new StateMachine<StraightLineSpeedTestChallenge, ChallengeState>();

    private PiWarsGame piwarsGame;

    public StraightLineSpeedTestChallenge(Game game, String name) {
        super(game, name);
        piwarsGame = (PiWarsGame)game;
        stateMachine.setCurrentState(ChallengeState.WAITING_START);
    }

    @Override
    public List<Polygon> getCollisionPolygons() {
        return piNoonPolygons;
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
    }

    @Override
    public void afterGameObjectAdded(GameObject gameObject) {
        if (gameObject instanceof Rover) {
            if (game.isServer()) {
                CameraAttachment cameraAttachment = game.getGameObjectFactory().newGameObjectWithId(PiWarsGameTypeObject.CameraAttachment, game.newId());
                cameraAttachment.attachToRover((Rover)gameObject);
                game.addNewGameObjectImmediately(cameraAttachment);
                cameraId = cameraAttachment.getId();
            }
            playerId = gameObject.getId();
            resetRover();
        } else if (gameObject instanceof CameraAttachment) {
            CameraAttachment cameraAttachment = (CameraAttachment)gameObject;
            cameraId = cameraAttachment.getId();

            Rover rover = game.getCurrentGameState().get(cameraAttachment.getParentId());
            if (rover != null) {
                cameraAttachment.attachToRover(rover);
            } else {
                // TODO add error here!!!
            }
        }
    }

    @Override
    public void gameObjectRemoved(GameObject gameObject) {
        if (gameObject instanceof Rover) {
            Rover rover = (Rover)gameObject;
            playerId = 0;
            cameraId = 0;
            if (rover.getCameraId() != 0) {
                game.removeGameObject(rover.getAttachemntId());
            }
        }
    }

    protected void setMessage(String message, boolean flashing) {
        getGameMessage().setMessage(message, flashing);
    }

    private GameMessageObject getGameMessage() {
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

    private Rover getPlayer() {
        if (playerId != 0) {
            return game.getCurrentGameState().get(playerId);
        }
        return null;
    }

    private CameraAttachment getCameraAttachment() {
        Rover rover = getPlayer();
        if (rover != null) {
            CameraAttachment cameraAttachment = piwarsGame.getCurrentGameState().get(rover.getCameraId());
            return cameraAttachment;
        }
        return null;
    }

    private void resetRover() {
        Rover player1 = getPlayer();
        if (player1 != null) {
            orientation.setEulerAnglesRad(0f, 0f, 0f); // (float)(Math.PI / 2f));
            player1.setPosition(-COURSE_LENGTH / 2 + 150, 0);
            player1.setOrientation(orientation);
        }
    }

    private void resetBarrels() {

    }

    private void stopRovers() {
        Rover player1 = getPlayer();
        if (player1 != null) {
            player1.setVelocity(0, 0);
            player1.setTurnSpeed(0);
            player1.setSpeed(0);
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
                challenge.resetBarrels();
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
                Rover player1 = challenge.getPlayer();
                challenge.piwarsGame.removeGameObject(player1.getId());
                challenge.playerId = 0;
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
