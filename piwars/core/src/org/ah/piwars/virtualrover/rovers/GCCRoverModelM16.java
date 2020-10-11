package org.ah.piwars.virtualrover.rovers;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;

import org.ah.piwars.virtualrover.game.rovers.Rover;

import java.util.NoSuchElementException;

import static java.lang.Math.abs;
import static java.lang.Math.atan2;

public class GCCRoverModelM16 extends FourWheelRoverModel {

    private ModelInstance body;

    private ModelInstance top;

    private Matrix4 bodyTransform = new Matrix4();

    public GCCRoverModelM16(AssetManager assetManager) throws NoSuchElementException {
        this("GCC Rover M16", assetManager, Color.WHITE);
    }

    public GCCRoverModelM16(String name, AssetManager assetManager, Color colour) throws NoSuchElementException {
        super(name, colour);

        body = new ModelInstance(assetManager.get("3d/rovers/gcc_M16/body.obj", Model.class), 0, 0, 0);
        top = new ModelInstance(assetManager.get("3d/rovers/gcc_M16/top.obj", Model.class), 0, 0, 0);

        body.materials.get(0).set(ColorAttribute.createDiffuse(Color.BLACK));

        top.materials.get(0).set(ColorAttribute.createDiffuse(colour));

        Model motorHolderModel = assetManager.get("3d/rovers/gcc_M16/motor_holder.obj");
        Model wheelModel = assetManager.get("3d/rovers/gcc_M16/wheel.obj");
        Model tyreModel = assetManager.get("3d/rovers/gcc_M16/tyre.obj");

        fr = new GCCRoverWheel(motorHolderModel, wheelModel, tyreModel, Color.ORANGE, 10f, -36.7f, -100f, 270);
        fl = new GCCRoverWheel(motorHolderModel, wheelModel, tyreModel, Color.ORANGE, 25f, -36.7f, -10f, 90);
        br = new GCCRoverWheel(motorHolderModel, wheelModel, tyreModel, Color.ORANGE, 132f, -36.7f, -100f, 270);
        bl = new GCCRoverWheel(motorHolderModel, wheelModel, tyreModel, Color.ORANGE, 150f, -36.7f, -10f, 90);
    }

    public static void load(AssetManager assetManager) {
        assetManager.load("3d/rovers/gcc_M16/body.obj", Model.class);
        assetManager.load("3d/rovers/gcc_M16/top.obj", Model.class);

        assetManager.load("3d/rovers/gcc_M16/motor_holder.obj", Model.class);
        assetManager.load("3d/rovers/gcc_M16/wheel.obj", Model.class);
        assetManager.load("3d/rovers/gcc_M16/tyre.obj", Model.class);
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
        bodyTransform.set(transform);
        //bodyTransform.rotate(new Vector3(0, 1, 0), 180 + bearing); // 180 + is because of all rover models are made 'backwards'
        bodyTransform.translate(-80f, 0, 55f);

        fr.getTransform().set(bodyTransform);
        fl.getTransform().set(bodyTransform);
        br.getTransform().set(bodyTransform);
        bl.getTransform().set(bodyTransform);

        top.transform.set(bodyTransform);
        body.transform.set(bodyTransform);

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

    @Override
    public void dispose() {
    }
}
