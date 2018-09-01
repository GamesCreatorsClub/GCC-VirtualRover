package org.ah.gcc.display;

import org.ah.gcc.virtualrover.ModelFactory;

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

    private Vector3 position;
    private ModelInstance wheel;
    private ModelCache cache;

    private Matrix4 transform;
    private ModelInstance tyre;

    private float lastAngle = 0;
    private Color color;
    private float scale;


    public BigWheel(ModelFactory f, float degreeOffset, Color color, float scale) {
        this.degreeOffset = degreeOffset;
        this.color = color;
        this.scale = scale;

        position = new Vector3();
        setTransform(new Matrix4());
        try {
            wheel = new ModelInstance(f.getBigWheel(), 0, 0, 0);
            wheel.materials.get(0).set(ColorAttribute.createDiffuse(color));

            tyre = new ModelInstance(f.getBigTyre(), 0, 0, 0);
            tyre.materials.get(0).set(ColorAttribute.createDiffuse(Color.BLACK));

        } catch (Exception e) {

            e.printStackTrace();
        }

        cache = new ModelCache();
        cache.begin();
        cache.add(wheel);
        cache.add(wheel);

        cache.end();

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


        wheel.transform.scale(0.16f, 0.16f, 0.16f);


        wheel.transform.rotate(new Vector3(0f, 1f, 0f), 90f);
        wheel.transform.translate(.5f, 0f, 0);
        wheelangle += speed;
        wheel.transform.rotate(new Vector3(0f, 0f, 1f), wheelangle * -15);


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

    public Vector3 getPosition(Matrix4 move) {
        float x = -1f;
        float y = 0;
        float z = 1.5f;
//        move.translate(-x, -y, -z).rotate(new Vector3(0, 1, 0), degreeOffset + degrees).translate(x, y, z);

        Vector3 pos = new Vector3();
        pos = move.getTranslation(pos);

        return pos;
    }

    public void setPosition(Vector3 position) {
        this.position = position;
    }

    public Matrix4 getTransform() {
        return transform;
    }

    public void setTransform(Matrix4 transform) {
        this.transform = transform;
    }


}
