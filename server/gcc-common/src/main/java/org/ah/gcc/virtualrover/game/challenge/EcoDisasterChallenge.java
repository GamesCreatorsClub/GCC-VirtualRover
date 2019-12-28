package org.ah.gcc.virtualrover.game.challenge;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import org.ah.gcc.virtualrover.game.GCCCollidableObject;
import org.ah.gcc.virtualrover.game.GCCGame;
import org.ah.gcc.virtualrover.game.GCCGameTypeObject;
import org.ah.gcc.virtualrover.game.GameMessageObject;
import org.ah.gcc.virtualrover.game.PiNoonAttachment;
import org.ah.gcc.virtualrover.game.Rover;
import org.ah.themvsus.engine.common.game.Game;
import org.ah.themvsus.engine.common.game.GameObject;
import org.ah.themvsus.engine.common.game.GameObjectWithPosition;
import org.ah.themvsus.engine.common.game.GameObjectWithPositionAndOrientation;
import org.ah.themvsus.engine.common.game.GameState;
import org.ah.themvsus.engine.common.input.PlayerInputs;
import org.ah.themvsus.engine.common.statemachine.State;
import org.ah.themvsus.engine.common.statemachine.StateMachine;

import java.util.ArrayList;
import java.util.List;

import static org.ah.gcc.virtualrover.engine.utils.CollisionUtils.polygonFromBox;
import static org.ah.gcc.virtualrover.engine.utils.CollisionUtils.polygonsOverlap;

import static java.util.Arrays.asList;

public class EcoDisasterChallenge extends AbstractChallenge {

    private List<Polygon> piNoonPolygons = asList(
            polygonFromBox(-1100, -1101,  1100, -1100),
            polygonFromBox(-1101, -1100, -1100,  1100),
            polygonFromBox(-1100,  1100,  1100,  1101),
            polygonFromBox( 1100, -1100,  1101,  1100));

    private int gameMessageId;
    private GameState gameStateGameMessageIsDefinedOn;
    private GameMessageObject cachedGameMessageObject;

    private Quaternion orientation = new Quaternion();

    private int playerId;
    private List<PiNoonAttachment> piNoonAttachments = new ArrayList<PiNoonAttachment>();

    private StateMachine<EcoDisasterChallenge, ChallengeState> stateMachine = new StateMachine<EcoDisasterChallenge, ChallengeState>();

    private GCCGame gccGame;

    public EcoDisasterChallenge(Game game, String name) {
        super(game, name);
        gccGame = (GCCGame)game;
        stateMachine.setCurrentState(ChallengeState.WAITING_START);
    }

    @Override
    public List<Polygon> getCollisionPolygons() {
        return piNoonPolygons;
    }

