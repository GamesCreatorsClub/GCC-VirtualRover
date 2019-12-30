package org.ah.gcc.virtualrover.game.rovers;

import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import org.ah.gcc.virtualrover.game.GCCCollidableObject;
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

    private static RoverColour[] ROVER_COLOUR_VALUES = RoverColour.values();

    private RoverType roverType;

    private RoverColour roverColour = RoverColour.WHITE;
    private int attachmentId;
    private int cameraId;

    protected Vector2 attachmentPosition;
    protected Vector3 cameraPosition;
    protected Quaternion cameraOrientation;
    protected float cameraAngle = 45f;

    private RoverControls roverControls;
    protected List<Polygon> polygons;

    protected Vector2 temp = new Vector2();

    public Rover(GameObjectFactory factory, int id, RoverType roverType) {
        super(factory, id);

        this.roverType = roverType;
        this.roverControls = roverType.createRoverControls();
    }

    @Override
    public void free() {
        roverColour = RoverColour.WHITE;
        attachmentId = 0;
        cameraId = 0;
        super.free();
    }

    @Override
    public GameObjectType getType() { return roverType.getGameObjectType(); }

    public Vector2 getAttachmentPosition() {
        return attachmentPosition;
    }

    public Vector3 getCameraPosition() {
        return cameraPosition;
    }

    public Quaternion getCameraOrientation() {
        return cameraOrientation;
    }

    public float getCameraAngle() {
        return cameraAngle;
    }

    public RoverType getRoverType() {
        return roverType;
    }

    public RoverControls getRoverControls() {
        return roverControls;
    }

    public void setRoverColour(RoverColour roverColour) {
        changed = changed || this.roverColour != roverColour;

        this.roverColour = roverColour;
    }

    public RoverColour getRoverColour() {
        return roverColour;
    }

    public int getAttachemntId() {
        return attachmentId;
    }

    public void addAttachment(int attachmentId) {
        this.attachmentId = attachmentId;
    }

    public int getCameraId() {
        return cameraId;
    }

    public void addCamera(int cameraId) {
        this.cameraId = cameraId;
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
        serializer.serializeUnsignedByte(roverColour.ordinal());
    }

    @Override
    public void deserialize(boolean full, Serializer serializer) {
        super.deserialize(full, serializer);
        int roverColourOrdinal = serializer.deserializeUnsignedByte();
        if (roverColourOrdinal < ROVER_COLOUR_VALUES.length) {
            RoverColour roverColour = ROVER_COLOUR_VALUES[roverColourOrdinal];
            setRoverColour(roverColour);
        } else {
            // TODO how to deal with this?
            // System.out.println("Received wrong colour! " + roverColourOrdinal);
        }
    }

    @Override
    public int size(boolean full) {
        return super.size(full) + 1;
    }

    @Override
    protected GameObject copyInt(GameObject newObject) {
        super.copyInt(newObject);

        Rover gccPlayer = (Rover)newObject;
        gccPlayer.roverColour = roverColour;
        gccPlayer.attachmentId = attachmentId;
        gccPlayer.cameraId = cameraId;

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

    @Override
    public String toString() {
        return "Rover" + (changed ? debugChangedText + "*[" : "[") + id + ",(" + position.x + "," + position.y + ")]";
    }
}
