package org.ah.gcc.virtualrover.rovers;

import static org.ah.gcc.virtualrover.MainGame.SCALE;

import java.util.NoSuchElementException;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
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
import org.ah.gcc.virtualrover.MainGame;
import org.ah.gcc.virtualrover.ModelFactory;

public abstract class AbstractRover implements Rover {

    private int id = 0;

    private String name;
    private Color colour;

    protected Matrix4 transform = new Matrix4();
    protected Matrix4 previousTransform = new Matrix4();

    protected Balloons balloons;

    protected AbstractRover(String name, ModelFactory modelFactory, Color colour) throws NoSuchElementException {
        this.name = name;
        this.colour = colour;

        Color balloonTransparentColour = new Color(colour);
        balloonTransparentColour.a = 0.7f;

        balloons = new Balloons(modelFactory, colour, balloonTransparentColour);
    }

    public String getName() {
        return name;
    }

    public Color getColour() {
        return colour;
    }

    @Override
    public Matrix4 getTransform() {
        return transform;
    }

    @Override
    public Matrix4 getPreviousTransform() {
        return previousTransform;
    }

    public int getId() {
        return id;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    public void removeBalloons() {
        balloons.removeBalloons();
    }

    public void resetBalloons() {
        balloons.resetBalloons();
    }

    protected void renderBalloons(ModelBatch batch, Environment environment) {
        balloons.render(batch, environment);
    }

    @Override
    public Vector2 sharpPoint() {
        return balloons.getSharpPoint(transform);
    }

    protected abstract void translateSharpPoint(Matrix4 sharpPointMatrix);

    public int checkIfBalloonsPopped(Vector2 robotSharpPoint) {
        return balloons.checkIfBalloonsPopped(robotSharpPoint);
    }

    public void update() {
        balloons.update(transform);
    }

    protected static float calcSpeedMillimetresInFrame(float speedMPS) {
        return speedMPS * Gdx.graphics.getDeltaTime() * 1000; // in millimetres
    }

    protected static class Balloons {
        private ModelInstance pinoon;
        private ModelInstance marker;

        private Balloon[] balloons = new Balloon[3];

        private Matrix4 sharpPointMatrix = new Matrix4();
        private Vector2 sharpPoint = new Vector2();
        private Vector3 sharpPointPos = new Vector3();

        private long balloonPeriod = 0;
        private long previousTime;

        public Balloons(ModelFactory modelFactory, Color roverColour, Color balloonTransparentColour) {
            marker = new ModelInstance(modelFactory.getMarker(), 0, 0, 0);
            marker.transform.scale(5f, 5f, 5f);

            marker.materials.get(0).set(ColorAttribute.createDiffuse(roverColour));

            pinoon = new ModelInstance(modelFactory.getPiNoon(), 0, 0, 0);
            pinoon.materials.get(0).set(ColorAttribute.createDiffuse(Color.GRAY));

            this.balloons[0] = new Balloon(modelFactory.getBaloon(), balloonTransparentColour);
            this.balloons[1] = new Balloon(modelFactory.getBaloon(), balloonTransparentColour);
            this.balloons[2] = new Balloon(modelFactory.getBaloon(), balloonTransparentColour);
        }

        public Vector2 getSharpPoint(Matrix4 roverTransform) {
            sharpPoint.set(sharpPointPos.x, sharpPointPos.z);
            return sharpPoint;
        }

        public void update(Matrix4 roverTransform) {
            balloonPeriod++;

            sharpPointMatrix.set(roverTransform);
            sharpPointMatrix.translate(-139f, 159f, -56.25f);
            sharpPointPos = sharpPointMatrix.getTranslation(sharpPointPos);

            for (Balloon balloon : this.balloons) {
                balloon.balloon.transform.set(roverTransform);
                // balloon.balloon.transform.scale(0.16f, 0.16f, 0.16f);
            }

            balloons[0].balloon.transform.translate(15f, 158f, -30f);
            balloons[1].balloon.transform.translate(-15f, 178f, -50f);
            balloons[2].balloon.transform.translate(15f, 178f, -70f);

            balloons[0].balloon.transform.rotate(new Vector3(1, 1, 0), (float) (30 + (Math.sin(balloonPeriod / (Math.random() * 4f + 60f)) * 5f)));
            balloons[1].balloon.transform.rotate(new Vector3(0, 0, 1), (float) (45 + (Math.cos(balloonPeriod / (Math.random() * 4f + 60f)) * 5f)));
            balloons[2].balloon.transform.rotate(new Vector3(1, 1, 0), (float) (-50 + (Math.sin(balloonPeriod / (Math.random() * 4f + 60f)) * 5f)));

            pinoon.transform.set(roverTransform);
            // pinoon.transform.scale(0.16f, 0.16f, 0.16f);
            pinoon.transform.translate(-10f, 8f, -66f);
            pinoon.transform.rotate(new Vector3(0, 1, 0), 180);

            if (MainGame.SHOW_MARKER) {
                // marker.transform.setToTranslationAndScaling(0.01f, 0.01f, 0.01f, sharpPointPos.x, sharpPointPos.y, sharpPointPos.z);
                marker.transform.setToTranslation(sharpPointPos);
                marker.transform.scale(0.003f, 0.003f, 0.003f);
            }
        }

        public void removeBalloons() {
            for (Balloon balloon : this.balloons) {
                balloon.remove();
            }
        }

        public void resetBalloons() {
            for (Balloon balloon : this.balloons) {
                balloon.reset();
            }
        }

        protected void render(ModelBatch batch, Environment environment) {
            batch.render(pinoon, environment);
            for (Balloon balloon : balloons) {
                balloon.render(batch, environment);
            }

            if (MainGame.SHOW_MARKER) {
                batch.render(marker, environment);
            }
        }

        public int checkIfBalloonsPopped(Vector2 sharpPoint) {
            float radius = 40 * MainGame.SCALE;

            int notPoppedBalloons = 0;
            for (Balloon balloon : balloons) {
                if (!balloon.popped) {
                    balloon.balloon.transform.getTranslation(balloon.ballonPosition);

                    Circle c = new Circle(balloon.ballonPosition.x, balloon.ballonPosition.z, radius);
                    if (c.contains(sharpPoint)) {
                        balloon.popped = true;
                    } else {
                        notPoppedBalloons++;
                    }
                }
            }
            return notPoppedBalloons;
        }
    }

    protected static class Balloon {
        private ModelInstance balloon;

        private Vector3 ballonPosition = new Vector3();

        private boolean popped = false;

        public Balloon(Model model, Color balloonTransparentColour) {
            balloon = new ModelInstance(model, 0, 0, 0);
            balloon.materials.get(0).set(ColorAttribute.createDiffuse(balloonTransparentColour));
            balloon.materials.get(0).set(new BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA));
        }

        public void render(ModelBatch batch, Environment environment) {
            if (!popped) {
                batch.render(balloon, environment);
            }
        }

        public void reset() { popped = false; }

        public void remove() { popped = true; }
    }
}