    @Override
    public boolean checkForCollision(GameObjectWithPosition object, Iterable<GameObjectWithPosition> objects) {
        if (object instanceof GCCCollidableObject) {
            if (polygonsOverlap(getCollisionPolygons(), ((GCCCollidableObject)object).getCollisionPolygons())) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void process(GameState currentGameState) {
        piNoonAttachments.clear();
        for (GameObject o : currentGameState.gameObjects().values()) {
            if (o instanceof Rover) {
                if (o.isAdded()) {
                    if (playerId == 0) {
                        playerId = o.getId();
                        resetRover();
                    }
                } else if (o.isRemoved()) {
                    if (o.getId() == playerId) {
                        playerId = 0;
                    }
                }
            } else if (o instanceof PiNoonAttachment) {
                piNoonAttachments.add((PiNoonAttachment)o);
            }
        }

        for (PiNoonAttachment piNoonAttachment : piNoonAttachments) {
            GameObject parent = game.getCurrentGameState().get(piNoonAttachment.getParentId());
            if (parent instanceof GameObjectWithPositionAndOrientation) {
                GameObjectWithPositionAndOrientation gameObject = (GameObjectWithPositionAndOrientation)parent;

                Vector3 position = gameObject.getPosition();
                piNoonAttachment.setPosition(position.x, position.y, position.z);
                piNoonAttachment.setOrientation(gameObject.getOrientation());
            }
        }

        if (stateMachine.getCurrentState().shouldCollideBalloons()) {
            for (PiNoonAttachment piNoonAttachment : piNoonAttachments) {
                int balloonBits = piNoonAttachment.getBalloonBits();
                if ((balloonBits & 7) != 0) {
                    Circle[] balloons = new Circle[3];
                    for (int balloonNo = 0; balloonNo < 3; balloonNo++) {
                        int balloonBit = 1 << balloonNo;
                        if ((balloonBits & balloonBit) != 0) {
                            balloons[balloonNo] = piNoonAttachment.getBalloon(balloonNo);
                        }
                    }
                    for (PiNoonAttachment otherPlayerAttachment : piNoonAttachments) {

                        Vector2 sharpEndOtherPlayer = otherPlayerAttachment.getSharpEnd();
                        for (int balloonNo = 0; balloonNo < 3; balloonNo++) {
                            if (balloons[balloonNo] != null && balloons[balloonNo].contains(sharpEndOtherPlayer)) {
                                balloons[balloonNo] = null;

                                int balloonBit = ~(1 << balloonNo);
                                balloonBits &= balloonBit;
                                piNoonAttachment.setBalloonBits(balloonBits);

                                if ((balloonBits & 7) == 0) {
                                    otherPlayerAttachment.setScore(otherPlayerAttachment.getScore() + 1);
                                }
                            }
                        }
                    }
                }
            }
        }
        stateMachine.update(this);
    }

    @Override
    public void beforeGameObjectAdded(GameObject gameObject) {
    }

    @Override
    public void afterGameObjectAdded(GameObject gameObject) {
        if (gameObject instanceof Rover) {
            if (game.isServer()) {
                PiNoonAttachment attachment = game.getGameObjectFactory().newGameObjectWithId(GCCGameTypeObject.PiNoonAttachment, game.newId());
                attachment.attachToRover((Rover)gameObject);
                game.addNewGameObjectImmediately(attachment);
            }
        } else if (gameObject instanceof PiNoonAttachment) {
            PiNoonAttachment attachment = (PiNoonAttachment)gameObject;
            Rover rover = game.getCurrentGameState().get(attachment.getParentId());
            if (rover != null) {
                attachment.attachToRover(rover);
            } else {
                // TODO add error here!!!
            }
        }
    }

    @Override
    public void gameObjectRemoved(GameObject gameObject) {
        if (gameObject instanceof Rover) {
            Rover rover = (Rover)gameObject;
            if (rover.getAttachemntId() != 0) {
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
            GameState currentGameState = gccGame.getCurrentGameState();
            gameMessageObject = gccGame.getCurrentGameState().get(gameMessageId);
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
            gameStateGameMessageIsDefinedOn = gccGame.getCurrentGameState();
            gameMessageObject = (GameMessageObject) gccGame.getGameObjectFactory().newGameObjectWithId(GCCGameTypeObject.GameMessageObject, gccGame.newId());
            cachedGameMessageObject = gameMessageObject;
            gccGame.addNewGameObject(gameMessageObject);
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

    private PiNoonAttachment getPlayerAttachment() {
        Rover rover = getPlayer();
        if (rover != null) {
            PiNoonAttachment attachment = gccGame.getCurrentGameState().get(rover.getAttachemntId());
            return attachment;
        }
        return null;
    }

    private void resetRover() {
        Rover player1 = getPlayer();
        if (player1 != null) {
            orientation.setEulerAnglesRad(0f, 0f, (float)(Math.PI + Math.PI / 4f));
            player1.setPosition(0, 700);
            player1.setOrientation(orientation);
            // player1.setRoverColour(RoverColour.BLUE);
        }
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

    private enum ChallengeState implements State<EcoDisasterChallenge> {

        WAITING_START() {
            @Override public void enter(EcoDisasterChallenge challenge) {
                Game game = challenge.getGame();

                if (game.containsObject(challenge.playerId)) {
                    game.removeGameObject(challenge.playerId);
                    challenge.playerId = 0;
                }

                challenge.getGameMessage().setInGame(false);
                challenge.getGameMessage().setWaiting(true);
            }

            @Override public void update(EcoDisasterChallenge challenge) {
                if (challenge.playerId != 0) {
                    challenge.stateMachine.toState(ChallengeState.GAME, challenge);
                }
            }

            @Override public void exit(EcoDisasterChallenge challenge) {
                challenge.resetRover();
                challenge.stopRovers();
            }
        },

        GAME() {
            @Override
            public boolean shouldCollideBalloons() { return true; }
            @Override public boolean shouldMoveRovers() { return true; }

            @Override public void enter(EcoDisasterChallenge challenge) {
                challenge.resetRover();
                setTimer(1000);
                challenge.setMessage("GO!", false);
                challenge.getGameMessage().setInGame(true);
                challenge.getGameMessage().setWaiting(false);

                // TODO
                // challenge.soundManager.playFight();
            }

            @Override public void update(EcoDisasterChallenge challenge) {
                super.update(challenge);

                if (isTimerDone()) {
                    challenge.setMessage(null, false);
                }

                PiNoonAttachment player1Attachment = challenge.getPlayerAttachment();

                if (player1Attachment != null) {
                }
            }
        },

        END() {
            @Override public boolean shouldMoveRovers() { return true; }

            @Override public void enter(EcoDisasterChallenge challenge) {
                // PiNoonAttachment player1Attachment = challenge.getPlayerOneAttachment();

                challenge.getGameMessage().setInGame(false);
                challenge.getGameMessage().setWaiting(false);
                setTimer(3000);
            }

            @Override public void update(EcoDisasterChallenge challenge) {
                if (isTimerDone()) {
                    challenge.stateMachine.toState(WAITING_START, challenge);
                }
            }

            @Override public void exit(EcoDisasterChallenge challenge) {
                Rover player1 = challenge.getPlayer();
                challenge.gccGame.removeGameObject(player1.getId());
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

        @Override public void enter(EcoDisasterChallenge challenge) {}
        @Override public void update(EcoDisasterChallenge challenge) {}
        @Override public void exit(EcoDisasterChallenge s) {}

        public boolean shouldMoveRovers() { return false; }
        public boolean shouldCollideBalloons() { return false; }
    }
}
