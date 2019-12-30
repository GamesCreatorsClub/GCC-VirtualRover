package org.ah.gcc.virtualrover.game.attachments;

import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;

import org.ah.gcc.virtualrover.game.GCCGameTypeObject;
import org.ah.gcc.virtualrover.game.rovers.Rover;
import org.ah.themvsus.engine.common.game.DependentObject;
import org.ah.themvsus.engine.common.game.GameObject;
import org.ah.themvsus.engine.common.game.GameObjectFactory;
import org.ah.themvsus.engine.common.game.GameObjectType;
import org.ah.themvsus.engine.common.game.GameObjectWithPositionAndOrientation;
import org.ah.themvsus.engine.common.transfer.Serializer;

public class CameraAttachment extends GameObjectWithPositionAndOrientation implements DependentObject {

    private int parentId;

    protected Vector3 cameraPosition = new Vector3();
    protected Quaternion cameraOrientation = new Quaternion();
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

    public void attachToRover(Rover rover) {
        setParentId(rover.getId());
        cameraPosition.set(rover.getCameraPosition());
        cameraOrientation.set(rover.getCameraOrientation());
        cameraAngle = rover.getCameraAngle();
        rover.addCamera(getId());
    }

    @Override
    public GameObjectType getType() { return GCCGameTypeObject.PiNoonAttachment; }

    @Override
    public void serialize(boolean full, Serializer serializer) {
        super.serialize(full, serializer);
        serializer.serializeUnsignedShort(parentId);
    }

    @Override
    public void deserialize(boolean full, Serializer serializer) {
        super.deserialize(full, serializer);
        parentId = serializer.deserializeUnsignedShort();
    }

    @Override
    public int size(boolean full) {
        return super.size(full) + 2;
    }

    @Override
    protected GameObject copyInt(GameObject newObject) {
        super.copyInt(newObject);

        CameraAttachment attachment = (CameraAttachment)newObject;
        attachment.parentId = parentId;
        attachment.cameraPosition.x = cameraPosition.x;
        attachment.cameraPosition.y = cameraPosition.y;
        attachment.cameraPosition.z = cameraPosition.z;

        attachment.cameraOrientation.x = cameraOrientation.x;
        attachment.cameraOrientation.y = cameraOrientation.y;
        attachment.cameraOrientation.z = cameraOrientation.z;
        attachment.cameraOrientation.w = cameraOrientation.w;

        return newObject;
    }

    @Override
    public String toString() {
        return "CameraAttachemnt" + (changed ? "*[" : "[") + id + " of " + getParentId() + "]";
    }
}
