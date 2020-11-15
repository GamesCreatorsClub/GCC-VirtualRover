package org.ah.piwars.fishtank.game.fish;

import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import org.ah.themvsus.engine.common.game.AbstractPlayer;
import org.ah.themvsus.engine.common.game.Game;
import org.ah.themvsus.engine.common.game.GameObject;
import org.ah.themvsus.engine.common.game.GameObjectFactory;
import org.ah.themvsus.engine.common.game.GameObjectWithPosition;
import org.ah.themvsus.engine.common.input.PlayerInput;
import org.ah.themvsus.engine.common.transfer.Serializer;

public abstract class Fish extends AbstractPlayer {

    protected static final float BALLOONS_RADIUS = 35;
    protected static final float SHARP_POINT_LENGTH = 130;

    private int[] cameraIds = new int[0];
    protected boolean stereoCamera = false;

    protected Vector2 attachmentPosition;
    protected Vector3 cameraPosition;
    protected Quaternion cameraOrientation;
    protected float cameraAngle = 45f;

    protected Vector2 temp = new Vector2();

    public Fish(GameObjectFactory factory, int id) {
        super(factory, id);
    }

    @Override
    public void free() {
        cameraIds = new int[0];
        super.free();
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

    public int[] getCameraId() {
        return cameraIds;
    }

    public void addCamera(int cameraId) {
        int[] newCameraIds = new int[cameraIds.length + 1];
        System.arraycopy(cameraIds, 0, newCameraIds, 0, cameraIds.length);

        newCameraIds[cameraIds.length] = cameraId;
        cameraIds = newCameraIds;
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
    }

    @Override
    public void serialize(boolean full, Serializer serializer) {
        super.serialize(full, serializer);
    }

    @Override
    public void deserialize(boolean full, Serializer serializer) {
        super.deserialize(full, serializer);
    }

    @Override
    public int size(boolean full) {
        return super.size(full);
    }

    @Override
    protected GameObject copyInt(GameObject newObject) {
        super.copyInt(newObject);

        Fish rover = (Fish)newObject;
        rover.cameraIds = cameraIds;

        return newObject;
    }

    @Override
    public String toString() {
        return "Rover" + (changed ? debugChangedText + "*[" : "[") + id + ",(" + position.x + "," + position.y + ")]";
    }
}
