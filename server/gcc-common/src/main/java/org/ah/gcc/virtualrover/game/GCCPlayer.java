package org.ah.gcc.virtualrover.game;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import org.ah.gcc.virtualrover.game.rovers.RoverType;
import org.ah.gcc.virtualrover.message.GCCPlayerServerUpdateMessage;
import org.ah.themvsus.engine.common.game.Game;
import org.ah.themvsus.engine.common.game.GameObject;
import org.ah.themvsus.engine.common.game.GameObjectFactory;
import org.ah.themvsus.engine.common.game.GameObjectWithPosition;
import org.ah.themvsus.engine.common.game.AbstractPlayer;
import org.ah.themvsus.engine.common.input.PlayerInput;
import org.ah.themvsus.engine.common.message.Message;
import org.ah.themvsus.engine.common.transfer.Serializer;

import java.util.List;

public class GCCPlayer extends AbstractPlayer implements GCCCollidableObject {

    private RoverType roverType;
    private int challengeBits;
    private int score;

    public GCCPlayer(GameObjectFactory factory, int id) {
        super(factory, id);
        setRoverType(RoverType.GCC);
    }

    public void setRoverType(RoverType roverType) {
        this.roverType = roverType;
    }

    public RoverType getRoverType() {
        return roverType;
    }

    public void setChallengeBits(int challengeBits) {
        this.challengeBits = challengeBits;
    }

    public int getChallengeBits() {
        return challengeBits;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getScore() {
        return score;
    }

    @Override
    public void process(Game game, Iterable<GameObjectWithPosition> objects) {
         super.process(game, objects);
    }

    public boolean checkForCollision(GameObjectWithPosition object, Iterable<GameObjectWithPosition> objects) {
        Vector3 objectPosition = object.getPosition();
        float objectCentreX = objectPosition.x;
        float objectCentreY = objectPosition.y;
        for (GameObjectWithPosition o : objects) {
            if (o != object) {
                Vector3 otherPos = o.getPosition();
                float centreX = otherPos.x;
                float centreY = otherPos.y;

                float distancesquared = ((objectCentreX - centreX) * (objectCentreX - centreX)) + ((objectCentreY - centreY) * (objectCentreY - centreY));

                if (distancesquared < (120 * 120)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void processPlayerInputs(PlayerInput playerInput) {
        roverType.getRoverDefinition().getRoverControls().processPlayerInput(this, playerInput);
    }

    @Override
    public void performCommand(Message command) {
        if (command instanceof GCCPlayerServerUpdateMessage) {
            super.performCommand(command);
        } else {
            super.performCommand(command);
        }
    }

    @Override
    public void serialize(boolean full, Serializer serializer) {
        super.serialize(full, serializer);
        serializer.serializeByte((byte)roverType.getId());
        serializer.serializeByte((byte)score);
        serializer.serializeShort(challengeBits);
    }

    @Override
    public void deserialize(boolean full, Serializer serializer) {
        super.deserialize(full, serializer);
        roverType = RoverType.getById(serializer.deserializeByte());
        score = serializer.deserializeByte();
        challengeBits = serializer.deserializeShort();

    }

    @Override
    protected GameObject copyInt(GameObject newObject) {
        super.copyInt(newObject);

        GCCPlayer gccPlayer = (GCCPlayer)newObject;
        gccPlayer.roverType = roverType;
        gccPlayer.score = score;
        gccPlayer.challengeBits = challengeBits;

        return newObject;
    }

    @Override
    public List<Polygon> getCollisionPolygons() {
        return getRoverType().getRoverDefinition().getPolygons(position.x, position.y, getBearing());
    }

    public Vector2 getSharpEnd() {
        return getRoverType().getRoverDefinition().getSharpPoint(position.x, position.y, getBearing());
    }

    public Circle getBalloon(int balloonNo) {
        return getRoverType().getRoverDefinition().getBalloon(balloonNo, position.x, position.y, getBearing());
    }
}
