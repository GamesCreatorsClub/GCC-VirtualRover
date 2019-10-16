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
import com.badlogic.gdx.math.collision.BoundingBox;
import org.ah.gcc.virtualrover.*;

public class CBiSRover extends AbstractRover {
    public ModelInstance body;

    private BigWheel fr;
    private BigWheel br;
    private BigWheel bl;
    private BigWheel fl;

    private static float ROVER_SCALE = 26;

    private Matrix4 blm = new Matrix4();
    private Matrix4 frm = new Matrix4();
    private Matrix4 brm = new Matrix4();
    private Matrix4 flm = new Matrix4();

    private Vector3 backleft = new Vector3();
    private Vector3 backright = new Vector3();
    private Vector3 frontleft = new Vector3();
    private Vector3 frontright = new Vector3();

    private Matrix4 nextPos = new Matrix4();

    public CBiSRover(String name, ModelFactory modelFactory, Color colour) throws NoSuchElementException {
        super(name, modelFactory, colour);

        body = new ModelInstance(modelFactory.getcBody(), 0, 0, 0);
        body.materials.get(0).set(ColorAttribute.createDiffuse(colour));

        fr = new BigWheel(modelFactory, 270, Color.YELLOW, ROVER_SCALE);
        fl = new BigWheel(modelFactory, 90, Color.YELLOW, ROVER_SCALE);
        br = new BigWheel(modelFactory, 270, Color.YELLOW, ROVER_SCALE);
        bl = new BigWheel(modelFactory, 90, Color.YELLOW, ROVER_SCALE );
    }

    @Override
    public void processInput(Inputs i, Rover[] rovers) {
        if (i.moveLeft()) {
            testAndMove(rotate(3), rovers);
        } else if (i.moveRight()) {
            testAndMove(rotate(-3), rovers);
        } else if (i.moveUp()) {
            testAndMove(drive(2.7f), rovers);
        } else if (i.moveDown()) {
            testAndMove(drive(-2.7f), rovers);
        } else if (i.rotateLeft()) {
            testAndMove(rotate(3), rovers);
        } else if (i.rotateRight()) {
            testAndMove(rotate(-3), rovers);
        } else {
            stop();
        }
    }

    @Override
    public void update() {
        fr.getTransform().set(transform);
        fl.getTransform().set(transform);
        br.getTransform().set(transform);
        bl.getTransform().set(transform);

        body.transform.set(transform);

        fl.getTransform().translate(42f, -27f, 0f);
        fr.getTransform().translate(20f, -30f, -145f);

        bl.getTransform().translate(180f, -27f, 0f);
        br.getTransform().translate(155f, -27f, -145f);

        fr.update();
        fl.update();
        br.update();
        bl.update();

        body.transform.scale(ROVER_SCALE, ROVER_SCALE, ROVER_SCALE);
        body.transform.translate(7.8f, -1.6f, 0f);

        // body.transform.scale(0.16f, 0.16f, 0.16f);
        body.transform.rotate(new Vector3(0, 1, 0), 90);

        super.update();
    }

    @Override
    public void render(ModelBatch batch, Environment environment, boolean hasBalloons) {
        bl.render(batch, environment);
        br.render(batch, environment);
        fl.render(batch, environment);
        fr.render(batch, environment);

        if (hasBalloons) {
            renderBalloons(batch, environment);
        }
        batch.render(body, environment);
    }

    protected boolean collides(Matrix4 move, Rover[] rovers) {
        blm.set(move);
        frm.set(move);
        brm.set(move);
        flm.set(move);

        backleft = bl.getPosition(blm.translate(160f, -36.7f, 0f), backleft);
        backright = br.getPosition(brm.translate(160f, -36.7f, -100f), backright);
        frontleft = fl.getPosition(flm.translate(6.7f, -36.7f, 0f), frontleft);
        frontright = fr.getPosition(frm.translate(6.7f, -36.7f, -100f), frontright);

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

    private void stop() {
        setWheelSpeeds(0);
    }

    private Matrix4 drive(float f) {
        nextPos.set(transform);

        setWheelSpeeds(f);
        nextPos.translate(new Vector3(-f, 0, 0));

        return nextPos;
    }

    private Matrix4 rotate(float angle) {
        nextPos.set(transform);
        float x = -100f;
        float y = 0;
        float z = 75f;
        nextPos.translate(-x, -y, -z).rotate(new Vector3(0, 1, 0), angle).translate(x, y, z);
        setWheelSpeeds(3);

        return nextPos;
    }

    private void setWheelSpeeds(float f) {
        fl.setSpeed((int) f);
        fr.setSpeed((int) f);
        bl.setSpeed((int) f);
        br.setSpeed((int) f);
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

        return new Polygon(new float[] { backleft.x, backleft.z, backright.x, backright.z, frontleft.x, frontleft.z, frontright.x, frontright.z });
    }

    @Override
    protected void translateSharpPoint(Matrix4 sharpPointMatrix) {
        sharpPointMatrix.translate(-136.7f, 160f, -58.5f);
    }
}
