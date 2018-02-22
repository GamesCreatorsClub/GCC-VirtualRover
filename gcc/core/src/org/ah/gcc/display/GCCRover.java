package org.ah.gcc.display;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;

public class GCCRover extends Rover {
    public ModelInstance body;
    public ModelInstance pinoon;

    public GCCRoverWheel fr;
    public GCCRoverWheel br;
    public GCCRoverWheel bl;

    public GCCRoverWheel fl;

    private Matrix4 transform;
    private ModelInstance top;
    private Color color;

    public GCCRover(String name, ModelFactory modelFactory, Color color) {
        super(name);
        this.color = color;
        transform = new Matrix4();
        try {
            body = new ModelInstance(modelFactory.getBody(), 0, 0, 0);
            top = new ModelInstance(modelFactory.getTop(), 0, 0, 0);
            top.materials.get(0).set(ColorAttribute.createDiffuse(color));
            body.materials.get(0).set(ColorAttribute.createDiffuse(Color.BLACK));

            pinoon = new ModelInstance(modelFactory.getPiNoon(), 0, 0, 0);
            body.materials.get(0).set(ColorAttribute.createDiffuse(Color.GRAY));


            fr = new GCCRoverWheel(modelFactory, 270, Color.ORANGE);
            fl = new GCCRoverWheel(modelFactory, 90, Color.ORANGE);
            br = new GCCRoverWheel(modelFactory, 270, Color.ORANGE);
            bl = new GCCRoverWheel(modelFactory, 90, Color.ORANGE);
        } catch (Exception e) {
            // TODO: handle exception
        }

    }

    @Override
    public void processInput(Inputs i) {
        if (i.moveUp()) {
            if (i.rotateLeft()) {
                steer(5);
            } else if (i.rotateRight()) {
                steer(-5);
            } else if (i.moveLeft()) {
                drive(1.7f, 45);
            } else if (i.moveRight()) {
                drive(1.7f, 135);
            } else {
                drive(-3, 0);
            }
        } else if (i.moveDown()) {
            if (i.rotateLeft()) {
                steerBack(5);
            } else if (i.rotateRight()) {
                steerBack(-5);
            } else if (i.moveLeft()) {
                drive(-1.7f, 135);
            } else if (i.moveRight()) {
                drive(-1.7f, 45);
            } else {
                drive(3, 0);
            }
        } else if (i.moveLeft()) {
            drive(3, 90);
        } else if (i.moveRight()) {
            drive(-3, 90);
        } else if (i.rotateLeft()) {
            rotate(3);
        } else if (i.rotateRight()) {
            rotate(-3);
        } else {
            stop();
        }

        if (Gdx.input.isKeyPressed(Input.Keys.Z)) {
            straightenWheels();
        } else if (Gdx.input.isKeyPressed(Input.Keys.X)) {
            slantWheels();
        }
    }

    @Override
    public void update() {
        fr.getTransform().set(transform);
        fl.getTransform().set(transform);
        br.getTransform().set(transform);
        bl.getTransform().set(transform);

        pinoon.transform.set(transform);
        top.transform.set(transform);
        body.transform.set(transform);
        fl.getTransform().translate(1f, -5.5f, 0f);
        fr.getTransform().translate(1f, -5.5f, -15f);

        bl.getTransform().translate(24f, -5.5f, 0f);
        br.getTransform().translate(24f, -5.5f, -15f);

        fr.update();
        fl.update();
        br.update();
        bl.update();

        pinoon.transform.scale(0.16f, 0.16f, 0.16f);

        pinoon.transform.translate(-10f, 8f, -66f);
        pinoon.transform.rotate(new Vector3(0, 1, 0), 180);

        top.transform.scale(0.16f, 0.16f, 0.16f);
        body.transform.scale(0.16f, 0.16f, 0.16f);

    }

    public Matrix4 getTransform() {
        return transform;
    }

    public void setTransform(Matrix4 transform) {
        this.transform = transform;
    }

    @Override
    public void render(ModelBatch batch, Environment environment) {
        // update();
        bl.render(batch, environment);
        br.render(batch, environment);
        fl.render(batch, environment);
        fr.render(batch, environment);
        batch.render(pinoon, environment);

        batch.render(top, environment);
        batch.render(body, environment);

    }

