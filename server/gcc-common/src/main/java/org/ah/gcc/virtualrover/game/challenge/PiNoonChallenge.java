package org.ah.gcc.virtualrover.game.challenge;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector2;

import org.ah.gcc.virtualrover.game.GCCCollidableObject;
import org.ah.gcc.virtualrover.game.GCCGame;
import org.ah.gcc.virtualrover.game.GCCGameTypeObject;
import org.ah.gcc.virtualrover.game.Rover;
import org.ah.gcc.virtualrover.game.Rover.RoverColour;
import org.ah.gcc.virtualrover.game.GameMessageObject;
import org.ah.themvsus.engine.common.game.Game;
import org.ah.themvsus.engine.common.game.GameObject;
import org.ah.themvsus.engine.common.game.GameObjectWithPosition;
import org.ah.themvsus.engine.common.game.GameState;
import org.ah.themvsus.engine.common.input.PlayerInputs;
import org.ah.themvsus.engine.common.statemachine.State;
import org.ah.themvsus.engine.common.statemachine.StateMachine;

import java.util.List;

import static org.ah.gcc.virtualrover.engine.utils.CollisionUtils.polygonFromBox;
import static org.ah.gcc.virtualrover.engine.utils.CollisionUtils.polygonsOverlap;

import static java.util.Arrays.asList;

public class PiNoonChallenge extends AbstractChallenge {

    private List<Polygon> piNoonPolygons = asList(
            polygonFromBox(-1000, -1001,  1000, -1000),
            polygonFromBox(-1001, -1000, -1000,  1000),
            polygonFromBox(-1000,  1000,  1000,  1001),
            polygonFromBox( 1000, -1000,  1001,  1000));

    private int gameMessageId;

    private String winner;
    private int countdown;

    private Quaternion orientation = new Quaternion();

    private int player1Id;
    private int player2Id;

    private StateMachine<PiNoonChallenge, ChallengeState> stateMachine = new StateMachine<PiNoonChallenge, ChallengeState>();

    private GCCGame gccGame;

