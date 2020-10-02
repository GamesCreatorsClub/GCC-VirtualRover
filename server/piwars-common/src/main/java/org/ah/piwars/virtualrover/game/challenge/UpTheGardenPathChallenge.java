package org.ah.piwars.virtualrover.game.challenge;

import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Shape2D;

import org.ah.piwars.virtualrover.game.GameMessageObject;
import org.ah.piwars.virtualrover.game.PiWarsGame;
import org.ah.piwars.virtualrover.game.rovers.Rover;
import org.ah.themvsus.engine.common.game.Game;
import org.ah.themvsus.engine.common.game.GameObject;
import org.ah.themvsus.engine.common.game.GameState;
import org.ah.themvsus.engine.common.input.PlayerInputs;
import org.ah.themvsus.engine.common.statemachine.State;
import org.ah.themvsus.engine.common.statemachine.StateMachine;

import java.util.List;

import static org.ah.piwars.virtualrover.engine.utils.CollisionUtils.overlaps;
import static org.ah.piwars.virtualrover.engine.utils.CollisionUtils.polygonFromBox;

import static java.util.Arrays.asList;

public class UpTheGardenPathChallenge extends CameraAbstractChallenge {

    public static final float CHALLENGE_WIDTH = 1500;
    public static float WALL_HEIGHT = 200;

    public static final List<Polygon> WALL_POLYGONS = asList(
            polygonFromBox(-CHALLENGE_WIDTH / 2, -CHALLENGE_WIDTH / 2 - 1,  CHALLENGE_WIDTH / 2, -CHALLENGE_WIDTH / 2),
            polygonFromBox(-CHALLENGE_WIDTH / 2 - 1, -CHALLENGE_WIDTH / 2, -CHALLENGE_WIDTH / 2,  CHALLENGE_WIDTH / 2),
            polygonFromBox(-CHALLENGE_WIDTH / 2,  CHALLENGE_WIDTH / 2,  CHALLENGE_WIDTH / 2,  CHALLENGE_WIDTH / 2 + 1),
            polygonFromBox( CHALLENGE_WIDTH / 2, -CHALLENGE_WIDTH / 2,  CHALLENGE_WIDTH / 2 + 1,  CHALLENGE_WIDTH / 2));

    private StateMachine<UpTheGardenPathChallenge, ChallengeState> stateMachine = new StateMachine<UpTheGardenPathChallenge, ChallengeState>();

    private Polygon pathLine = createPathLine();

    private Rectangle finishArea = new Rectangle(300, 485, 450, 265);

    public UpTheGardenPathChallenge(PiWarsGame game, String name) {
        super(game, name);
        setWallPolygons(WALL_POLYGONS);
        stateMachine.setCurrentState(ChallengeState.WAITING_START);
    }

