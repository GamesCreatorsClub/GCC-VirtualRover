package org.ah.gcc.virtualrover.rovers;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector3;

import org.ah.gcc.virtualrover.ModelFactory;
import org.ah.gcc.virtualrover.input.GCCPlayerInput;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class CBiSRover extends AbstractRover {
    private static float roverSpeed = 0.4f; // metre per second

    private ModelInstance body;

    private BigWheel fr;
    private BigWheel br;
    private BigWheel bl;
    private BigWheel fl;

    private static float ROVER_SCALE = 26;

    private Vector3 backleft = new Vector3();
    private Vector3 backright = new Vector3();
    private Vector3 frontleft = new Vector3();
    private Vector3 frontright = new Vector3();

    private Matrix4 nextPos = new Matrix4();

    private float[] polygonVertices = new float[] { backleft.x, backleft.z, backright.x, backright.z, frontleft.x, frontleft.z, frontright.x, frontright.z };
    private Polygon polygon = new Polygon(polygonVertices);
    private List<Polygon> polygons = new ArrayList<Polygon>();

    public CBiSRover(String name, ModelFactory modelFactory, Color colour) throws NoSuchElementException {
        super(name, colour);

        body = new ModelInstance(modelFactory.getcBody(), 0, 0, 0);
        body.materials.get(0).set(ColorAttribute.createDiffuse(colour));

        fr = new BigWheel(modelFactory, Color.YELLOW, ROVER_SCALE, 20f, -27f, -145f, 270);
        fl = new BigWheel(modelFactory, Color.YELLOW, ROVER_SCALE, 42f, -27f, 0f, 90);
        br = new BigWheel(modelFactory, Color.YELLOW, ROVER_SCALE, 165f, -27f, -145f, 270);
        bl = new BigWheel(modelFactory, Color.YELLOW, ROVER_SCALE, 190f, -27f, 0f, 90);

        polygons.add(polygon);
    }

    @Override
    public void update() {
        body.transform.set(transform);

        body.transform.scale(ROVER_SCALE, ROVER_SCALE, ROVER_SCALE);
        body.transform.translate(7.8f, -1.6f, 0f);
        body.transform.rotate(new Vector3(0, 1, 0), 90);


        fr.getTransform().set(transform);
        fl.getTransform().set(transform);
        br.getTransform().set(transform);
        bl.getTransform().set(transform);

        fr.update();
        fl.update();
        br.update();
        bl.update();

        super.update();
    }

    @Override
    public void render(ModelBatch batch, Environment environment) {
        bl.render(batch, environment);
        br.render(batch, environment);
        fl.render(batch, environment);
        fr.render(batch, environment);

        renderAttachment(batch, environment);

        batch.render(body, environment);
    }

    @Override
    public Matrix4 processInput(GCCPlayerInput i) {
        previousTransform.set(transform);
        float speed = calcSpeedMillimetresInFrame(roverSpeed);
        if (i.moveX() < 0f) {
            return rotate(speed);
        } else if (i.moveX() > 0f) {
            return rotate(-speed);
        } else if (i.moveY() > 0f) {
            return drive(speed * 0.9f);
        } else if (i.moveY() < 0f) {
            return drive(-speed * 0.9f);
        } else if (i.rotateX() < 0f) {
            return rotate(speed);
        } else if (i.rotateX() > 0f) {
            return rotate(-speed);
        } else {
            stop();
        }
        return null;
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
    public List<Polygon> getPolygons() {
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
        return polygons;
    }
}
