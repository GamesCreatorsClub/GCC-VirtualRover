package org.ah.piwars.virtualrover.game.challenge;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Shape2D;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.IntArray;

import org.ah.piwars.virtualrover.game.GameMessageObject;
import org.ah.piwars.virtualrover.game.PiWarsGame;
import org.ah.piwars.virtualrover.game.PiWarsGameTypeObject;
import org.ah.piwars.virtualrover.game.attachments.CameraAttachment;
import org.ah.piwars.virtualrover.game.objects.BarrelObject;
import org.ah.piwars.virtualrover.game.objects.BarrelObject.BarrelColour;
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

import static org.ah.piwars.virtualrover.engine.utils.CollisionUtils.findCollidedShape;
import static org.ah.piwars.virtualrover.engine.utils.CollisionUtils.getCircleOverlapsPolygonSeqment;
import static org.ah.piwars.virtualrover.engine.utils.CollisionUtils.polygonFromBox;

import static java.util.Arrays.asList;

public class EcoDisasterChallenge extends CameraAbstractChallenge {

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

    private BarrelObject[] barrelObjects = new BarrelObject[0];
    private Vector2 start = new Vector2();
    private Vector2 end = new Vector2();
    private Vector2 point = new Vector2();
    private Vector2 displacement = new Vector2();

    public EcoDisasterChallenge(PiWarsGame game, String name) {
        super(game, name);
        setWallPolygons(WALL_POLYGONS);
        stateMachine.setCurrentState(ChallengeState.WAITING_START);
    }

    @Override
    public boolean checkForCollision(GameObjectWithPosition object, Iterable<GameObjectWithPosition> objects) {
        if (object instanceof Rover) {
            Rover rover = (Rover)object;
            List<Shape2D> roverPolygons = rover.getCollisionPolygons();
            for (GameObjectWithPosition o : objects) {
                if (o != object && o instanceof BarrelObject) {
                    BarrelObject barrel = ((BarrelObject)o);
                    Circle barrelCircle = barrel.getCirle();
                    Shape2D collidedShape = findCollidedShape(barrelCircle, roverPolygons);
                    if (collidedShape != null && !tryMovingBarrels(rover, collidedShape, barrel)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private boolean tryMovingBarrels(Rover rover, Shape2D collidedShape, BarrelObject startingBarrel) {
        for (int i = 0; i < barrels.size; i++) {
            barrelObjects[i] = piwarsGame.getCurrentGameState().get(barrels.get(i));
            barrelObjects[i].getCirle(); // Make sure all circles are updated
        }

        Circle startingBarrelCircle = startingBarrel.getCirleInt();
        if (collidedShape instanceof Polygon) {
            float distance = getCircleOverlapsPolygonSeqment(startingBarrelCircle, (Polygon)collidedShape, start, end);
//
//            line.set(end);
//            line.sub(start);
//            line.nor();
//            line.rotate90(1);
//            line.nor();
//
//            line.scl(barrelCircle.radius - distance + 0.1f);
//            line.add(barrelCircle.x, barrelCircle.y);
//
//            distance = Intersector.distanceLinePoint(start.x, start.y, end.x, end.y, line.x, line.y);
//            if (distance < barrelCircle.radius - 0.01f) {
//                line.sub(barrelCircle.x, barrelCircle.y);
//                line.sub(barrelCircle.x, barrelCircle.y);
//            }

            point.set(startingBarrelCircle.x, startingBarrelCircle.y);
            Intersector.intersectSegmentCircleDisplace(start, end, point, startingBarrelCircle.radius, displacement);

            displacement.nor();
            displacement.scl(startingBarrelCircle.radius - distance + 0.1f);

            point.add(displacement);

            startingBarrelCircle.x = point.x;
            startingBarrelCircle.y = point.y;
            if (!canMove(displacement, startingBarrelCircle)) {
                return false;
            }
        } else if (collidedShape instanceof Circle) {
            Circle shapeCircle = ((Circle)collidedShape);
            point.x = shapeCircle.x;
            point.y = shapeCircle.y;
            end.x = startingBarrelCircle.x;
            end.y = startingBarrelCircle.y;
            end.sub(start);
            end.nor();
            end.scl(shapeCircle.radius + startingBarrelCircle.radius);
            point.add(end);

            startingBarrelCircle.x = point.x;
            startingBarrelCircle.y = point.y;

            if (!canMove(displacement, startingBarrelCircle)) {
                return false;
            }
        } else {
            // TODO cannot handle other shapes yet
            return false;
        }

        if (checkBarrelMovements(startingBarrelCircle)) {
            for (BarrelObject barrel : barrelObjects) {
                startingBarrelCircle = barrel.getCirleInt();
                Vector3 position = barrel.getPosition();
                if (startingBarrelCircle.x != position.x || startingBarrelCircle.y != position.y) {
                    barrel.setPosition(startingBarrelCircle.x, startingBarrelCircle.y);
                }
            }
            return true;
        }
        return false;
    }

    private boolean checkBarrelMovements(Circle currentCircle) {
        while (true) {

            int i = 0;
            Circle nextCircle = null;
            while (i < barrelObjects.length && nextCircle == null) {
                BarrelObject barrel = barrelObjects[i];
                Circle barrelCircle = barrel.getCirleInt();
                if (barrelCircle != currentCircle) {
                    float d = barrelCircle.radius + currentCircle.radius;
                    d = d * d;
                    if (distance2(barrelCircle.x, barrelCircle.y, currentCircle.x, currentCircle.y) < d) {

                        point.x = currentCircle.x;
                        point.y = currentCircle.y;
                        displacement.x = barrelCircle.x;
                        displacement.y = barrelCircle.y;
                        displacement.sub(point);
                        displacement.nor();
                        displacement.scl(currentCircle.radius + barrelCircle.radius + 0.1f);
                        point.add(displacement);

                        barrelCircle.x = point.x;
                        barrelCircle.y = point.y;

                        if (!canMove(displacement, barrelCircle)) {
                            return false;
                        }
                        nextCircle = barrelCircle;
                    }
                }
                i++;
            }
            if (nextCircle != null) {
                currentCircle = nextCircle;
            } else {
                return true;
            }
        }
    }

    private boolean canMove(Vector2 movement, Circle barrelCircle) {
        float x = barrelCircle.x;
        float y = barrelCircle.y;
        float r = barrelCircle.radius;
        if ((x - r < -CHALLENGE_WIDTH / 2)
                || (x + r > CHALLENGE_WIDTH / 2)
                || (y - r < -CHALLENGE_WIDTH / 2)
                || (y + r > CHALLENGE_WIDTH / 2)) {
            return false;
        }
        return true;
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
        if (barrelObjects.length != barrels.size) {
            barrelObjects = new BarrelObject[barrels.size];
        }
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
