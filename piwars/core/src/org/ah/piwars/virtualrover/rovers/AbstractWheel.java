package org.ah.piwars.virtualrover.rovers;

import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;

public abstract class AbstractWheel {

    protected float speed = 0;
    protected float wheelangle = 0;

    protected float degrees = 0;
    protected float degreeOffset = 0;

    protected Matrix4 transform = new Matrix4();

    private Vector3 relativePosition = new Vector3();

    protected AbstractWheel(float positionX, float positionY, float positionZ, float degreeeOffset) {
        relativePosition.x = positionX;
        relativePosition.y = positionY;
        relativePosition.z = positionZ;
        this.degreeOffset = degreeeOffset;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public float getDegrees() {
        return degrees;
    }

    public void setDegrees(float degrees) {
        this.degrees = degrees;
    }

    public Vector3 getPosition(Vector3 result) {
        return transform.getTranslation(result);
    }

    public Matrix4 getTransform() {
        return transform;
    }

    public void update() {
        wheelangle += speed;
        transform.translate(relativePosition);
        internalUpdate();
    }

    protected abstract void render(ModelBatch batch, Environment environment);

    protected abstract void internalUpdate();

    public abstract void dispose();
}
