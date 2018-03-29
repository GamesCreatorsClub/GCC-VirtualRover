package org.ah.gcc.display;

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

    public GCCRoverWheel fr;
    public GCCRoverWheel br;
    public GCCRoverWheel bl;

    public GCCRoverWheel fl;


    public long a = 0;

    private Matrix4 transform;
    private ModelInstance top;
    private Color color;
    private List<BoundingBox> world;
    private GCCRoverDisplay main;
    private ModelInstance balloon1;
    private ModelInstance balloon2;
    private ModelInstance balloon3;

    private List<Robot> robots = new ArrayList<Robot>();

    private boolean hasBallon1 = true;
    private boolean hasBallon2 = true;
    private boolean hasBallon3 = true;

    private int id = 0;

    public GCCRover(String name, ModelFactory modelFactory, Color color, GCCRoverDisplay main) {
        super(name);
        this.color = color;
        this.main = main;
        transform = new Matrix4();

        try {
            body = new ModelInstance(modelFactory.getBody(), 0, 0, 0);
            top = new ModelInstance(modelFactory.getTop(), 0, 0, 0);
            top.materials.get(0).set(ColorAttribute.createDiffuse(color));
            body.materials.get(0).set(ColorAttribute.createDiffuse(Color.BLACK));

            pinoon = new ModelInstance(modelFactory.getPiNoon(), 0, 0, 0);
            pinoon.materials.get(0).set(ColorAttribute.createDiffuse(Color.GRAY));

            fr = new GCCRoverWheel(modelFactory, 270, Color.ORANGE);

            fl = new GCCRoverWheel(modelFactory, 90, Color.ORANGE);
            br = new GCCRoverWheel(modelFactory, 270, Color.ORANGE);
            bl = new GCCRoverWheel(modelFactory, 90, Color.ORANGE);

            color.a = 0.7f;
            balloon1 = new ModelInstance(modelFactory.getBaloon(), 0, 0, 0);
            balloon1.materials.get(0).set(ColorAttribute.createDiffuse(color));
            balloon1.materials.get(0).set(new BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA));

            balloon2 = new ModelInstance(modelFactory.getBaloon(), 0, 0, 0);
            balloon2.materials.get(0).set(ColorAttribute.createDiffuse(color));
            balloon2.materials.get(0).set(new BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA));


            balloon3 = new ModelInstance(modelFactory.getBaloon(), 0, 0, 0);
            balloon3.materials.get(0).set(ColorAttribute.createDiffuse(color));
            balloon3.materials.get(0).set(new BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA));
        } catch (Exception e) {
            // TODO: handle exception
        }

    }

    public void setWorldCollision(List<BoundingBox> boxes) {
        this.world = boxes;

    }

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
        a++;
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
        balloon3.transform.translate(-15f, 178f, -36f);

        balloon1.transform.rotate(new Vector3(1, 1, 0), (float) (30 + (Math.sin(a / (Math.random()*4f + 60f)) * 5f)));
        balloon2.transform.rotate(new Vector3(0, 0, 1), (float) (45 + (Math.cos(a / (Math.random()*4f + 60f)) * 5f)));
        balloon3.transform.rotate(new Vector3(1, 1, 0), (float) (270 + (Math.sin(a / (Math.random()*4f + 60f)) * 5f)));

        for (Robot robot : robots) {
            if (getBallon1().contains(robot.sharpPoint())) {
                hasBallon1 = false;
                System.out.println("hit 1");
            }
            if (getBallon2().contains(robot.sharpPoint())) {
                hasBallon2 = false;
                System.out.println("hit 2");

            }
            if (getBallon3().contains(robot.sharpPoint())) {
                hasBallon3 = false;
                System.out.println("hit 3");

            }
        }

//        main.setMarkerPosition(sharpPoint(), 1, false);
//        balloon2.transform.translate(-1, -1, -1).rotate(new Vector3(0, 1, 0), 120).translate(1, 1, 1);


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

        if (hasBallon1) {
            batch.render(balloon1, environment);
        }
        if (hasBallon2) {
            batch.render(balloon2, environment);
        }
        if (hasBallon3) {
            batch.render(balloon3, environment);
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


        float maxX = 250 * GCCRoverDisplay.SCALE;
        float maxZ = 210 * GCCRoverDisplay.SCALE;
        float minX = -215 * GCCRoverDisplay.SCALE;
        float minZ = -255 * GCCRoverDisplay.SCALE;



        if (backleft.x > maxX || backleft.z > maxZ || backleft.x < minX || backleft.z < minZ
                || backright.x > maxX || backright.z > maxZ || backright.x < minX || backright.z < minZ
                || frontleft.x > maxX || frontleft.z > maxZ || frontleft.x < minX || frontleft.z < minZ
                || frontright.x > maxX || frontright.z > maxZ || frontright.x < minX || frontright.z < minZ
                ) {
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


        Polygon poly = new Polygon(new float[] {backleft.x, backleft.z, backright.x, backright.z, frontleft.x, frontleft.z, frontright.x, frontright.z});
        return poly;
    }

    @Override
    public Vector2 sharpPoint() {
        Matrix4 m = new Matrix4(transform);

       m.translate(-21f, 24f, -9f);

        Vector3 pos = new Vector3();
        pos = m.getTranslation(pos);
        main.setMarkerPosition(pos, id, false);
        return new Vector2(pos.x, pos.z);
    }

    public Circle getBallon1() {
        Matrix4 m = new Matrix4(transform);

        m.translate(-2f, 30f, -3f);

        Vector3 pos = new Vector3();
        pos = m.getTranslation(pos);


        float radius = 4  * GCCRoverDisplay.SCALE;

        Circle c = new Circle(pos.x, pos.y, radius);

        return c;
    }



    public Circle getBallon2() {
        Matrix4 m = new Matrix4(transform);

        m.translate(-12f, 30f, -9f);

        Vector3 pos = new Vector3();
        pos = m.getTranslation(pos);


        float radius = 8  * GCCRoverDisplay.SCALE;

        Circle c = new Circle(pos.x, pos.y, radius);

        return c;
    }

    public Circle getBallon3() {
        Matrix4 m = new Matrix4(transform);

        m.translate(2f, 30f, -14f);

        Vector3 pos = new Vector3();
        pos = m.getTranslation(pos);


        float radius =  6 * GCCRoverDisplay.SCALE;

        Circle c = new Circle(pos.x, pos.y, radius);

        return c;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
