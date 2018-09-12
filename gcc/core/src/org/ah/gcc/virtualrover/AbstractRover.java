package org.ah.gcc.virtualrover;

import static org.ah.gcc.virtualrover.MainGame.SCALE;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

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

public abstract class AbstractRover implements Rover {

    private int id = 0;

    private String name;
    private Color colour;

    private int balloons = 3;

    protected long balloonPeriod = 0;
    protected Matrix4 transform = new Matrix4();

    protected ModelInstance pinoon;
    protected ModelInstance marker;
    protected ModelInstance balloon1;
    protected ModelInstance balloon2;
    protected ModelInstance balloon3;

    protected Vector3 ballonPosition1 = new Vector3();
    protected Vector3 ballonPosition2 = new Vector3();
    protected Vector3 ballonPosition3 = new Vector3();

    protected boolean hasBallon1 = true;
    protected boolean hasBallon2 = true;
    protected boolean hasBallon3 = true;

    protected List<Rover> robots = new ArrayList<Rover>();

    protected Matrix4 sharpPointMatrix = new Matrix4();
    protected Vector2 sharpPoint = new Vector2();
    protected Vector3 sharpPointPos = new Vector3();


    public AbstractRover(String name, ModelFactory modelFactory, Color colour) throws NoSuchElementException {
        this.name = name;
        this.colour = colour;

        marker = new ModelInstance(modelFactory.getMarker(), 0, 0, 0);
        marker.transform.scale(0.05f, 0.05f, 0.05f);
        marker.materials.get(0).set(ColorAttribute.createDiffuse(colour));

        pinoon = new ModelInstance(modelFactory.getPiNoon(), 0, 0, 0);
        pinoon.materials.get(0).set(ColorAttribute.createDiffuse(Color.GRAY));

        setBalloons(3);

        Color balloonTransparentColour = new Color(colour);
        balloonTransparentColour.a = 0.7f;
        balloon1 = new ModelInstance(modelFactory.getBaloon(), 0, 0, 0);
        balloon1.materials.get(0).set(ColorAttribute.createDiffuse(balloonTransparentColour));
        balloon1.materials.get(0).set(new BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA));

        balloon2 = new ModelInstance(modelFactory.getBaloon(), 0, 0, 0);
        balloon2.materials.get(0).set(ColorAttribute.createDiffuse(balloonTransparentColour));
        balloon2.materials.get(0).set(new BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA));

        balloon3 = new ModelInstance(modelFactory.getBaloon(), 0, 0, 0);
        balloon3.materials.get(0).set(ColorAttribute.createDiffuse(balloonTransparentColour));
        balloon3.materials.get(0).set(new BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA));
    }

    public String getName() {
        return name;
    }

    public Color getColour() {
        return colour;
    }

    public int getBalloons() {
        return balloons;
    }

    public void setBalloons(int balloons) {
        this.balloons = balloons;
    }

    public void popBaloon() {
        balloons -= 1;
    }

    @Override
    public void addOtherRover(Rover robot) {
        robots.add(robot);
    }

    @Override
    public Matrix4 getTransform() {
        return transform;
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

    protected void renderBalloons(ModelBatch batch, Environment environment) {
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

    public boolean collidesWithRover(Matrix4 move, Rover rover) {
        boolean collision = Intersector.overlapConvexPolygons(getPolygon(move), rover.getPolygon());

        return collision;
    }

    @Override
    public Polygon getPolygon() {
        return getPolygon(transform);
    }

    protected abstract Polygon getPolygon(Matrix4 move);

    @Override
    public Vector2 sharpPoint() {
        sharpPointMatrix.set(transform);
        sharpPointMatrix.translate(-21f, 24f, -9f);

        sharpPointPos = sharpPointMatrix.getTranslation(sharpPointPos);

        sharpPoint.set(sharpPointPos.x, sharpPointPos.z);
        return sharpPoint;
    }

    protected abstract void translateSharpPoint(Matrix4 sharpPointMatrix);

    protected void checkForBalloonPopped() {
        for (Rover robot : robots) {
            Vector2 robotSharpPoint = robot.sharpPoint();
            if (getBallon1().contains(robotSharpPoint)) {
                hasBallon1 = false;
            }
            if (getBallon2().contains(robotSharpPoint)) {
                hasBallon2 = false;
            }
            if (getBallon3().contains(robotSharpPoint)) {
                hasBallon3 = false;
            }
        }
    }

    protected void updateMarkerTransform() {
        if (MainGame.SHOW_MARKER) {
            marker.transform.setToTranslation(sharpPointPos);
            marker.transform.scale(SCALE, SCALE, SCALE);
        }
    }

    protected void updateBalloons() {
        balloon1.transform.set(transform);
        balloon2.transform.set(transform);
        balloon3.transform.set(transform);

        balloon1.transform.scale(0.16f, 0.16f, 0.16f);
        balloon2.transform.scale(0.16f, 0.16f, 0.16f);
        balloon3.transform.scale(0.16f, 0.16f, 0.16f);

        balloon1.transform.translate(0f, 158f, -50f);
        balloon2.transform.translate(-15f, 178f, -64f);
        balloon3.transform.translate(0f, 178f, -70f);

        balloon1.transform.rotate(new Vector3(1, 1, 0), (float) (30 + (Math.sin(balloonPeriod / (Math.random() * 4f + 60f)) * 5f)));
        balloon2.transform.rotate(new Vector3(0, 0, 1), (float) (45 + (Math.cos(balloonPeriod / (Math.random() * 4f + 60f)) * 5f)));
        balloon3.transform.rotate(new Vector3(1, 1, 0), (float) (-50 + (Math.sin(balloonPeriod / (Math.random() * 4f + 60f)) * 5f)));
    }

    protected void updatePiNoon() {
        pinoon.transform.set(transform);
        pinoon.transform.scale(0.16f, 0.16f, 0.16f);
        pinoon.transform.translate(-10f, 8f, -66f);
        pinoon.transform.rotate(new Vector3(0, 1, 0), 180);
    }
}
