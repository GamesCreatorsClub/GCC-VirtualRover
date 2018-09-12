package org.ah.gcc.virtualrover;

import java.util.NoSuchElementException;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;

public class GCCRover extends AbstractRover {
    public ModelInstance body;

    public GCCRoverWheel fr;
    public GCCRoverWheel br;
    public GCCRoverWheel bl;

    public GCCRoverWheel fl;

    private ModelInstance top;

    private Matrix4 blm = new Matrix4();
    private Matrix4 frm = new Matrix4();
    private Matrix4 brm = new Matrix4();
    private Matrix4 flm = new Matrix4();

    private Vector3 backleft = new Vector3();
    private Vector3 backright = new Vector3();
    private Vector3 frontleft = new Vector3();
    private Vector3 frontright = new Vector3();

    public GCCRover(String name, ModelFactory modelFactory, Color colour) throws NoSuchElementException {
        super(name, modelFactory, colour);

        body = new ModelInstance(modelFactory.getBody(), 0, 0, 0);
        top = new ModelInstance(modelFactory.getTop(), 0, 0, 0);

        body.materials.get(0).set(ColorAttribute.createDiffuse(Color.BLACK));

        top.materials.get(0).set(ColorAttribute.createDiffuse(colour));

        fr = new GCCRoverWheel(modelFactory, 270, Color.ORANGE);

        fl = new GCCRoverWheel(modelFactory, 90, Color.ORANGE);
        br = new GCCRoverWheel(modelFactory, 270, Color.ORANGE);
        bl = new GCCRoverWheel(modelFactory, 90, Color.ORANGE);
    }

