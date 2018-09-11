package org.ah.gcc.virtualrover;

import static org.ah.gcc.virtualrover.MainGame.SCALE;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;

public class GCCRover extends Rover {
    public ModelInstance body;
    public ModelInstance pinoon;
    public ModelInstance marker;

    public GCCRoverWheel fr;
    public GCCRoverWheel br;
    public GCCRoverWheel bl;

    public GCCRoverWheel fl;

    public long balloonPeriod = 0;

    private Matrix4 transform;
    private ModelInstance top;

    private ModelInstance balloon1;
    private ModelInstance balloon2;
    private ModelInstance balloon3;
    private Vector3 ballonPosition1 = new Vector3();
    private Vector3 ballonPosition2 = new Vector3();
    private Vector3 ballonPosition3 = new Vector3();

    private List<Robot> robots = new ArrayList<Robot>();

    private boolean hasBallon1 = true;
    private boolean hasBallon2 = true;
    private boolean hasBallon3 = true;

    private boolean doingPiNoon = false;

    private int id = 0;

    private Matrix4 sharpPointMatrix;
    private Vector2 sharpPoint;
    private Vector3 sharpPointPos;

    public GCCRover(String name, ModelFactory modelFactory, Color colour) {
        super(name, colour);
        transform = new Matrix4();
        sharpPointMatrix = new Matrix4();
        sharpPoint = new Vector2();
        sharpPointPos = new Vector3();

        try {
            body = new ModelInstance(modelFactory.getBody(), 0, 0, 0);
            top = new ModelInstance(modelFactory.getTop(), 0, 0, 0);
            marker = new ModelInstance(modelFactory.getMarker(), 0, 0, 0);

            body.materials.get(0).set(ColorAttribute.createDiffuse(Color.BLACK));

            top.materials.get(0).set(ColorAttribute.createDiffuse(colour));

            marker.transform.scale(0.05f, 0.05f, 0.05f);
            marker.materials.get(0).set(ColorAttribute.createDiffuse(colour));

            pinoon = new ModelInstance(modelFactory.getPiNoon(), 0, 0, 0);
            pinoon.materials.get(0).set(ColorAttribute.createDiffuse(Color.GRAY));

            fr = new GCCRoverWheel(modelFactory, 270, Color.ORANGE);

            fl = new GCCRoverWheel(modelFactory, 90, Color.ORANGE);
            br = new GCCRoverWheel(modelFactory, 270, Color.ORANGE);
            bl = new GCCRoverWheel(modelFactory, 90, Color.ORANGE);

            colour.a = 0.7f;
            balloon1 = new ModelInstance(modelFactory.getBaloon(), 0, 0, 0);
            balloon1.materials.get(0).set(ColorAttribute.createDiffuse(colour));
            balloon1.materials.get(0).set(new BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA));

            balloon2 = new ModelInstance(modelFactory.getBaloon(), 0, 0, 0);
            balloon2.materials.get(0).set(ColorAttribute.createDiffuse(colour));
            balloon2.materials.get(0).set(new BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA));

