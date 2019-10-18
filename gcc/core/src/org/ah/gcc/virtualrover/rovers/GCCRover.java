package org.ah.gcc.virtualrover.rovers;

import java.util.List;
import java.util.NoSuchElementException;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import org.ah.gcc.virtualrover.*;

public class GCCRover extends AbstractRover {
    private static float roverSpeed = 0.4f; // metre per second

    private ModelInstance body;

    private GCCRoverWheel fr;
    private GCCRoverWheel br;
    private GCCRoverWheel bl;
    private GCCRoverWheel fl;

    private ModelInstance top;

    private Matrix4 blm = new Matrix4();
    private Matrix4 frm = new Matrix4();
    private Matrix4 brm = new Matrix4();
    private Matrix4 flm = new Matrix4();

    private Vector3 backleft = new Vector3();
    private Vector3 backright = new Vector3();
    private Vector3 frontleft = new Vector3();
    private Vector3 frontright = new Vector3();

    private Matrix4 nextPos = new Matrix4();

    private float[] polygonVertices = new float[] { 0, 0, 0, 0, 0, 0, 0, 0 };
    private Polygon polygon = new Polygon(polygonVertices);

    public GCCRover(String name, ModelFactory modelFactory, Color colour) throws NoSuchElementException {
        super(name, modelFactory, colour);

        body = new ModelInstance(modelFactory.getBody(), 0, 0, 0);
        top = new ModelInstance(modelFactory.getTop(), 0, 0, 0);

        body.materials.get(0).set(ColorAttribute.createDiffuse(Color.BLACK));

        top.materials.get(0).set(ColorAttribute.createDiffuse(colour));

        fr = new GCCRoverWheel(modelFactory, Color.ORANGE, 10f, -36.7f, -100f, 270);
        fl = new GCCRoverWheel(modelFactory, Color.ORANGE, 25f, -36.7f, -10f, 90);
        br = new GCCRoverWheel(modelFactory, Color.ORANGE, 132f, -36.7f, -100f, 270);
        bl = new GCCRoverWheel(modelFactory, Color.ORANGE, 150f, -36.7f, -10f, 90);
    }

    @Override
    public void update() {
        fr.getTransform().set(transform);
        fl.getTransform().set(transform);
        br.getTransform().set(transform);
        bl.getTransform().set(transform);

        top.transform.set(transform);
        body.transform.set(transform);

        fr.update();
        fl.update();
        br.update();
        bl.update();

        super.update();
    }

    @Override
    public void render(ModelBatch batch, Environment environment, boolean hasBalloons) {
        // update();
        bl.render(batch, environment);
        br.render(batch, environment);
        fl.render(batch, environment);
        fr.render(batch, environment);

        batch.render(top, environment);
        batch.render(body, environment);

        if (hasBalloons) {
            renderBalloons(batch, environment);
        }
    }

    @Override
    public Matrix4 processInput(Inputs i) {
        previousTransform.set(transform);
        float speed = calcSpeedMillimetresInFrame(roverSpeed);
        if (i.moveUp()) {
            if (i.rotateLeft()) {
                return steer(speed * 1.05f);
            } else if (i.rotateRight()) {
                return steer(-speed * 1.05f);
            } else if (i.moveLeft()) {
                return drive(-speed * 0.5f, 135);
            } else if (i.moveRight()) {
                return drive(-speed * 0.5f, 45);
            } else {
                return drive(-speed, 0);
            }
        } else if (i.moveDown()) {
            if (i.rotateLeft()) {
                return steerBack(speed * 1.05f);
            } else if (i.rotateRight()) {
                return steerBack(-speed * 1.05f);
            } else if (i.moveLeft()) {
                return drive(speed * 0.5f, 45);
            } else if (i.moveRight()) {
                return drive(speed * 0.5f, 135);
            } else {
                return drive(speed, 0);
            }
        } else if (i.moveLeft()) {
            return drive(speed, 90);
        } else if (i.moveRight()) {
            return drive(-speed, 90);
        } else if (i.rotateLeft()) {
            return rotate(speed);
        } else if (i.rotateRight()) {
            return rotate(-speed);
        } else {
            stop();
        }
        return null;
    }

    private void straightenWheels() {
        fl.setDegrees(0);
        fr.setDegrees(0);
        bl.setDegrees(0);
        br.setDegrees(0);
    }

