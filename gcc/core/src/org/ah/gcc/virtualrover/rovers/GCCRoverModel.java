package org.ah.gcc.virtualrover.rovers;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.math.Vector3;

import org.ah.gcc.virtualrover.ModelFactory;
import org.ah.gcc.virtualrover.game.Rover;

import java.util.NoSuchElementException;

import static java.lang.Math.abs;
import static java.lang.Math.atan2;

public class GCCRoverModel extends FourWheelRoverModel {

    private ModelInstance body;

    private ModelInstance top;

     public GCCRoverModel(String name, ModelFactory modelFactory, Color colour) throws NoSuchElementException {
        super(name, colour);

        body = new ModelInstance(modelFactory.getBody(), 0, 0, 0);
        top = new ModelInstance(modelFactory.getTop(), 0, 0, 0);

        body.materials.get(0).set(ColorAttribute.createDiffuse(Color.BLACK));

        top.materials.get(0).set(ColorAttribute.createDiffuse(colour));

        fr = new GCCRoverWheel(modelFactory, Color.ORANGE, 10f, -36.7f, -100f, 270);
        fl = new GCCRoverWheel(modelFactory, Color.ORANGE, 25f, -36.7f, -10f, 90);
        br = new GCCRoverWheel(modelFactory, Color.ORANGE, 132f, -36.7f, -100f, 270);
        bl = new GCCRoverWheel(modelFactory, Color.ORANGE, 150f, -36.7f, -10f, 90);
    }

    @Override
    public void setColour(Color colour) {
        super.setColour(colour);
        top.materials.get(0).set(ColorAttribute.createDiffuse(colour));
    }

    @Override
    public void update(Rover rover) {
        super.update(rover);

        float bearing = rover.getBearing();

        transform.rotate(new Vector3(0, 1, 0), 180 + bearing); // 180 + is because of all rover models are made 'backwards'
        transform.translate(-80f, 0, 55f);

        fr.getTransform().set(transform);
        fl.getTransform().set(transform);
        br.getTransform().set(transform);
        bl.getTransform().set(transform);

        top.transform.set(transform);
        body.transform.set(transform);

        Vector3 velocity = rover.getVelocity();
        if (abs(rover.getTurnSpeed()) < 0.01f) {
            if (abs(velocity.x) < 0.01f && abs(velocity.y) < 0.01f) {
                // fr.setDegrees(0f);
                // fl.setDegrees(0f);
                // br.setDegrees(0f);
                // bl.setDegrees(0f);
            } else {
                float direction = (float)(atan2(velocity.y, velocity.x) * 180 / Math.PI);
                float relativeAngle = fixAngle(direction - bearing);

                float wheelAngle = relativeAngle;
                if (wheelAngle > 95f && wheelAngle < 265f) {
                    wheelAngle = fixAngle(wheelAngle + 180);
                }

                fr.setDegrees(wheelAngle);
                fl.setDegrees(wheelAngle);
                br.setDegrees(wheelAngle);
                bl.setDegrees(wheelAngle);
            }
        } else {
            if (abs(velocity.x) < 0.01f && abs(velocity.y) < 0.01f) {
                fr.setDegrees(60f);
                fl.setDegrees(-60f);
                br.setDegrees(-60f);
                bl.setDegrees(60f);
            } else {
                float sign = 1f;
                float dist;
                if (rover.getTurnSpeed() >= 0f) {
                    dist = 100f;
                } else {
                    dist = 100f;
                    sign = -1f;
                }
                float frontAngle = (float)(atan2(69, dist - 36.5) * 180 / Math.PI);
                float backAngle = (float)(atan2(69, dist + 36.5) * 180 / Math.PI);

                fr.setDegrees(frontAngle * sign);
                fl.setDegrees(frontAngle * sign);
                br.setDegrees(-backAngle * sign);
                bl.setDegrees(-backAngle * sign);
            }
        }

        setWheelSpeeds(rover.getSpeed());

        fr.update();
        fl.update();
        br.update();
        bl.update();
    }

    @Override
    public void render(ModelBatch batch, Environment environment) {
        bl.render(batch, environment);
        br.render(batch, environment);
        fl.render(batch, environment);
        fr.render(batch, environment);

        batch.render(top, environment);
        batch.render(body, environment);
    }
}