    @Override
    public void processInput(Inputs i) {
        if (i.moveUp()) {
            if (i.rotateLeft()) {
                steer(5);
            } else if (i.rotateRight()) {
                steer(-5);
            } else if (i.moveLeft()) {
                drive(-1.7f, 135);
            } else if (i.moveRight()) {
                drive(-1.7f, 45);
            } else {
                drive(-3, 0);
            }
        } else if (i.moveDown()) {
            if (i.rotateLeft()) {
                steerBack(5);
            } else if (i.rotateRight()) {
                steerBack(-5);
            } else if (i.moveLeft()) {
                drive(1.7f, 45);
            } else if (i.moveRight()) {
                drive(1.7f, 135);
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

        if (i.straightenWheels()) {
            straightenWheels();
        } else if (i.slantWheels()) {
            slantWheels();
        }
    }

    @Override
    public void update() {
        balloonPeriod++;
        fr.getTransform().set(transform);
        fl.getTransform().set(transform);
        br.getTransform().set(transform);
        bl.getTransform().set(transform);

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

        top.transform.scale(0.16f, 0.16f, 0.16f);
        body.transform.scale(0.16f, 0.16f, 0.16f);

        updatePiNoon();
        updateBalloons();
        checkForBalloonPopped();
        updateMarkerTransform();
    }

    @Override
    public void render(ModelBatch batch, Environment environment, boolean hasBalloons) {
        // update();
        bl.render(batch, environment);
        br.render(batch, environment);
        fl.render(batch, environment);
        fr.render(batch, environment);

        if (hasBalloons) {
            renderBalloons(batch, environment);
        }

        batch.render(top, environment);
        batch.render(body, environment);
    }

    @Override
    public boolean collidesWithRover(Matrix4 move, Rover rover) {
        boolean collision = Intersector.overlapConvexPolygons(getPolygon(move), rover.getPolygon());

        return collision;
    }

    public boolean collides(Matrix4 move) {
        // box.set(t, t);
        blm.set(move);
        frm.set(move);
        brm.set(move);
        flm.set(move);

        backleft = bl.getPosition(blm.translate(24f, -5.5f, 0f), backleft);
        backright = br.getPosition(brm.translate(24f, -5.5f, -15f), backright);
        frontleft = fl.getPosition(flm.translate(1f, -5.5f, 0f), frontleft);
        frontright = fr.getPosition(frm.translate(1f, -5.5f, -15f), frontright);

        float maxX = 250 * MainGame.SCALE;
        float maxZ = 210 * MainGame.SCALE;
        float minX = -215 * MainGame.SCALE;
        float minZ = -255 * MainGame.SCALE;

        if (backleft.x > maxX || backleft.z > maxZ || backleft.x < minX || backleft.z < minZ || backright.x > maxX || backright.z > maxZ || backright.x < minX || backright.z < minZ
                || frontleft.x > maxX || frontleft.z > maxZ || frontleft.x < minX || frontleft.z < minZ || frontright.x > maxX || frontright.z > maxZ || frontright.x < minX
                || frontright.z < minZ) {
            return true;
        }

        for (Rover robot : robots) {
            if (collidesWithRover(move, robot)) {
                return true;
            }
        }
        return false;
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
        Matrix4 move = new Matrix4(transform);

        straightenWheels();
        setWheelSpeeds(speed);
        move.translate(new Vector3(-speed, 0, 0));

        if (!collides(move)) {
            transform.set(move);
        }
    }

    public void drive(float speed, int angle) {
        Matrix4 t = new Matrix4(transform);

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

            t.translate(new Vector3(0, 0, speed));
        } else if (angle == 45) {
            fl.setDegrees(135);
            fr.setDegrees(135);
            bl.setDegrees(135);
            br.setDegrees(135);
            t.translate(new Vector3(speed, 0, speed));

        } else if (angle == 0) {
            fl.setDegrees(0);
            fr.setDegrees(0);
            bl.setDegrees(0);
            br.setDegrees(0);
            t.translate(new Vector3(speed, 0, 0));
        } else if (angle == 135) {
            fl.setDegrees(180 + 45);
            fr.setDegrees(180 + 45);
            bl.setDegrees(180 + 45);
            br.setDegrees(180 + 45);
            t.translate(new Vector3(speed, 0, -speed));

        }

        if (!collides(t)) {
            transform.set(t);
        } else {

        }
    }

    public void rotate(float angle) {
        Matrix4 t = new Matrix4(transform);
        float x = -12f;
        float y = 0;
        float z = 8f;
        t.translate(-x, -y, -z).rotate(new Vector3(0, 1, 0), angle).translate(x, y, z);
        slantWheels();
        setWheelSpeeds(3);

        transform.set(t);
    }

    public void steer(int d) {
        Matrix4 t = new Matrix4(transform);

        setWheelSpeeds(d);

        if (d > 0) {
            fl.setDegrees(45);
            fr.setDegrees(45);
            bl.setDegrees(360 - 45);
            br.setDegrees(360 - 45);
            float x = -12f;
            float y = 0;
            float z = -4f;
            t.translate(-x, -y, -z).rotate(new Vector3(0, 1, 0), d).translate(x, y, z);
        } else {
            fl.setDegrees(360 - 45);
            fr.setDegrees(360 - 45);
            bl.setDegrees(45);
            br.setDegrees(45);
            float x = -12f;
            float y = 0;
            float z = 30f;
            t.translate(-x, -y, -z).rotate(new Vector3(0, 1, 0), d).translate(x, y, z);
        }
        if (!collides(t)) {
            transform.set(t);
        }
    }

    public void steerBack(int d) {
        Matrix4 t = new Matrix4(transform);

        setWheelSpeeds(d);

        if (d > 0) {
            fl.setDegrees(45);
            fr.setDegrees(45);
            bl.setDegrees(360 - 45);
            br.setDegrees(360 - 45);
            float x = -12f;
            float y = 0;
            float z = -4f;
            t.translate(-x, -y, -z).rotate(new Vector3(0, 1, 0), -d).translate(x, y, z);
        } else {
            fl.setDegrees(360 - 45);
            fr.setDegrees(360 - 45);
            bl.setDegrees(45);
            br.setDegrees(45);
            float x = -12f;
            float y = 0;
            float z = 30f;
            t.translate(-x, -y, -z).rotate(new Vector3(0, 1, 0), -d).translate(x, y, z);
        }
        if (!collides(t)) {
            transform.set(t);
        }
    }

    public void setWheelSpeeds(int speed) {
        fl.setSpeed(speed);
        fr.setSpeed(speed);
        bl.setSpeed(speed);
        br.setSpeed(speed);
    }

    public BoundingBox getBoundingBox() {
        BoundingBox box = new BoundingBox();
        body.calculateBoundingBox(box);
        box.mul(transform);
        return box;
    }

    @Override
    public Polygon getPolygon(Matrix4 move) {
        blm.set(move);
        frm.set(move);
        brm.set(move);
        flm.set(move);

        backleft = bl.getPosition(blm.translate(24f, -5.5f, 0f), backleft);
        backright = br.getPosition(brm.translate(24f, -5.5f, -15f), backright);
        frontleft = fl.getPosition(flm.translate(1f, -5.5f, 0f), frontleft);
        frontright = fr.getPosition(frm.translate(1f, -5.5f, -15f), frontright);

        Polygon poly = new Polygon(new float[] { backleft.x, backleft.z, backright.x, backright.z, frontleft.x, frontleft.z, frontright.x, frontright.z });
        return poly;
    }

    @Override
    protected void translateSharpPoint(Matrix4 sharpPointMatrix) {
        sharpPointMatrix.translate(-21f, 24f, -9f);
    }

}
