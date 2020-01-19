package org.ah.piwars.virtualrover.game.challenge;

import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Quaternion;

import org.ah.piwars.virtualrover.game.MineSweeperStateObject;
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
import java.util.Random;

import static org.ah.piwars.virtualrover.engine.utils.CollisionUtils.polygonFromBox;
import static org.ah.piwars.virtualrover.engine.utils.CollisionUtils.polygonsOverlap;

import static java.util.Arrays.asList;

public class MineSweeperChallenge extends CameraAbstractChallenge {

    public static float COURSE_WIDTH = 2200;

    public static float WALL_HEIGHT = 200;

    public static List<Polygon> MINE_POLYGONS = asList(
            polygonFromBox(-1100, -1100,  -550, -550),
            polygonFromBox(-550, -1100,  0, -550),
            polygonFromBox(0, -1100,  550, -550),
            polygonFromBox(550, -1100,  1100, -550),

            polygonFromBox(-1100, -550,  -550, 0),
            polygonFromBox(-550, -550,  0, 0),
            polygonFromBox(0, -550,  550, 0),
            polygonFromBox(550, -550,  1100, 0),

            polygonFromBox(-1100, 0,  -550, 550),
            polygonFromBox(-550, 0,  0, 550),
            polygonFromBox(0, 0,  550, 550),
            polygonFromBox(550, 0,  1100, 550),

            polygonFromBox(-1100, 550,  -550, 1100),
            polygonFromBox(-550, 550,  0, 1100),
            polygonFromBox(0, 550,  550, 1100),
            polygonFromBox(550, 550,  1100, 1100));

    public static List<Polygon> WALL_POLYGONS = asList(
            polygonFromBox(-1100, -1101,  1100, -1100),
            polygonFromBox(-1101, -1100, -1100,  1100),
            polygonFromBox(-1100,  1100,  1100,  1101),
            polygonFromBox( 1100, -1100,  1101,  1100));

    private List<Polygon> piNoonPolygons = WALL_POLYGONS;

    private Random random = new Random();

    private Quaternion orientation = new Quaternion();

    private int stateObjectId;

    private StateMachine<MineSweeperChallenge, ChallengeState> stateMachine = new StateMachine<MineSweeperChallenge, ChallengeState>();

    public MineSweeperChallenge(PiWarsGame game, String name) {
        super(game, name);
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
//        if (cameraId > 0) {
//            CameraAttachment cameraAttachment = game.getCurrentGameState().get(cameraId);
//
//            GameObject parent = game.getCurrentGameState().get(cameraAttachment.getParentId());
//            if (parent instanceof GameObjectWithPositionAndOrientation) {
//                GameObjectWithPositionAndOrientation gameObject = (GameObjectWithPositionAndOrientation)parent;
//
//                Vector3 position = gameObject.getPosition();
//                cameraAttachment.setPosition(position.x, position.y, position.z);
//                cameraAttachment.setOrientation(gameObject.getOrientation());
//            }
//        }

        stateMachine.update(this);
    }

    @Override
    public void beforeGameObjectAdded(GameObject gameObject) {
        super.beforeGameObjectAdded(gameObject);
    }

    @Override
    public void afterGameObjectAdded(GameObject gameObject) {
        super.afterGameObjectAdded(gameObject);
        if (gameObject instanceof Rover) {
            if (piwarsGame.isServer()) {
//                CameraAttachment cameraAttachment = getCameraAttachment();
//                orientation.setEulerAngles(30f, 0f, 0f);
//                cameraAttachment.setOrientation(orientation);

                MineSweeperStateObject mineSweeperStateObject = piwarsGame.getGameObjectFactory().newGameObjectWithId(PiWarsGameTypeObject.MineSweeperStateObject, piwarsGame.newId());
                piwarsGame.addNewGameObjectImmediately(mineSweeperStateObject);
                mineSweeperStateObject.setStateBits(0x0);
            }
            playerId = gameObject.getId();
            resetRover();
        } else if (gameObject instanceof MineSweeperStateObject) {
            stateObjectId = gameObject.getId();
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
            // player1.setRoverColour(RoverColour.BLUE);
        }
    }

