package org.ah.gcc.display;

import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelCache;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;

public class GCCRoverWheel {
    private int speed = 0;
    private float degrees = 0;

    private Vector3 position;
    private ModelInstance wheel;
    private ModelInstance motor;
    private ModelCache cache;

    private Matrix4 transform;

    public GCCRoverWheel(int speed, int degrees, ModelFactory f) {
        this.speed = speed;
        this.degrees = degrees;
        position = new Vector3();
        transform = new Matrix4();
        try {
            motor = new ModelInstance(f.getMotorHolder(), 0, 0, 0);
            wheel = new ModelInstance(f.getWheel(), -0.5f, 0.7f, -1.5f);
            motor.transform.scale(4f, 4f, 4f);
            wheel.transform.scale(0.16f, 0.16f, 0.16f);
            wheel.transform.rotate(new Vector3(0f, 0f, 1f), -90f);
        } catch (Exception e) {
            e.printStackTrace();
        }

        cache = new ModelCache();
        cache.begin();
        cache.add(motor);
        cache.add(wheel);
        cache.end();

    }

    public void updatePosition (Vector3 pos) {
        position = pos;
//        motor.transform.translate(pos);
//        Vector3 vector3 = new Vector3(-0.5f, 0.7f, -1.5f);
//        vector3 = vector3.rotate(new Vector3(0f, 0f, 1f), degrees);
//        wheel.transform.translate(pos.add(vector3));
        wheel.transform.rotate(new Vector3(0f, 0f, 1f), degrees);
        motor.transform.rotate(new Vector3(0f, 0f, 1f), degrees);
//        degrees += 1;

    }

    public void render(ModelBatch batch, Environment environment) {
        wheel.transform.rotate(new Vector3(0f, 1f, 0f), speed);
        batch.render(wheel, environment);

        batch.render(motor, environment);
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
        return position;
    }

    public void setPosition(Vector3 position) {
        this.position = position;
    }
}