            balloon3 = new ModelInstance(modelFactory.getBaloon(), 0, 0, 0);
            balloon3.materials.get(0).set(ColorAttribute.createDiffuse(colour));
            balloon3.materials.get(0).set(new BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA));
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    @Override
    public void addOtherRover(Robot robot) {
        robots.add(robot);
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

        if (Gdx.input.isKeyPressed(Input.Keys.Z)) {
            straightenWheels();
        } else if (Gdx.input.isKeyPressed(Input.Keys.X)) {
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

        pinoon.transform.set(transform);
        top.transform.set(transform);
        body.transform.set(transform);

        balloon1.transform.set(transform);
        balloon2.transform.set(transform);
        balloon3.transform.set(transform);

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

        balloon1.transform.scale(0.16f, 0.16f, 0.16f);
        balloon2.transform.scale(0.16f, 0.16f, 0.16f);
        balloon3.transform.scale(0.16f, 0.16f, 0.16f);

        balloon1.transform.translate(0f, 158f, -50f);
        balloon2.transform.translate(-15f, 178f, -64f);
        balloon3.transform.translate(0f, 178f, -70f);

        balloon1.transform.rotate(new Vector3(1, 1, 0), (float) (30 + (Math.sin(balloonPeriod / (Math.random() * 4f + 60f)) * 5f)));
        balloon2.transform.rotate(new Vector3(0, 0, 1), (float) (45 + (Math.cos(balloonPeriod / (Math.random() * 4f + 60f)) * 5f)));
        balloon3.transform.rotate(new Vector3(1, 1, 0), (float) (-50 + (Math.sin(balloonPeriod / (Math.random() * 4f + 60f)) * 5f)));

        for (Robot robot : robots) {
            Vector2 robotSharpPoint = robot.sharpPoint();
            if (getBallon1().contains(robotSharpPoint)) {
                hasBallon1 = false;
//                System.out.println("hit 1");
            }
            if (getBallon2().contains(robotSharpPoint)) {
                hasBallon2 = false;
//                System.out.println("hit 2");
            }
            if (getBallon3().contains(robotSharpPoint)) {
                hasBallon3 = false;
//                System.out.println("hit 3");
            }
        }

        if (MainGame.SHOW_MARKER) {
            marker.transform.setToTranslation(sharpPointPos);
            marker.transform.scale(SCALE, SCALE, SCALE);
        }
    }

    @Override
    public Matrix4 getTransform() {
        return transform;
    }

    @Override
    public void render(ModelBatch batch, Environment environment) {
        // update();
        bl.render(batch, environment);
        br.render(batch, environment);
        fl.render(batch, environment);
        fr.render(batch, environment);

        if (doingPiNoon) {
            batch.render(pinoon, environment);

            if (hasBallon1) {
                batch.render(balloon1, environment);
            }
            if (hasBallon2) {
                batch.render(balloon2, environment);
            }
            if (hasBallon3) {
                batch.render(balloon3, environment);
            }

            if (MainGame.SHOW_MARKER) {
                batch.render(marker, environment);
            }
        }

        batch.render(top, environment);
        batch.render(body, environment);
    }

    public boolean collidesWithRover(Matrix4 move, Robot rover) {
        boolean collision = Intersector.overlapConvexPolygons(getPolygon(move), rover.getPolygon());

        return collision;
    }

    public boolean collides(Matrix4 move) {
        // box.set(t, t);
        Matrix4 blm = new Matrix4(move);
        Matrix4 frm = new Matrix4(move);
        Matrix4 brm = new Matrix4(move);
        Matrix4 flm = new Matrix4(move);

        Vector3 backleft = bl.getPosition(blm.translate(24f, -5.5f, 0f));
        Vector3 backright = br.getPosition(brm.translate(24f, -5.5f, -15f));
        Vector3 frontleft = fl.getPosition(flm.translate(1f, -5.5f, 0f));
        Vector3 frontright = fr.getPosition(frm.translate(1f, -5.5f, -15f));

        float maxX = 250 * MainGame.SCALE;
        float maxZ = 210 * MainGame.SCALE;
        float minX = -215 * MainGame.SCALE;
        float minZ = -255 * MainGame.SCALE;

        if (backleft.x > maxX || backleft.z > maxZ || backleft.x < minX || backleft.z < minZ || backright.x > maxX || backright.z > maxZ || backright.x < minX || backright.z < minZ
                || frontleft.x > maxX || frontleft.z > maxZ || frontleft.x < minX || frontleft.z < minZ || frontright.x > maxX || frontright.z > maxZ || frontright.x < minX
                || frontright.z < minZ) {
            return true;
        }

        for (Robot robot : robots) {
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

    public void checkBalloons(Robot robot) {
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
    public Polygon getPolygon() {
        return getPolygon(transform);
    }

    public Polygon getPolygon(Matrix4 move) {
        Matrix4 blm = new Matrix4(move);
        Matrix4 frm = new Matrix4(move);
        Matrix4 brm = new Matrix4(move);
        Matrix4 flm = new Matrix4(move);

        Vector3 backleft = bl.getPosition(blm.translate(24f, -5.5f, 0f));
        Vector3 backright = br.getPosition(brm.translate(24f, -5.5f, -15f));
        Vector3 frontleft = fl.getPosition(flm.translate(1f, -5.5f, 0f));
        Vector3 frontright = fr.getPosition(frm.translate(1f, -5.5f, -15f));

        Polygon poly = new Polygon(new float[] { backleft.x, backleft.z, backright.x, backright.z, frontleft.x, frontleft.z, frontright.x, frontright.z });
        return poly;
    }

    @Override
    public Vector2 sharpPoint() {
        sharpPointMatrix.set(transform);
        sharpPointMatrix.translate(-21f, 24f, -9f);

        sharpPointPos = sharpPointMatrix.getTranslation(sharpPointPos);

        sharpPoint.set(sharpPointPos.x, sharpPointPos.z);
        return sharpPoint;
    }

    public Circle getBallon1() {
        balloon1.transform.getTranslation(ballonPosition1);

        float radius = 8 * MainGame.SCALE;
        Circle c = new Circle(ballonPosition1.x, ballonPosition1.z, radius);
        return c;
    }

    public Circle getBallon2() {
        balloon2.transform.getTranslation(ballonPosition2);

        float radius = 8 * MainGame.SCALE;

        Circle c = new Circle(ballonPosition2.x, ballonPosition2.z, radius);

        return c;
    }

    public Circle getBallon3() {
        balloon3.transform.getTranslation(ballonPosition3);

        float radius = 8 * MainGame.SCALE;

        Circle c = new Circle(ballonPosition3.x, ballonPosition3.z, radius);

        return c;
    }

    public int getId() {
        return id;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    @Override
    public boolean hasBallon1() {
        return hasBallon1;
    }

    @Override
    public void hasBallon1(boolean hasBallon1) {
        this.hasBallon1 = hasBallon1;
    }

    @Override
    public boolean hasBallon2() {
        return hasBallon2;
    }

    @Override
    public void hasBallon2(boolean hasBallon2) {
        this.hasBallon2 = hasBallon2;
    }

    @Override
    public boolean hasBallon3() {
        return hasBallon3;
    }

    @Override
    public void hasBallon3(boolean hasBallon3) {
        this.hasBallon3 = hasBallon3;
    }

    public boolean isDoingPiNoon() {
        return doingPiNoon;
    }

    @Override
    public void setDoingPiNoon(boolean doingPiNoon) {
        this.doingPiNoon = doingPiNoon;
    }
}
