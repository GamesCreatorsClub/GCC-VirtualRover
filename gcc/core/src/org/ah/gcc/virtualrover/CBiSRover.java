package org.ah.gcc.virtualrover;

import java.util.ArrayList;
import java.util.List;

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

public class CBiSRover extends Rover {
    public ModelInstance body;
    public ModelInstance pinoon;

    public BigWheel fr;
    public BigWheel br;
    public BigWheel bl;
    public BigWheel fl;

    public long a = 0;

    private Matrix4 transform;
    private Color color;
    private List<BoundingBox> world;
    private MainGame main;
    private ModelInstance balloon1;
    private ModelInstance balloon2;
    private ModelInstance balloon3;
    private Vector3 ballonPosition1 = new Vector3();
    private Vector3 ballonPosition2 = new Vector3();
    private Vector3 ballonPosition3 = new Vector3();
    private Vector3 roverPosition1 = new Vector3();
    private Vector3 roverPosition2 = new Vector3();

    private List<Robot> robots = new ArrayList<Robot>();

    private boolean hasBallon1 = true;
    private boolean hasBallon2 = true;
    private boolean hasBallon3 = true;

    private boolean doingPiNoon = false;

    private int id = 0;
    private float scale = 26;

    public CBiSRover(String name, ModelFactory modelFactory, Color color, MainGame main) {
        super(name);
        this.color = color;
        this.main = main;
        transform = new Matrix4();

        try {
            body = new ModelInstance(modelFactory.getcBody(), 0, 0, 0);
            body.materials.get(0).set(ColorAttribute.createDiffuse(color));

            pinoon = new ModelInstance(modelFactory.getPiNoon(), 0, 0, 0);
            pinoon.materials.get(0).set(ColorAttribute.createDiffuse(Color.GRAY));

            fr = new BigWheel(modelFactory, 270, Color.YELLOW, scale);
            fl = new BigWheel(modelFactory, 90, Color.YELLOW, scale);
            br = new BigWheel(modelFactory, 270, Color.YELLOW, scale);
            bl = new BigWheel(modelFactory, 90, Color.YELLOW, scale );

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
        if (i.moveLeft()) {
            rotate(3);
        } else if (i.moveRight()) {
            rotate(-3);
        } else if (i.moveUp()) {
            drive(2.7f);
        } else if (i.moveDown()) {
            drive(-2.7f);

        } else if (i.rotateLeft()) {
        } else if (i.rotateRight()) {
        } else {
            stop();
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
        body.transform.set(transform);

        balloon1.transform.set(transform);
        balloon2.transform.set(transform);
        balloon3.transform.set(transform);

        fl.getTransform().translate(1f, -5.5f, 0f);
        fr.getTransform().translate(0f, -5.5f, -20f);

        bl.getTransform().translate(27f, -5.5f, 0f);
        br.getTransform().translate(26f, -5.5f, -20f);

        fr.update();
        fl.update();
        br.update();
        bl.update();

        pinoon.transform.scale(0.16f, 0.16f, 0.16f);

        pinoon.transform.translate(-10f, 8f, -66f);
        pinoon.transform.rotate(new Vector3(0, 1, 0), 180);


        body.transform.scale(scale, scale, scale);
        body.transform.translate(1.2f, -0.3f, 0f);


        body.transform.scale(0.16f, 0.16f, 0.16f);
        body.transform.rotate(new Vector3(0, 1, 0), 90);

        balloon1.transform.scale(0.16f, 0.16f, 0.16f);
        balloon2.transform.scale(0.16f, 0.16f, 0.16f);
        balloon3.transform.scale(0.16f, 0.16f, 0.16f);

        balloon1.transform.translate(0f, 158f, -50f);
        balloon2.transform.translate(-15f, 178f, -64f);
        balloon3.transform.translate(0f, 178f, -70f);

        balloon1.transform.rotate(new Vector3(1, 1, 0), (float) (30 + (Math.sin(a / (Math.random() * 4f + 60f)) * 5f)));
        balloon2.transform.rotate(new Vector3(0, 0, 1), (float) (45 + (Math.cos(a / (Math.random() * 4f + 60f)) * 5f)));
        balloon3.transform.rotate(new Vector3(1, 1, 0), (float) (-50 + (Math.sin(a / (Math.random() * 4f + 60f)) * 5f)));

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

        // main.setMarkerPosition(sharpPoint(), 1, false);
        // balloon2.transform.translate(-1, -1, -1).rotate(new Vector3(0, 1, 0), 120).translate(1, 1, 1);

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
        }
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


    public void checkBalloons(Robot robot) {

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

    private double sideAngleFront(double dist) {
        double x = Math.atan2(69.0, dist - 36.5);

        return Math.toDegrees(x);
    }

    private double sideAngleBack(double dist) {
        double x = Math.atan2(69.0, dist + 36.5);

        return Math.toDegrees(x);
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
        Matrix4 m = new Matrix4(transform);

        m.translate(-21f, 24f, -9f);

        Vector3 pos = new Vector3();
        pos = m.getTranslation(pos);
        main.setMarkerPosition(pos, id, false);

        return new Vector2(pos.x, pos.z);
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

        Circle c = new Circle(ballonPosition3.x, ballonPosition3.y, radius);

        return c;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean hasBallon1() {
        return hasBallon1;
    }

    public void hasBallon1(boolean hasBallon1) {
        this.hasBallon1 = hasBallon1;
    }

    public boolean hasBallon2() {
        return hasBallon2;
    }

    public void hasBallon2(boolean hasBallon2) {
        this.hasBallon2 = hasBallon2;
    }

    public boolean hasBallon3() {
        return hasBallon3;
    }

    public void hasBallon3(boolean hasBallon3) {
        this.hasBallon3 = hasBallon3;
    }

    public boolean isDoingPiNoon() {
        return doingPiNoon;
    }

    public void setDoingPiNoon(boolean doingPiNoon) {
        this.doingPiNoon = doingPiNoon;
    }
}
