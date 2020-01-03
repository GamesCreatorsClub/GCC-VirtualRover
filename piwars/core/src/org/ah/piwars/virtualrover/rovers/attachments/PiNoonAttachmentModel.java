package org.ah.piwars.virtualrover.rovers.attachments;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import org.ah.piwars.virtualrover.ModelFactory;

public class PiNoonAttachmentModel extends AbstractAttachmentModel {
    private static final boolean SHOW_MARKER = true;

    private ModelInstance pinoon;
    private ModelInstance marker;

    private Balloon[] balloons = new Balloon[3];

    private Matrix4 sharpPointMatrix = new Matrix4();
    private Vector2 sharpPoint = new Vector2();
    private Vector3 sharpPointPos = new Vector3();

    private long balloonPeriod = 0;

    public PiNoonAttachmentModel(ModelFactory modelFactory, Color roverColour) {
        Color balloonTransparentColour = new Color(roverColour);
        balloonTransparentColour.a = 0.7f;

        marker = new ModelInstance(modelFactory.getMarker(), 0, 0, 0);
        marker.transform.scale(5f, 5f, 5f);

        marker.materials.get(0).set(ColorAttribute.createDiffuse(roverColour));

        pinoon = new ModelInstance(modelFactory.getPiNoon(), 0, 0, 0);
        pinoon.materials.get(0).set(ColorAttribute.createDiffuse(Color.GRAY));

        this.balloons[0] = new Balloon(modelFactory.getBaloon(), balloonTransparentColour);
        this.balloons[1] = new Balloon(modelFactory.getBaloon(), balloonTransparentColour);
        this.balloons[2] = new Balloon(modelFactory.getBaloon(), balloonTransparentColour);
    }

    @Override
    public void setColour(Color roverColour) {
        marker.materials.get(0).set(ColorAttribute.createDiffuse(roverColour));
        Color balloonTransparentColour = new Color(roverColour);
        balloonTransparentColour.a = 0.7f;
        for (Balloon balloon : balloons) {
            balloon.setColour(balloonTransparentColour);
        }
    }

    public Vector2 getSharpPoint() {
        sharpPoint.set(sharpPointPos.x, sharpPointPos.z);
        return sharpPoint;
    }

    @Override
    public void update(Matrix4 roverTransform) {
        balloonPeriod++;

        sharpPointMatrix.set(roverTransform);
        sharpPointMatrix.translate(-139f, 159f, -56.25f);
        sharpPointPos = sharpPointMatrix.getTranslation(sharpPointPos);

        for (Balloon balloon : this.balloons) {
            balloon.balloon.transform.set(roverTransform);
        }

        balloons[0].balloon.transform.translate(15f, 178f, -70f);
        balloons[1].balloon.transform.translate(-15f, 178f, -50f);
        balloons[2].balloon.transform.translate(15f, 158f, -30f);

        balloons[0].balloon.transform.rotate(new Vector3(1, 1, 0), (float) (-50 + (Math.sin(balloonPeriod / (Math.random() * 4f + 60f)) * 5f)));
        balloons[1].balloon.transform.rotate(new Vector3(0, 0, 1), (float) (45 + (Math.cos(balloonPeriod / (Math.random() * 4f + 60f)) * 5f)));
        balloons[2].balloon.transform.rotate(new Vector3(1, 1, 0), (float) (30 + (Math.sin(balloonPeriod / (Math.random() * 4f + 60f)) * 5f)));

        pinoon.transform.set(roverTransform);

        pinoon.transform.translate(-10f, 8f, -66f);
        pinoon.transform.rotate(new Vector3(0, 1, 0), 180);

        if (SHOW_MARKER) {
            marker.transform.setToTranslation(sharpPointPos);
            marker.transform.scale(0.003f, 0.003f, 0.003f);
        }
    }

    @Override
    public void render(ModelBatch batch, Environment environment) {
        batch.render(pinoon, environment);
        for (Balloon balloon : balloons) {
            balloon.render(batch, environment);
        }

        if (SHOW_MARKER) {
            batch.render(marker, environment);
        }
    }

    protected static class Balloon {
        private ModelInstance balloon;
        // private Color colour;

        private boolean popped = false;

        public Balloon(Model model, Color balloonTransparentColour) {
            // this.colour = balloonTransparentColour;
            balloon = new ModelInstance(model, 0, 0, 0);
            balloon.materials.get(0).set(ColorAttribute.createDiffuse(balloonTransparentColour));
            balloon.materials.get(0).set(new BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA));
        }

        public void setColour(Color balloonTransparentColour) {
            // this.colour = balloonTransparentColour;
            balloon.materials.get(0).set(ColorAttribute.createDiffuse(balloonTransparentColour));
            balloon.materials.get(0).set(new BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA));
        }

        public void render(ModelBatch batch, Environment environment) {
            if (!popped) {
                batch.render(balloon, environment);
            }
        }

        public void reset() { popped = false; }

        public void pop() { popped = true; }
    }

    public void setBalloonBits(int balloonBits) {
        for (int balloonNo = 0; balloonNo < 3; balloonNo++) {
            int balloonBit = (1 << balloonNo);
            if ((balloonBits & balloonBit) != 0) {
                if (balloons[balloonNo].popped) {
                    balloons[balloonNo].reset();
                }
            } else {
                if (!balloons[balloonNo].popped) {
                    balloons[balloonNo].pop();
                }
            }
        }
    }
}