    private void slantWheels() {
        fl.setDegrees(-60);
        fr.setDegrees(60);
        bl.setDegrees(+60);
        br.setDegrees(-60);
    }

    private void stop() {
        setWheelSpeeds(0);
    }

    private Matrix4 drive(float speed, int angle) {
        nextPos.set(transform);

        straightenWheels();
        setWheelSpeeds(speed);
        if (angle < 0) {
            angle += 360;
        }

        if (angle == 90) {
            fl.setDegrees(90);
            fr.setDegrees(90);
            bl.setDegrees(90);
            br.setDegrees(90);

            nextPos.translate(new Vector3(0, 0, speed));
        } else if (angle == 45) {
            fl.setDegrees(135);
            fr.setDegrees(135);
            bl.setDegrees(135);
            br.setDegrees(135);

            nextPos.translate(new Vector3(speed, 0, speed));
        } else if (angle == 0) {
            fl.setDegrees(0);
            fr.setDegrees(0);
            bl.setDegrees(0);
            br.setDegrees(0);

            nextPos.translate(new Vector3(speed, 0, 0));
        } else if (angle == 135) {
            fl.setDegrees(180 + 45);
            fr.setDegrees(180 + 45);
            bl.setDegrees(180 + 45);
            br.setDegrees(180 + 45);

            nextPos.translate(new Vector3(speed, 0, -speed));
        }

        return nextPos;
    }

    private Matrix4 rotate(float angle) {
        nextPos.set(transform);
        float x = -80f;
        float y = 0;
        float z = 60f;
        nextPos.translate(-x, -y, -z).rotate(new Vector3(0, 1, 0), angle).translate(x, y, z);
        slantWheels();
        setWheelSpeeds(3);

        return nextPos;
    }

    private Matrix4 steer(float d) {
        nextPos.set(transform);

        setWheelSpeeds(d);

        if (d > 0) {
            fl.setDegrees(45);
            fr.setDegrees(45);
            bl.setDegrees(360 - 45);
            br.setDegrees(360 - 45);
            float x = -12f;
            float y = 0;
            float z = -4f;
            nextPos.translate(-x, -y, -z).rotate(new Vector3(0, 1, 0), d).translate(x, y, z);
        } else {
            fl.setDegrees(360 - 45);
            fr.setDegrees(360 - 45);
            bl.setDegrees(45);
            br.setDegrees(45);
            float x = -12f;
            float y = 0;
            float z = 30f;
            nextPos.translate(-x, -y, -z).rotate(new Vector3(0, 1, 0), d).translate(x, y, z);
        }
        return nextPos;
    }

    private Matrix4 steerBack(float d) {
        nextPos.set(transform);

        setWheelSpeeds(d);

        if (d > 0) {
            fl.setDegrees(45);
            fr.setDegrees(45);
            bl.setDegrees(360 - 45);
            br.setDegrees(360 - 45);
            float x = -12f;
            float y = 0;
            float z = -4f;
            nextPos.translate(-x, -y, -z).rotate(new Vector3(0, 1, 0), -d).translate(x, y, z);
        } else {
            fl.setDegrees(360 - 45);
            fr.setDegrees(360 - 45);
            bl.setDegrees(45);
            br.setDegrees(45);
            float x = -12f;
            float y = 0;
            float z = 30f;
            nextPos.translate(-x, -y, -z).rotate(new Vector3(0, 1, 0), -d).translate(x, y, z);
        }

        return nextPos;
    }

    private void setWheelSpeeds(float speed) {
        fl.setSpeed(speed);
        fr.setSpeed(speed);
        bl.setSpeed(speed);
        br.setSpeed(speed);
    }

    @Override
    public Polygon getPolygon() {
        backleft = bl.getPosition(backleft);
        backright = br.getPosition(backright);
        frontleft = fl.getPosition(frontleft);
        frontright = fr.getPosition(frontright);

        polygonVertices[0] = frontright.x;
        polygonVertices[1] = frontright.z;
        polygonVertices[2] = frontleft.x;
        polygonVertices[3] = frontleft.z;
        polygonVertices[4] = backleft.x;
        polygonVertices[5] = backleft.z;
        polygonVertices[6] = backright.x;
        polygonVertices[7] = backright.z;

        polygon.dirty();
        return polygon;
    }

    @Override
    protected void translateSharpPoint(Matrix4 sharpPointMatrix) {
        sharpPointMatrix.translate(-136.6f, 160f, -58.5f);
    }
}
