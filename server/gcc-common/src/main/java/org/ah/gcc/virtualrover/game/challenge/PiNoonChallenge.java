package org.ah.gcc.virtualrover.game.challenge;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;

import org.ah.gcc.virtualrover.game.GCCCollidableObject;
import org.ah.gcc.virtualrover.game.GCCGame;
import org.ah.gcc.virtualrover.game.GCCGameTypeObject;
import org.ah.gcc.virtualrover.game.GCCPlayer;
import org.ah.gcc.virtualrover.game.GameMessageObject;
import org.ah.themvsus.engine.common.game.GameObjectWithPosition;
import org.ah.themvsus.engine.common.game.GameState;

import java.util.List;

import static org.ah.gcc.virtualrover.engine.utils.PolygonUtils.polygonFromBox;
import static org.ah.gcc.virtualrover.engine.utils.PolygonUtils.polygonsOverlap;

import static java.util.Arrays.asList;

public class PiNoonChallenge extends AbstractChallenge {

    private List<Polygon> piNoonPolygons = asList(
            polygonFromBox(-1000, -1001,  1000, -1000),
            polygonFromBox(-1001, -1000, -1000,  1000),
            polygonFromBox(-1000,  1000,  1000,  1001),
            polygonFromBox( 1000, -1000,  1001,  1000));

    private int gameMessageId;

    @Override
    public List<Polygon> getCollisionPolygons() {
        return piNoonPolygons;
    }

    @Override
    public void spawnedPlayer(GCCPlayer player) {
        player.setChallengeBits(7);
    }

    @Override
    public boolean checkForCollision(GameObjectWithPosition object, Iterable<GameObjectWithPosition> objects) {
        if (object instanceof GCCCollidableObject) {
            if (polygonsOverlap(getCollisionPolygons(), ((GCCCollidableObject)object).getCollisionPolygons())) {
                return true;
            }
        }

        if (object instanceof GCCPlayer) {
            GCCPlayer player = (GCCPlayer)object;
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
                    if (o != object && o instanceof GCCPlayer) {
                        GCCPlayer otherPlayer = (GCCPlayer)o;

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
    public void process(GCCGame gccGame, GameState newGameState) {
        GameMessageObject gameMessage = getGameMessage(gccGame, newGameState);
        gameMessage.setMessage(Long.toString(System.currentTimeMillis() / 1000));
    }

    private GameMessageObject getGameMessage(GCCGame gccGame, GameState newGameState) {
        GameMessageObject gameMessageObject = null;

        if (gameMessageId != 0) {
            gameMessageObject = (GameMessageObject) newGameState.get(gameMessageId);
        }

        if (gameMessageObject == null) {
            gameMessageObject = (GameMessageObject) gccGame.getGameObjectFactory().newGameObjectWithId(GCCGameTypeObject.GameMessageObject, gccGame.newId());
            gccGame.addNewGameObject(gameMessageObject);
            gameMessageId = gameMessageObject.getId();
        }
        return gameMessageObject;
    }
}
