package org.ah.piwars.virtualrover.game.challenge;

import com.badlogic.gdx.math.Polygon;

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

import java.util.List;

import static org.ah.piwars.virtualrover.engine.utils.CollisionUtils.polygonFromBox;
import static org.ah.piwars.virtualrover.engine.utils.CollisionUtils.polygonsOverlap;

import static java.util.Arrays.asList;

public class CanyonsOfMarsChallenge extends CameraAbstractChallenge {

    public static float CHALLENGE_WIDTH = 3400;
    public static float CHALLENGE_HEIGHT = 1830;

    public static float WALL_HEIGHT = 200;

    public static final Polygon FLOOR_POLYGON = polygonFromBox(-CHALLENGE_WIDTH / 2, -CHALLENGE_HEIGHT / 2, CHALLENGE_WIDTH / 2, CHALLENGE_HEIGHT / 2);

    public static final List<Polygon> WALL_POLYGONS = asList(
                polygonFromBox(1690, -915, 1700, 915),
                polygonFromBox(-1700, 905, 1700, 915),
                polygonFromBox(-1700, -915, -1690, 915),
                polygonFromBox(-1700, -915, 340, -905),

                polygonFromBox(1010, -915, 1020, 315),
                polygonFromBox(340, 305, 1020, 315),
                polygonFromBox(330, -315, 340, 315),
                polygonFromBox(-1020, -315, 340, -305),
                polygonFromBox(-1020, -315, -1010, 315),

                polygonFromBox(-330, 305, -340, 915)
            );

    public static final Polygon START_POLIGON = polygonFromBox(1015, -920, 1700, -915);
    public static final Polygon END_POLIGON = polygonFromBox(300, -920, 1015, -915);

    private StateMachine<CanyonsOfMarsChallenge, ChallengeState> stateMachine = new StateMachine<CanyonsOfMarsChallenge, ChallengeState>();

    public CanyonsOfMarsChallenge(PiWarsGame game, String name) {
        super(game, name);
        wallPolygons = WALL_POLYGONS;
        stateMachine.setCurrentState(ChallengeState.WAITING_START);
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
            orientation.setEulerAnglesRad(0f, 0f, (float)(Math.PI / 2f));
            player1.setPosition(1360, -610);
            player1.setOrientation(orientation);
        }
    }

    @Override
    public boolean processPlayerInputs(int playerId, PlayerInputs playerInputs) {
        boolean doMove = stateMachine.getCurrentState().shouldMoveRovers();

        return doMove;
    }

    private enum ChallengeState implements State<CanyonsOfMarsChallenge> {

        WAITING_START() {
            @Override public void enter(CanyonsOfMarsChallenge challenge) {
                Game game = challenge.getGame();

                if (game.containsObject(challenge.playerId)) {
                    game.removeGameObject(challenge.playerId);
                    challenge.playerId = 0;
                }

                challenge.getGameMessage().setInGame(false);
                challenge.getGameMessage().setWaiting(true);
            }

            @Override public void update(CanyonsOfMarsChallenge challenge) {
                if (challenge.playerId != 0) {
                    challenge.stateMachine.toState(ChallengeState.GAME, challenge);
                }
            }

            @Override public void exit(CanyonsOfMarsChallenge challenge) {
                challenge.resetRover();
                challenge.stopRovers();
            }
        },

        GAME() {
            @Override public boolean shouldMoveRovers() { return true; }

            @Override public void enter(CanyonsOfMarsChallenge challenge) {
                challenge.resetRover();
                setTimer(1000);
                challenge.setMessage("GO!", false);
                challenge.getGameMessage().setInGame(true);
                challenge.getGameMessage().setWaiting(false);

                // TODO
                // challenge.soundManager.playFight();
            }

            @Override public void update(CanyonsOfMarsChallenge challenge) {
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

            @Override public void enter(CanyonsOfMarsChallenge challenge) {
                // PiNoonAttachment player1Attachment = challenge.getPlayerOneAttachment();

                challenge.getGameMessage().setInGame(false);
                challenge.getGameMessage().setWaiting(false);
                setTimer(3000);
            }

            @Override public void update(CanyonsOfMarsChallenge challenge) {
                if (isTimerDone()) {
                    challenge.stateMachine.toState(WAITING_START, challenge);
                }
            }

            @Override public void exit(CanyonsOfMarsChallenge challenge) {
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

        @Override public void enter(CanyonsOfMarsChallenge challenge) {}
        @Override public void update(CanyonsOfMarsChallenge challenge) {}
        @Override public void exit(CanyonsOfMarsChallenge s) {}

        public boolean shouldMoveRovers() { return false; }
    }
}
