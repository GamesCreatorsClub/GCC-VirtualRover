package org.ah.gcc.virtualrover.rovers;

import java.util.NoSuchElementException;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector3;
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

    protected boolean collides(Matrix4 move, Rover[] rovers) {
        blm.set(move);
        frm.set(move);
        brm.set(move);
        flm.set(move);

        backleft = bl.getPosition(blm.translate(160f, -36.7f, 0f), backleft);
        backright = br.getPosition(brm.translate(160f, -36.7f, -160f), backright);
        frontleft = fl.getPosition(flm.translate(6.7f, -36.7f, 0f), frontleft);
        frontright = fr.getPosition(frm.translate(6.7f, -36.7f, -160f), frontright);

        float maxX = 1000 * MainGame.SCALE;
        float maxZ = 1000 * MainGame.SCALE;
        float minX = -1000 * MainGame.SCALE;
        float minZ = -1000 * MainGame.SCALE;

        if (backleft.x > maxX || backleft.z > maxZ || backleft.x < minX || backleft.z < minZ || backright.x > maxX || backright.z > maxZ || backright.x < minX || backright.z < minZ
                || frontleft.x > maxX || frontleft.z > maxZ || frontleft.x < minX || frontleft.z < minZ || frontright.x > maxX || frontright.z > maxZ || frontright.x < minX
                || frontright.z < minZ) {
            return true;
        }

        for (Rover rover : rovers) {
            if (this != rover && collidesWithRover(move, rover)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void processInput(Inputs i, Rover[] rovers) {
        float speed = calcSpeedMillimetresInFrame(roverSpeed);
        if (i.moveUp()) {
            if (i.rotateLeft()) {
                testAndMove(steer(speed * 1.05f), rovers);
            } else if (i.rotateRight()) {
                testAndMove(steer(-speed * 1.05f), rovers);
            } else if (i.moveLeft()) {
                testAndMove(drive(-speed * 0.5f, 135), rovers);
            } else if (i.moveRight()) {
                testAndMove(drive(-speed * 0.5f, 45), rovers);
            } else {
                testAndMove(drive(-speed, 0), rovers);
            }
        } else if (i.moveDown()) {
            if (i.rotateLeft()) {
                testAndMove(steerBack(speed * 1.05f), rovers);
            } else if (i.rotateRight()) {
                testAndMove(steerBack(-speed * 1.05f), rovers);
            } else if (i.moveLeft()) {
                testAndMove(drive(speed * 0.5f, 45), rovers);
            } else if (i.moveRight()) {
                testAndMove(drive(speed * 0.5f, 135), rovers);
            } else {
                testAndMove(drive(speed, 0), rovers);
            }
        } else if (i.moveLeft()) {
            testAndMove(drive(speed, 90), rovers);
        } else if (i.moveRight()) {
            testAndMove(drive(-speed, 90), rovers);
        } else if (i.rotateLeft()) {
            testAndMove(rotate(speed), rovers);
        } else if (i.rotateRight()) {
            testAndMove(rotate(-speed), rovers);
        } else {
            stop();
            update();
        }
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
    public Polygon getPolygon(Matrix4 move) {
        blm.set(move);
        frm.set(move);
        brm.set(move);
        flm.set(move);

        backleft = bl.getPosition(blm.translate(160f, -36.7f, 0f), backleft);
        backright = br.getPosition(brm.translate(160f, -36.7f, -100f), backright);
        frontleft = fl.getPosition(flm.translate(6.7f, -36.7f, 0f), frontleft);
        frontright = fr.getPosition(frm.translate(6.7f, -36.7f, -100f), frontright);

        Polygon poly = new Polygon(new float[] { backleft.x, backleft.z, backright.x, backright.z, frontleft.x, frontleft.z, frontright.x, frontright.z });
        return poly;
    }

    @Override
    protected void translateSharpPoint(Matrix4 sharpPointMatrix) {
        sharpPointMatrix.translate(-136.6f, 160f, -58.5f);
    }
}
