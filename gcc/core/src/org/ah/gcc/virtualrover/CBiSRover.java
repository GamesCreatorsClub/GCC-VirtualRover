package org.ah.gcc.virtualrover;

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

public class CBiSRover extends AbstractRover {
    public ModelInstance body;

    public BigWheel fr;
    public BigWheel br;
    public BigWheel bl;
    public BigWheel fl;

    private static float ROVER_SCALE = 26;

    private Matrix4 blm = new Matrix4();
    private Matrix4 frm = new Matrix4();
    private Matrix4 brm = new Matrix4();
    private Matrix4 flm = new Matrix4();

    private Vector3 backleft = new Vector3();
    private Vector3 backright = new Vector3();
    private Vector3 frontleft = new Vector3();
    private Vector3 frontright = new Vector3();

    public CBiSRover(String name, ModelFactory modelFactory, Color colour) throws NoSuchElementException {
        super(name, modelFactory, colour);

        body = new ModelInstance(modelFactory.getcBody(), 0, 0, 0);
        body.materials.get(0).set(ColorAttribute.createDiffuse(colour));

        marker.transform.scale(0.05f, 0.05f, 0.05f);

        fr = new BigWheel(modelFactory, 270, Color.YELLOW, ROVER_SCALE);
        fl = new BigWheel(modelFactory, 90, Color.YELLOW, ROVER_SCALE);
        br = new BigWheel(modelFactory, 270, Color.YELLOW, ROVER_SCALE);
        bl = new BigWheel(modelFactory, 90, Color.YELLOW, ROVER_SCALE );
    }

    @Override
    public void processInput(Inputs i) {
        if (i.moveLeft()) {
            rotate(3);
        } else if (i.moveRight()) {
            rotate(-3);
        } else if (i.moveUp()) {
            drive(2.7f);
        } else if (i.moveDown()) {
            drive(-2.7f);
        } else if (i.rotateLeft()) {
            rotate(3);
        } else if (i.rotateRight()) {
            rotate(-3);
        } else {
            stop();
        }
    }

    @Override
    public void update() {
        balloonPeriod++;
        fr.getTransform().set(transform);
        fl.getTransform().set(transform);
        br.getTransform().set(transform);
        bl.getTransform().set(transform);

        body.transform.set(transform);

        fl.getTransform().translate(1f, -5.5f, 0f);
        fr.getTransform().translate(0f, -5.5f, -20f);

        bl.getTransform().translate(27f, -5.5f, 0f);
        br.getTransform().translate(26f, -5.5f, -20f);

        fr.update();
        fl.update();
        br.update();
        bl.update();

        body.transform.scale(ROVER_SCALE, ROVER_SCALE, ROVER_SCALE);
        body.transform.translate(1.2f, -0.3f, 0f);

        body.transform.scale(0.16f, 0.16f, 0.16f);
        body.transform.rotate(new Vector3(0, 1, 0), 90);

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
        batch.render(body, environment);
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

    public void stop() {
        setWheelSpeeds(0);
    }

    public void drive(float f) {
        Matrix4 move = new Matrix4(transform);

        setWheelSpeeds(f);
        move.translate(new Vector3(-f, 0, 0));

        if (!collides(move)) {
            transform.set(move);
        }
    }

    public void rotate(float angle) {
        Matrix4 t = new Matrix4(transform);
        float x = -12f;
        float y = 0;
        float z = 8f;
        t.translate(-x, -y, -z).rotate(new Vector3(0, 1, 0), angle).translate(x, y, z);
        setWheelSpeeds(3);

        transform.set(t);
    }

    public void steer(int d) {
        Matrix4 t = new Matrix4(transform);

        setWheelSpeeds(d);

        if (d > 0) {
            float x = -12f;
            float y = 0;
            float z = -4f;
            t.translate(-x, -y, -z).rotate(new Vector3(0, 1, 0), d).translate(x, y, z);
        } else {
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
            float x = -12f;
            float y = 0;
            float z = -4f;
            t.translate(-x, -y, -z).rotate(new Vector3(0, 1, 0), -d).translate(x, y, z);
        } else {
            float x = -12f;
            float y = 0;
            float z = 30f;
            t.translate(-x, -y, -z).rotate(new Vector3(0, 1, 0), -d).translate(x, y, z);
        }
        if (!collides(t)) {
            transform.set(t);
        }
    }

    public void setWheelSpeeds(float f) {
        fl.setSpeed((int) f);
        fr.setSpeed((int) f);
        bl.setSpeed((int) f);
        br.setSpeed((int) f);
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