    public PiNoonChallenge(Game game, String name) {
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

        if (object instanceof Rover && stateMachine.getCurrentState().shouldCollideBalloons()) {
            Rover player = (Rover)object;
            int balloonBits = player.getChallengeBits();
            if ((balloonBits & 7) != 0) {
                Circle[] balloons = new Circle[3];
                for (int balloonNo = 0; balloonNo < 3; balloonNo++) {
                    int balloonBit = 1 << balloonNo;
                    if ((balloonBits & balloonBit) != 0) {
                        balloons[balloonNo] = player.getBalloon(balloonNo);
                    }
                }
                for (GameObjectWithPosition o : objects) {
                    if (o != object && o instanceof Rover) {
                        Rover otherPlayer = (Rover)o;

                        Vector2 sharpEndOtherPlayer = otherPlayer.getSharpEnd();
                        for (int balloonNo = 0; balloonNo < 3; balloonNo++) {
                            if (balloons[balloonNo] != null && balloons[balloonNo].contains(sharpEndOtherPlayer)) {
                                balloons[balloonNo] = null;

                                int balloonBit = ~ (1 << balloonNo);
                                balloonBits &= balloonBit;
                                player.setChallengeBits(balloonBits);

                                if ((balloonBits & 7) == 0) {
                                    otherPlayer.setScore(otherPlayer.getScore() + 1);
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    @Override
    public void process() {
        GameState currentGameState = gccGame.getCurrentGameState();
        for (GameObject o : currentGameState.gameObjects().values()) {
            if (o instanceof Rover) {
                if (o.isAdded()) {
                    if (player1Id == 0) {
                        player1Id = o.getId();
                        resetPlayer1Rover();
                    } else if (player2Id == 0) {
                        player2Id = o.getId();
                        resetPlayer2Rover();
                    }
                } else if (o.isRemoved()) {
                    if (o.getId() == player1Id) {
                        player1Id = 0;
                    }
                    if (o.getId() == player2Id) {
                        player2Id = 0;
                    }
                }
            }
        }

        stateMachine.update(this);
    }

    protected void setMessage(String message, boolean flashing) {
        getGameMessage().setMessage(message, flashing);
    }

    private GameMessageObject getGameMessage() {
        GameMessageObject gameMessageObject = null;

        if (gameMessageId != 0) {
            gameMessageObject = (GameMessageObject) gccGame.getCurrentGameState().get(gameMessageId);
        }

        if (gameMessageObject == null) {
            gameMessageObject = (GameMessageObject) gccGame.getGameObjectFactory().newGameObjectWithId(GCCGameTypeObject.GameMessageObject, gccGame.newId());
            gccGame.addNewGameObject(gameMessageObject);
            gameMessageId = gameMessageObject.getId();
        }
        return gameMessageObject;
    }

    private Rover getPlayerOne() {
        if (player1Id != 0) {
            return (Rover) game.getCurrentGameState().get(player1Id);
        }
        return null;
    }

    private Rover getPlayerTwo() {
        if (player2Id != 0) {
            return (Rover) game.getCurrentGameState().get(player2Id);
        }
        return null;
    }

    private void resetRovers() {
        resetPlayer1Rover();
        resetPlayer2Rover();
    }

    protected void resetPlayer1Rover() {
        Rover player1 = getPlayerOne();
        if (player1 != null) {
            orientation.setEulerAnglesRad(0f, 0f, (float)(Math.PI + Math.PI / 4f));
            player1.setPosition(700, 700);
            player1.setOrientation(orientation);
            player1.setRoverColour(RoverColour.BLUE);
            removeBalloons(player1);
        }
    }

    protected void resetPlayer2Rover() {
        Rover player2 = getPlayerTwo();
        if (player2 != null) {
            orientation.setEulerAnglesRad(0f, 0f, (float)(Math.PI / 4f));
            player2.setPosition(-700, -700);
            player2.setOrientation(orientation);
            player2.setRoverColour(RoverColour.GREEN);
            removeBalloons(player2);
        }
    }

    protected void resetRoverBalloons() {
        for (GameObject gameObject : gccGame.getCurrentGameState().gameObjects().values()) {
            if (gameObject instanceof Rover) {
                resetBallons((Rover)gameObject);
            }
        }
    }

    protected void removeRoverBalloons() {
        for (GameObject gameObject : gccGame.getCurrentGameState().gameObjects().values()) {
            if (gameObject instanceof Rover) {
                removeBalloons((Rover)gameObject);
            }
        }
    }

    protected void resetBallons(Rover player) {
        player.setChallengeBits(7);
    }

    protected void removeBalloons(Rover player) {
        player.setChallengeBits(0);
    }

    private void stopRovers() {
        Rover player1 = getPlayerOne();
        if (player1 != null) {
            player1.setVelocity(0, 0);
            player1.setTurnSpeed(0);
            player1.setSpeed(0);
        }
        Rover player2 = getPlayerTwo();
        if (player2 != null) {
            player2.setVelocity(0, 0);
            player2.setTurnSpeed(0);
            player2.setSpeed(0);
        }
    }

    @Override
    public boolean processPlayerInputs(int playerId, PlayerInputs playerInputs) {
        boolean doMove = stateMachine.getCurrentState().shouldMoveRovers();
        if (!doMove) {
            playerInputs.pop(gccGame.getCurrentFrameId());
        }
        return doMove;
    }

    private enum ChallengeState implements State<PiNoonChallenge> {

        WAITING_START() {
            @Override public void enter(PiNoonChallenge challenge) {
                Game game = challenge.getGame();

                if (game.containsObject(challenge.player2Id)) {
                    game.removeGameObject(challenge.player2Id);
                    challenge.player2Id = 0;
                }
                if (game.containsObject(challenge.player1Id)) {
                    game.removeGameObject(challenge.player1Id);
                    challenge.player1Id = 0;
                }

                challenge.winner = null;
                challenge.getGameMessage().setInGame(false);
                challenge.getGameMessage().setWaiting(true);
            }

            @Override public void update(PiNoonChallenge challenge) {
                if (challenge.player1Id != 0 && challenge.player2Id != 0) {
                    challenge.stateMachine.toState(ChallengeState.BREAK, challenge);
                }
            }

            @Override public void exit(PiNoonChallenge challenge) {
                challenge.resetRovers();
                challenge.stopRovers();
            }
        },

        BREAK() {
            @Override public boolean shouldMoveRovers() { return true; }

            @Override public void enter(PiNoonChallenge challenge) {
                challenge.getGameMessage().setInGame(true);
                challenge.getGameMessage().setWaiting(false);
                if (challenge.winner != null) {
                    setTimer(3000);
                    challenge.setMessage(challenge.winner + " won that round!", false);
                } else {
                    setTimer(1000);
                    challenge.setMessage(null, false);
                }
            }

            @Override public void update(PiNoonChallenge challenge) {
                super.update(challenge);

                if (isTimerDone()) {
                    challenge.stateMachine.toState(ChallengeState.ROUND, challenge);
                }
            }
        },

        ROUND() {
            @Override public void enter(PiNoonChallenge challenge) {
                challenge.resetRovers();
                challenge.resetRoverBalloons();
                challenge.stopRovers();

                setTimer(1000);
                Rover player1 = challenge.getPlayerOne();
                Rover player2 = challenge.getPlayerTwo();
                challenge.setMessage("round " + (player1.getScore() + player2.getScore() + 1), false);
                challenge.getGameMessage().setInGame(true);
                challenge.getGameMessage().setWaiting(false);
            }

            @Override public void update(PiNoonChallenge challenge) {
                super.update(challenge);

                if (isTimerDone()) {
                    challenge.countdown = 3;
                    challenge.stateMachine.toState(ChallengeState.ROUND_COUNTDOWN, challenge);
                }
            }
        },

        ROUND_COUNTDOWN() {
            @Override public void enter(PiNoonChallenge challenge) {
                challenge.stopRovers();
                setTimer(1000);
                challenge.setMessage(Integer.toString(challenge.countdown), false);
                challenge.getGameMessage().setInGame(true);
                challenge.getGameMessage().setWaiting(false);
                if (challenge.countdown == 2) {
                    // TODO Add this
                    // challenge.soundManager.playReady();
                }
            }

            @Override public void update(PiNoonChallenge challenge) {
                super.update(challenge);

                if (isTimerDone()) {
                    challenge.countdown--;
                    if (challenge.countdown == 0) {
                        challenge.stateMachine.toState(ChallengeState.GAME, challenge);
                    } else {
                        enter(challenge);
                    }
                }
            }
        },

        GAME() {
            @Override
            public boolean shouldCollideBalloons() { return true; }
            @Override public boolean shouldMoveRovers() { return true; }

            @Override public void enter(PiNoonChallenge challenge) {
                challenge.resetRovers();
                setTimer(1000);
                challenge.setMessage("GO!", false);
                challenge.getGameMessage().setInGame(true);
                challenge.getGameMessage().setWaiting(false);

                // TODO
                // challenge.soundManager.playFight();

                challenge.resetRoverBalloons();
            }

            @Override public void update(PiNoonChallenge challenge) {
                super.update(challenge);

                if (isTimerDone()) {
                    challenge.setMessage(null, false);
                }

                Rover player1 = challenge.getPlayerOne();
                Rover player2 = challenge.getPlayerTwo();

                if (player1.getChallengeBits() == 0 || player2.getChallengeBits() == 0) {
                    int player1score = player1.getScore();
                    int player2score = player2.getScore();
                    if (player1score + player2score >= 3) {
                        if (player1score > player2score) {
                            challenge.winner = player1.getAlias();
                        } else if (player1score < player2score) {
                            challenge.winner = player2.getAlias();
                        }
                        challenge.stateMachine.toState(ChallengeState.END, challenge);
                    } else {
                        if (player1.getChallengeBits() == 0) {
                            challenge.winner = player2.getAlias();
                        } else if (player2.getChallengeBits() == 0) {
                            challenge.winner = player1.getAlias();
                        }
                        challenge.stateMachine.toState(ChallengeState.BREAK, challenge);
                        challenge.stopRovers();
                    }
                }
            }
        },

        END() {
            @Override public boolean shouldMoveRovers() { return true; }

            @Override public void enter(PiNoonChallenge challenge) {
                Rover player1 = challenge.getPlayerOne();
                Rover player2 = challenge.getPlayerTwo();

                challenge.setMessage(challenge.winner + " wins! " + player1.getScore() + " - " + player2.getScore(), false);
                challenge.getGameMessage().setInGame(false);
                challenge.getGameMessage().setWaiting(false);
                setTimer(3000);
            }

            @Override public void update(PiNoonChallenge challenge) {
                if (isTimerDone()) {
                    challenge.stateMachine.toState(WAITING_START, challenge);
                }
            }

            @Override public void exit(PiNoonChallenge challenge) {
                Rover player1 = challenge.getPlayerOne();
                Rover player2 = challenge.getPlayerTwo();
                challenge.gccGame.removeGameObject(player2.getId());
                challenge.gccGame.removeGameObject(player1.getId());
                challenge.player1Id = 0;
                challenge.player2Id = 0;
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

        @Override public void enter(PiNoonChallenge challenge) {}
        @Override public void update(PiNoonChallenge challenge) {}
        @Override public void exit(PiNoonChallenge s) {}

        public boolean shouldMoveRovers() { return false; }
        public boolean shouldCollideBalloons() { return false; }
    }
}
