package org.ah.gcc.virtualrover.game;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;

import org.ah.themvsus.engine.common.game.DependentObject;
import org.ah.themvsus.engine.common.game.GameObject;
import org.ah.themvsus.engine.common.game.GameObjectFactory;
import org.ah.themvsus.engine.common.game.GameObjectType;
import org.ah.themvsus.engine.common.transfer.Serializer;

public class PiNoonAttachment extends DependentObject {

    protected static final float BALLOONS_RADIUS = 35;
    protected static final float SHARP_POINT_LENGTH = 130;

    private int balloonBits;
    private int score;

    protected Vector2 attachmentPosition = new Vector2();
    protected Vector2[] balloons = new Vector2[3];
    protected Circle[] ballonsTempCircle = new Circle[3];

    protected Vector2 temp = new Vector2();

    public PiNoonAttachment(GameObjectFactory factory, int id) {
        super(factory, id);

        balloons[0] = new Vector2(0f, -45f);
        balloons[1] = new Vector2(75f, 0f);
        balloons[2] = new Vector2(0f, 45f);
        for (int i = 0; i < ballonsTempCircle.length; i++) {
            ballonsTempCircle[i] = new Circle(0,  0, BALLOONS_RADIUS);
        }
    }

    @Override
    public void free() {
        balloonBits = 0;
        score = 0;
        super.free();
    }

    public void attachToRover(Rover rover) {
        super.setParentId(rover.getId());
        attachmentPosition.set(rover.getAttachmentPosition());
        rover.addAttachment(getId());
    }

    @Override
    public GameObjectType getType() { return GCCGameTypeObject.PiNoonAttachment; }

    public void setBalloonBits(int challengeBits) {
        this.balloonBits = challengeBits;
        this.changed = true;
    }

    public int getBalloonBits() {
        return balloonBits;
    }

    public void setScore(int score) {
        this.score = score;
        this.changed = true;
    }

    public int getScore() {
        return score;
    }

    @Override
    public void serialize(boolean full, Serializer serializer) {
        super.serialize(full, serializer);
        serializer.serializeByte((byte)score);
        serializer.serializeUnsignedByte(balloonBits);
    }

    @Override
    public void deserialize(boolean full, Serializer serializer) {
        super.deserialize(full, serializer);
        score = serializer.deserializeByte();
        balloonBits = serializer.deserializeUnsignedByte();
    }

    @Override
    public int size(boolean full) {
        return super.size(full) + 2;
    }

    @Override
    protected GameObject copyInt(GameObject newObject) {
        super.copyInt(newObject);

        PiNoonAttachment attachment = (PiNoonAttachment)newObject;
        attachment.score = score;
        attachment.balloonBits = balloonBits;
        attachment.attachmentPosition.x = attachmentPosition.x;
        attachment.attachmentPosition.y = attachmentPosition.y;

        return newObject;
    }

    public Vector2 getSharpEnd() {
        temp.set(attachmentPosition);
        temp.add(SHARP_POINT_LENGTH, 0);
        temp.rotate(getBearing());
        temp.add(position.x, position.y);
        return temp;
    }

    public Circle getBalloon(int balloonNo) {
        temp.set(balloons[balloonNo]);
        temp.add(attachmentPosition);
        temp.rotate(getBearing());
        temp.add(position.x, position.y);
        ballonsTempCircle[balloonNo].setPosition(temp);
        return ballonsTempCircle[balloonNo];
    }
}
