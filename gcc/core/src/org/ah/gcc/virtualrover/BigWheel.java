package org.ah.gcc.virtualrover;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelCache;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;

public class BigWheel {
    private int speed = 0;
    private float wheelangle = 0;
    private float degrees = 0;
    private float degreeOffset = 0;

    // private Vector3 position = new Vector3();
    private ModelInstance wheel;
    private ModelCache cache = new ModelCache();

    private Matrix4 transform = new Matrix4();
    private ModelInstance tyre;

    private Color colour;
    private float scale;

    private static final Vector3 WHEEL_ORIENTATION_AXIS = new Vector3(0f, 1f, 0f);
    private static final Vector3 WHEEL_ROTATION_AXIS = new Vector3(0f, 0f, 1f);

    public BigWheel(ModelFactory f, float degreeOffset, Color colour, float scale) {
        this.degreeOffset = degreeOffset;
        this.colour = colour;
        this.scale = scale;

        wheel = new ModelInstance(f.getBigWheel(), 0, 0, 0);
        wheel.materials.get(0).set(ColorAttribute.createDiffuse(colour));

        tyre = new ModelInstance(f.getBigTyre(), 0, 0, 0);
        tyre.materials.get(0).set(ColorAttribute.createDiffuse(Color.BLACK));

        cache.begin();
        cache.add(wheel);
        cache.add(wheel);

        cache.end();
    }

    public Color getColour() {
        return colour;
    }

    public void update() {
        update(getTransform());
    }

    public void update(Matrix4 transform) {
        float x = -1f;
        float y = -1f;
        float z = 1.5f;
        transform.translate(-x, -y, -z).rotate(new Vector3(0, 1, 0), degreeOffset + degrees).translate(x, y, z);

        wheel.transform.set(transform);

        wheel.transform.scale(scale, scale, scale);

        // wheel.transform.scale(0.16f, 0.16f, 0.16f);
        wheel.transform.rotate(WHEEL_ORIENTATION_AXIS, 90f);
        wheel.transform.translate(.5f, 0f, 0);
        wheelangle += speed;
        wheel.transform.rotate(WHEEL_ROTATION_AXIS, wheelangle * -15);

        tyre.transform.set(wheel.transform);
    }

    public void render(ModelBatch batch, Environment environment) {
        batch.render(wheel, environment);
        batch.render(tyre, environment);

    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public float getDegrees() {
        return degrees;
    }

    public void setDegrees(float degrees) {
        this.degrees = degrees;
    }

    public Vector3 getPosition() {
        Vector3 pos = new Vector3();
        pos = transform.getTranslation(pos);
        return pos;
    }

    public Vector3 getPosition(Matrix4 move, Vector3 result) {
//        float x = -1f;
//        float y = 0;
//        float z = 1.5f;
//        move.translate(-x, -y, -z).rotate(new Vector3(0, 1, 0), degreeOffset + degrees).translate(x, y, z);

        return move.getTranslation(result);
    }

    public Matrix4 getTransform() {
        return transform;
    }
}
