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

public class GCCRoverModelM18 extends FourWheelRoverModel {

    private static Color ROVER_COLOUR = new Color(0, 0.5f, 0.7f, 1);

    private ModelInstance cover;
    private ModelInstance bodyTop1;
    private ModelInstance bodyBottom1;
    private Matrix4 bodyTransform = new Matrix4();

    public GCCRoverModelM18(AssetManager assetManager) throws NoSuchElementException {
        this("GCC Rover M18", assetManager, Color.WHITE);
    }

    public GCCRoverModelM18(String name, AssetManager assetManager, Color colour) throws NoSuchElementException {
        super(name, colour);

        Model bodyTopModel = assetManager.get("3d/rovers/gcc_M18/rover_2018_shell_top_2.obj");
        Model bodyBottomModel = assetManager.get("3d/rovers/gcc_M18/rover_2018_shell_bottom_2.obj");

        cover = new ModelInstance(assetManager.get("3d/rovers/gcc_M18/rover_2018_shell_cover.obj", Model.class), 0, 0 ,0);
        bodyTop1 = new ModelInstance(bodyTopModel, 0, 0, 0);
        bodyBottom1 = new ModelInstance(bodyBottomModel, 0, 0, 0);

        cover.materials.get(0).set(ColorAttribute.createDiffuse(colour));
        bodyTop1.materials.get(0).set(ColorAttribute.createDiffuse(Color.WHITE));
        bodyBottom1.materials.get(0).set(ColorAttribute.createDiffuse(ROVER_COLOUR));

        Model motorHolderModel = assetManager.get("3d/rovers/gcc_M16/motor_holder.obj");
        Model wheelModel = assetManager.get("3d/rovers/gcc_M16/wheel.obj");
        Model tyreModel = assetManager.get("3d/rovers/gcc_M16/tyre.obj");

        fr = new GCCRoverWheel(motorHolderModel, wheelModel, tyreModel, Color.ORANGE, 10f, -36.7f, -100f, 270);
        fl = new GCCRoverWheel(motorHolderModel, wheelModel, tyreModel, Color.ORANGE, 25f, -36.7f, -10f, 90);
        br = new GCCRoverWheel(motorHolderModel, wheelModel, tyreModel, Color.ORANGE, 132f, -36.7f, -100f, 270);
        bl = new GCCRoverWheel(motorHolderModel, wheelModel, tyreModel, Color.ORANGE, 150f, -36.7f, -10f, 90);
    }

    public static void load(AssetManager assetManager) {
        assetManager.load("3d/rovers/gcc_M18/rover_2018_shell_bottom_2.obj", Model.class);
        assetManager.load("3d/rovers/gcc_M18/rover_2018_shell_top_2.obj", Model.class);
        assetManager.load("3d/rovers/gcc_M18/rover_2018_shell_cover.obj", Model.class);
    }

    @Override
    public void setColour(Color colour) {
        super.setColour(colour);
        cover.materials.get(0).set(ColorAttribute.createDiffuse(colour));
    }

    @Override
    public void update(Rover rover) {
        super.update(rover);

        float bearing = rover.getBearing();
        bodyTransform.set(transform);
//        bodyTransform.rotate(new Vector3(0, 1, 0), 180 + bearing); // 180 + is because of all rover models are made 'backwards'
        bodyTransform.translate(0f, -50, 0f);
//        transform.translate(-80f, 0, 55f);

        fr.getTransform().set(bodyTransform);
        fl.getTransform().set(bodyTransform);
        br.getTransform().set(bodyTransform);
        bl.getTransform().set(bodyTransform);

        cover.transform.set(bodyTransform);
        bodyTop1.transform.set(bodyTransform);
        bodyBottom1.transform.set(bodyTransform);

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
//        bl.render(batch, environment);
//        br.render(batch, environment);
//        fl.render(batch, environment);
//        fr.render(batch, environment);

        batch.render(cover, environment);
        batch.render(bodyTop1, environment);
        batch.render(bodyBottom1, environment);
    }

    @Override
    public void dispose() {
    }
}