    protected MineSweeperStateObject getMineSweeperStateObject() {
        if (stateObjectId != 0) {
            return piwarsGame.getCurrentGameState().get(stateObjectId);
        }
        return null;
    }

    protected void clear_lights() {
        MineSweeperStateObject mineSweeperStateObject = getMineSweeperStateObject();
        if (mineSweeperStateObject != null) {
            mineSweeperStateObject.setStateBits(0);
        }
    }

    protected void change_lights() {
        MineSweeperStateObject mineSweeperStateObject = getMineSweeperStateObject();
        if (mineSweeperStateObject != null) {
            int next_light_no = random.nextInt(16);
            int light = 1 << next_light_no;
            mineSweeperStateObject.setStateBits(light);
        }
     }

    @Override
    public boolean processPlayerInputs(int playerId, PlayerInputs playerInputs) {
        boolean doMove = stateMachine.getCurrentState().shouldMoveRovers();

        return doMove;
    }

    private enum ChallengeState implements State<MineSweeperChallenge> {

        WAITING_START() {
            @Override public void enter(MineSweeperChallenge challenge) {
                challenge.clear_lights();

                Game game = challenge.getGame();

                if (game.containsObject(challenge.playerId)) {
                    game.removeGameObject(challenge.playerId);
                    challenge.playerId = 0;
                }

                challenge.getGameMessage().setInGame(false);
                challenge.getGameMessage().setWaiting(true);
            }

            @Override public void update(MineSweeperChallenge challenge) {
                if (challenge.playerId != 0) {
                    challenge.stateMachine.toState(ChallengeState.GAME, challenge);
                }
            }

            @Override public void exit(MineSweeperChallenge challenge) {
                challenge.resetRover();
                challenge.stopRovers();
            }
        },

        GAME() {
            boolean messageRemoved = false;

            @Override public boolean shouldMoveRovers() { return true; }

            @Override public void enter(MineSweeperChallenge challenge) {
                challenge.resetRover();
                setTimer(1000);
                challenge.setMessage("GO!", false);
                challenge.getGameMessage().setInGame(true);
                challenge.getGameMessage().setWaiting(false);

                // TODO
                // challenge.soundManager.playFight();
            }

            @Override public void update(MineSweeperChallenge challenge) {
                super.update(challenge);

                if (isTimerDone() && !messageRemoved) {
                    challenge.setMessage(null, false);
                    challenge.change_lights();
                    messageRemoved = true;
                }

                Rover rover = challenge.getRover();
                MineSweeperStateObject mineSweeperStateObject = challenge.getMineSweeperStateObject();
                if (mineSweeperStateObject != null && mineSweeperStateObject.getStateBits() != 0) {
                    int index = 0;
                    int bit = 1;
                    int bits = mineSweeperStateObject.getStateBits();
                    while ((bits & bit) == 0 && index < 16) {
                        bit = bit << 1;
                        index++;
                    }
                    if (index < 16) {
                        Polygon selectedPolygon = MINE_POLYGONS.get(index);
                        if (selectedPolygon.contains(rover.getPosition().x, rover.getPosition().y)) {
                            challenge.change_lights();
                        }
                    }
                }

                CameraAttachment player1Attachment = challenge.getCameraAttachment();

                if (player1Attachment != null) {
                }
            }
        },

        END() {
            @Override public boolean shouldMoveRovers() { return true; }

            @Override public void enter(MineSweeperChallenge challenge) {
                // PiNoonAttachment player1Attachment = challenge.getPlayerOneAttachment();

                challenge.getGameMessage().setInGame(false);
                challenge.getGameMessage().setWaiting(false);
                setTimer(3000);
            }

            @Override public void update(MineSweeperChallenge challenge) {
                if (isTimerDone()) {
                    challenge.stateMachine.toState(WAITING_START, challenge);
                }
            }

            @Override public void exit(MineSweeperChallenge challenge) {
                Rover player1 = challenge.getRover();
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

        @Override public void enter(MineSweeperChallenge challenge) {}
        @Override public void update(MineSweeperChallenge challenge) {}
        @Override public void exit(MineSweeperChallenge s) {}

        public boolean shouldMoveRovers() { return false; }
    }
}
