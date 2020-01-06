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

import java.util.ArrayList;
import java.util.List;

import static org.ah.piwars.virtualrover.engine.utils.CollisionUtils.polygonsOverlap;

import static java.util.Arrays.asList;

public class BlastOffChallenge extends AbstractChallenge {

    public static float SQRT2 = (float) Math.sqrt(2);

    public static int COURSE_LENGTH = 7200; // 7200
    public static float COURSE_WIDTH = 550;
    public static float WALL_HEIGHT = 70;

    public static final List<Polygon> FLOOR_POLYGONS = asList(
            polygonForWall1(0, COURSE_WIDTH),
            polygonForWall2(0, COURSE_WIDTH),
            polygonForWall3(0, COURSE_WIDTH),
            polygonForWall4(0, COURSE_WIDTH),
            polygonForWall5(0, COURSE_WIDTH)
    );

    public static final List<Polygon> LINE_POLYGONS = asList(
            polygonForWall1(0, 10),
            polygonForWall2(0, 10),
            polygonForWall3(0, 10),
            polygonForWall4(0, 10),
            polygonForWall5(0, 10)
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

    private List<Polygon> piNoonPolygons;

    private int gameMessageId;
    private GameState gameStateGameMessageIsDefinedOn;
    private GameMessageObject cachedGameMessageObject;

    private Quaternion orientation = new Quaternion();

    private int playerId;
    private int cameraId;

    private StateMachine<BlastOffChallenge, ChallengeState> stateMachine = new StateMachine<BlastOffChallenge, ChallengeState>();

    private PiWarsGame piwarsGame;

    public BlastOffChallenge(Game game, String name) {
        super(game, name);
        piwarsGame = (PiWarsGame)game;
        stateMachine.setCurrentState(ChallengeState.WAITING_START);

        piNoonPolygons = new ArrayList<Polygon>();
        piNoonPolygons.addAll(WALLS_POLYGONS);
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

    private enum ChallengeState implements State<BlastOffChallenge> {

        WAITING_START() {
            @Override public void enter(BlastOffChallenge challenge) {
                Game game = challenge.getGame();

                if (game.containsObject(challenge.playerId)) {
                    game.removeGameObject(challenge.playerId);
                    challenge.playerId = 0;
                }

                challenge.getGameMessage().setInGame(false);
                challenge.getGameMessage().setWaiting(true);
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
                challenge.resetBarrels();
                setTimer(1000);
                challenge.setMessage("GO!", false);
                challenge.getGameMessage().setInGame(true);
                challenge.getGameMessage().setWaiting(false);

                // TODO
                // challenge.soundManager.playFight();
            }

            @Override public void update(BlastOffChallenge challenge) {
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

            @Override public void enter(BlastOffChallenge challenge) {
                // PiNoonAttachment player1Attachment = challenge.getPlayerOneAttachment();

                challenge.getGameMessage().setInGame(false);
                challenge.getGameMessage().setWaiting(false);
                setTimer(3000);
            }

            @Override public void update(BlastOffChallenge challenge) {
                if (isTimerDone()) {
                    challenge.stateMachine.toState(WAITING_START, challenge);
                }
            }

            @Override public void exit(BlastOffChallenge challenge) {
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