    public void straightenWheels() {
        fl.setDegrees(0);
        fr.setDegrees(0);
        bl.setDegrees(0);
        br.setDegrees(0);

    }

    public void slantWheels() {
        fl.setDegrees(-60);
        fr.setDegrees(60);
        bl.setDegrees(+60);
        br.setDegrees(-60);

    }

    public void stop() {
        setWheelSpeeds(0);
    }

    public void drive(int speed) {
        straightenWheels();
        setWheelSpeeds(speed);
        getTransform().translate(new Vector3(-speed, 0, 0));

    }

    public void drive(float speed, int angle) {
        straightenWheels();
        setWheelSpeeds((int) speed);
        if (angle < 0) {
            angle += 360;
        }

        if (angle == 90) {
            fl.setDegrees(90);
            fr.setDegrees(90);
            bl.setDegrees(90);
            br.setDegrees(90);
            getTransform().translate(new Vector3(0, 0, speed));
        } else if (angle == 45) {
            fl.setDegrees(45);
            fr.setDegrees(45);
            bl.setDegrees(45);
            br.setDegrees(45);
            getTransform().translate(new Vector3(speed, 0, speed));

        } else if (angle == 0) {
            fl.setDegrees(0);
            fr.setDegrees(0);
            bl.setDegrees(0);
            br.setDegrees(0);
            getTransform().translate(new Vector3(speed, 0, 0));
        } else if (angle == 135) {
            fl.setDegrees(180 + 135);
            fr.setDegrees(180 + 135);
            bl.setDegrees(180 + 135);
            br.setDegrees(180 + 135);
            getTransform().translate(new Vector3(speed, 0, -speed));

        }

    }

    public void rotate(float angle) {
        float x = -12f;
        float y = 0;
        float z = 8f;
        getTransform().translate(-x, -y, -z).rotate(new Vector3(0, 1, 0), angle).translate(x, y, z);
        slantWheels();
        setWheelSpeeds(3);

    }

    public void steer(int d) {
        setWheelSpeeds(d);

        if (d > 0) {
            fl.setDegrees(45);
            fr.setDegrees(45);
            bl.setDegrees(360 - 45);
            br.setDegrees(360 - 45);
            float x = -12f;
            float y = 0;
            float z = -4f;
            getTransform().translate(-x, -y, -z).rotate(new Vector3(0, 1, 0), d).translate(x, y, z);
        } else {
            fl.setDegrees(360 - 45);
            fr.setDegrees(360 - 45);
            bl.setDegrees(45);
            br.setDegrees(45);
            float x = -12f;
            float y = 0;
            float z = 30f;
            getTransform().translate(-x, -y, -z).rotate(new Vector3(0, 1, 0), d).translate(x, y, z);
        }
    }

    public void steerBack(int d) {
        setWheelSpeeds(d);

        if (d > 0) {
            fl.setDegrees(45);
            fr.setDegrees(45);
            bl.setDegrees(360 - 45);
            br.setDegrees(360 - 45);
            float x = -12f;
            float y = 0;
            float z = -4f;
            getTransform().translate(-x, -y, -z).rotate(new Vector3(0, 1, 0), -d).translate(x, y, z);
        } else {
            fl.setDegrees(360 - 45);
            fr.setDegrees(360 - 45);
            bl.setDegrees(45);
            br.setDegrees(45);
            float x = -12f;
            float y = 0;
            float z = 30f;
            getTransform().translate(-x, -y, -z).rotate(new Vector3(0, 1, 0), -d).translate(x, y, z);
        }
    }



    private double sideAngleFront(double dist) {
        double x = Math.atan2(69.0, dist - 36.5);

        return Math.toDegrees(x);
    }

    private double sideAngleBack(double dist) {
        double x = Math.atan2(69.0, dist + 36.5);

        return Math.toDegrees(x);
    }

    public void setWheelSpeeds(int speed) {
        fl.setSpeed(speed);
        fr.setSpeed(speed);
        bl.setSpeed(speed);
        br.setSpeed(speed);
    }
}
