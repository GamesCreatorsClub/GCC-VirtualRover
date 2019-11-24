package org.ah.gcc.virtualrover.game;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import org.ah.gcc.virtualrover.game.rovers.RoverControls;
import org.ah.gcc.virtualrover.game.rovers.RoverType;
import org.ah.themvsus.engine.common.game.AbstractPlayer;
import org.ah.themvsus.engine.common.game.Game;
import org.ah.themvsus.engine.common.game.GameObject;
import org.ah.themvsus.engine.common.game.GameObjectFactory;
import org.ah.themvsus.engine.common.game.GameObjectType;
import org.ah.themvsus.engine.common.game.GameObjectWithPosition;
import org.ah.themvsus.engine.common.input.PlayerInput;
import org.ah.themvsus.engine.common.transfer.Serializer;

import java.util.List;

public abstract class Rover extends AbstractPlayer implements GCCCollidableObject {

    protected static final float BALLOONS_RADIUS = 35;
    protected static final float SHARP_POINT_LENGTH = 130;

    public enum RoverColour {
        WHITE,
        GREEN,
        BLUE
    }

    private RoverType roverType;

    private int challengeBits;
    private int score;
    // TODO add full_change as well so not all, normally not changing attributes like rover_type are sent across every time
    private RoverColour roverColour = RoverColour.WHITE;

    private RoverControls roverControls;
    protected List<Polygon> polygons;
    protected Vector2 attachmentPosition;
    protected Vector2[] balloons = new Vector2[3];
    protected Circle[] ballonsTempCircle = new Circle[3];

    protected Vector2 temp = new Vector2();

    public Rover(GameObjectFactory factory, int id, RoverType roverType) {
        super(factory, id);

        this.roverType = roverType;
        this.roverControls = roverType.createRoverControls();

        balloons[0] = new Vector2(0f, -45f);
        balloons[1] = new Vector2(75f, 0f);
        balloons[2] = new Vector2(0f, 45f);
        for (int i = 0; i < ballonsTempCircle.length; i++) {
            ballonsTempCircle[i] = new Circle(0,  0, BALLOONS_RADIUS);
        }
    }

    @Override
    public void free() {
        challengeBits = 0;
        score = 0;
        roverColour = RoverColour.WHITE;
        super.free();
    }

    @Override
    public GameObjectType getType() { return roverType.getGameObjectType(); }

    public RoverType getRoverType() {
        return roverType;
    }

    public RoverControls getRoverControls() {
        return roverControls;
    }

    public void setChallengeBits(int challengeBits) {
        this.challengeBits = challengeBits;
        this.changed = true;
    }

    public int getChallengeBits() {
        return challengeBits;
    }

    public void setScore(int score) {
        this.score = score;
        this.changed = true;
    }

    public int getScore() {
        return score;
    }

    public void setRoverColour(RoverColour roverColour) {
        this.roverColour = roverColour;
        this.changed = true;
    }

    public RoverColour getRoverColour() {
        return roverColour;
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
        roverControls.processPlayerInput(this, playerInput);
    }

    @Override
    public void serialize(boolean full, Serializer serializer) {
        super.serialize(full, serializer);
        serializer.serializeByte((byte)score);
        serializer.serializeUnsignedShort(challengeBits);
        serializer.serializeUnsignedByte((byte)roverColour.ordinal());
    }

    @Override
    public void deserialize(boolean full, Serializer serializer) {
        super.deserialize(full, serializer);
        score = serializer.deserializeByte();
        challengeBits = serializer.deserializeUnsignedShort();
        roverColour = RoverColour.values()[serializer.deserializeUnsignedByte()];
    }

    @Override
    public int size(boolean full) {
        return super.size(full) + 4;
    }

    @Override
    protected GameObject copyInt(GameObject newObject) {
        super.copyInt(newObject);

        Rover gccPlayer = (Rover)newObject;
        gccPlayer.score = score;
        gccPlayer.challengeBits = challengeBits;
        gccPlayer.roverColour = roverColour;

        return newObject;
    }

    @Override
    public List<Polygon> getCollisionPolygons() {

        for (Polygon p : polygons) {
            p.setPosition(position.x, position.y);
            p.setRotation(getBearing());
        }
        return polygons;

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