    public Polygon getPathLine() {
        return pathLine;
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
            orientation.setEulerAnglesRad(0f, 0f, 0f);
            player1.setPosition(-600, -650);
            player1.setOrientation(orientation);
        }
    }

    @Override
    public boolean processPlayerInputs(int playerId, PlayerInputs playerInputs) {
        boolean doMove = stateMachine.getCurrentState().shouldMoveRovers();

        return doMove;
    }

    private static Polygon createPathLine() {
        return new Polygon(new float[]{
                -750, -634,
                -104, -635,
                110, -510,
                407, -513,
                483, -487,
                528, -439,
                554, -388,
                563, -330,
                559, -275,
                534, -227,
                509, -189,
                464, -155,
                407, -137,
                100, -135,
                -90, 56,
                -375, 55,
                -437, 69,
                -489, 106,
                -528, 159,
                -547, 222,
                -544, 292,
                -519, 348,
                -472, 404,
                -418, 429,
                -359, 436,
                -103, 586,
                703, 583,
            });
    }

    private boolean isAtFinish() {
        Rover rover = getRover();

        for (Shape2D shape : rover.getCollisionPolygons()) {
            if (shape instanceof Polygon) {
                Polygon polygon = (Polygon)shape;
                float[] vertices = polygon.getTransformedVertices();
                for (int i = 0; i < vertices.length; i = i + 2) {
                    float x = vertices[i];
                    float y = vertices[i + 1];
                    if (!finishArea.contains(x, y)) {
                        return false;
                    }
                }
            } else {
                throw new RuntimeException("Cannot overlap " + shape.getClass().getSimpleName() + "(" + shape.getClass().getName() + ") with polygon");
            }
        }
        return true;
    }

    private boolean isAstray() {
        Rover rover = getRover();

        for (Shape2D shape : rover.getCollisionPolygons()) {
            if (shape instanceof Polygon) {
                if (!overlaps(pathLine, (Polygon)shape, true)) {
                    return true;
                }
            } else {
                throw new RuntimeException("Cannot overlap " + shape.getClass().getSimpleName() + "(" + shape.getClass().getName() + ") with polygon");
            }
        }

        return false;
    }


    private enum ChallengeState implements State<UpTheGardenPathChallenge> {

        WAITING_START() {
            @Override public void enter(UpTheGardenPathChallenge challenge) {
                Game game = challenge.getGame();

                if (game.containsObject(challenge.playerId)) {
                    game.removeGameObject(challenge.playerId);
                    challenge.playerId = 0;
                }
//                for (int barrelId : challenge.barrels.items) {
//                    GameObject barrel = game.getCurrentGameState().get(barrelId);
//                    if (barrel != null) {
//                        game.removeGameObject(barrelId);
//                    }
//                }
//                challenge.barrels.clear();

                GameMessageObject gameMessageObject = challenge.getGameMessage();
                gameMessageObject.setInGame(false);
                gameMessageObject.setWaiting(true);

                challenge.removeTimer();
            }

            @Override public void update(UpTheGardenPathChallenge challenge) {
                if (challenge.playerId != 0) {
                    challenge.stateMachine.toState(ChallengeState.GAME, challenge);
                }
            }

            @Override public void exit(UpTheGardenPathChallenge challenge) {
                challenge.stopRovers();
            }
        },

        GAME() {
            @Override public boolean shouldMoveRovers() { return true; }

            @Override public void enter(UpTheGardenPathChallenge challenge) {
                challenge.startTimer(3000);
                challenge.resetRover();
                setTimer(1000);

                GameMessageObject gameMessageObject = challenge.getGameMessage();
                gameMessageObject.setMessage("GO!", false);
                gameMessageObject.setInGame(true);
                gameMessageObject.setWaiting(false);

                // TODO
                // challenge.soundManager.playFight();
            }

            @Override public void update(UpTheGardenPathChallenge challenge) {
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

                if (challenge.isAstray()) {
                    challenge.stopTimer();
                    challenge.getGameMessage().setMessage("You've astrayed from the path!", false);
                    challenge.stateMachine.toState(ChallengeState.END, challenge);
                }

                if (challenge.isAtFinish()) {
                    challenge.stopTimer();
                    challenge.getGameMessage().setMessage("Well done!", false);
                    challenge.stateMachine.toState(ChallengeState.END, challenge);
                }
            }
        },

        END() {
            @Override public boolean shouldMoveRovers() { return true; }

            @Override public void enter(UpTheGardenPathChallenge challenge) {
                GameMessageObject gameMessageObject = challenge.getGameMessage();
                gameMessageObject.setInGame(false);
                gameMessageObject.setWaiting(false);
                challenge.stopTimer();

                setTimer(3000);
            }

            @Override public void update(UpTheGardenPathChallenge challenge) {
                if (isTimerDone()) {
                    challenge.stateMachine.toState(WAITING_START, challenge);
                }
            }

            @Override public void exit(UpTheGardenPathChallenge challenge) {
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

        @Override public void enter(UpTheGardenPathChallenge challenge) {}
        @Override public void update(UpTheGardenPathChallenge challenge) {}
        @Override public void exit(UpTheGardenPathChallenge s) {}

        public boolean shouldMoveRovers() { return false; }
    }
}
