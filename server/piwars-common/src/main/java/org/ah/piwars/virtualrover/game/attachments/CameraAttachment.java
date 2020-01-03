package org.ah.piwars.virtualrover.game.attachments;

import org.ah.piwars.virtualrover.game.PiWarsGameTypeObject;
import org.ah.piwars.virtualrover.game.rovers.Rover;
import org.ah.themvsus.engine.common.game.DependentObject;
import org.ah.themvsus.engine.common.game.GameObject;
import org.ah.themvsus.engine.common.game.GameObjectFactory;
import org.ah.themvsus.engine.common.game.GameObjectType;
import org.ah.themvsus.engine.common.game.GameObjectWithPositionAndOrientation;
import org.ah.themvsus.engine.common.transfer.Serializer;

public class CameraAttachment extends GameObjectWithPositionAndOrientation implements DependentObject {

    private int parentId;

    protected float cameraAngle = 45f;

    public CameraAttachment(GameObjectFactory factory, int id) {
        super(factory, id);
    }

    @Override
    public void free() {
        parentId = 0;
        super.free();
    }

    @Override
    public int getParentId() {
        return parentId;
    }

    @Override
    public void setParentId(int parentId) {
        changed = changed || this.parentId != parentId;

        this.parentId = parentId;
    }

    public float getCameraAngle() {
        return cameraAngle;
    }

    public void setCameraAngle(float cameraAngle) {
        changed = changed || this.cameraAngle != cameraAngle;

        this.cameraAngle = cameraAngle;
    }

    public void attachToRover(Rover rover) {
        setParentId(rover.getId());
        position.set(rover.getCameraPosition());
        orientation.set(rover.getCameraOrientation());
        cameraAngle = rover.getCameraAngle();
        rover.addCamera(getId());
    }

    @Override
    public GameObjectType getType() { return PiWarsGameTypeObject.CameraAttachment; }

    @Override
    public void serialize(boolean full, Serializer serializer) {
        super.serialize(full, serializer);
        serializer.serializeUnsignedShort(parentId);
        serializer.serializeFloat(cameraAngle);
    }

    @Override
    public void deserialize(boolean full, Serializer serializer) {
        super.deserialize(full, serializer);
        setParentId(serializer.deserializeUnsignedShort());
        setCameraAngle(serializer.deserializeFloat());
    }

    @Override
    public int size(boolean full) {
        return super.size(full) + 2 + 4;
    }

    @Override
    protected GameObject copyInt(GameObject newObject) {
        super.copyInt(newObject);

        CameraAttachment attachment = (CameraAttachment)newObject;
        attachment.parentId = parentId;

        return newObject;
    }

    @Override
    public String toString() {
        return "CameraAttachemnt" + (changed ? "*[" : "[") + id + " of " + getParentId() + "]";
    }
}
