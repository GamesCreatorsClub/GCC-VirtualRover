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

public class MacFeegleModel extends FourWheelRoverModel {

    private static float ROVER_SCALE = 26;

    private ModelInstance body;

    private Matrix4 bodyTransform = new Matrix4();

    public MacFeegleModel(AssetManager assetManager) throws NoSuchElementException {
        this("MacFeegle Rover", assetManager, Color.WHITE);
    }

    public MacFeegleModel(String name, AssetManager assetManager, Color colour) throws NoSuchElementException {
        super(name, colour);

        body = new ModelInstance(assetManager.get("3d/rovers/CBiS_18/body.obj", Model.class), 0, 0, 0);
        body.materials.get(0).set(ColorAttribute.createDiffuse(colour));

        Model wheelModel = assetManager.get("3d/rovers/CBiS_18/wheel.obj", Model.class);
        Model tyreModel = assetManager.get("3d/rovers/CBiS_18/tyre.obj", Model.class);

        fr = new BigWheel(wheelModel, tyreModel, Color.YELLOW, ROVER_SCALE, 20f, -27f, -145f, 270);
        fl = new BigWheel(wheelModel, tyreModel, Color.YELLOW, ROVER_SCALE, 42f, -27f, 0f, 90);
        br = new BigWheel(wheelModel, tyreModel, Color.YELLOW, ROVER_SCALE, 165f, -27f, -145f, 270);
        bl = new BigWheel(wheelModel, tyreModel, Color.YELLOW, ROVER_SCALE, 190f, -27f, 0f, 90);
    }

    public static void load(AssetManager assetManager) {
        assetManager.load("3d/rovers/CBiS_18/body.obj", Model.class);
        assetManager.load("3d/rovers/CBiS_18/tyre.obj", Model.class);
        assetManager.load("3d/rovers/CBiS_18/wheel.obj", Model.class);
    }

    @Override
    public void setColour(Color colour) {
        super.setColour(colour);
        body.materials.get(0).set(ColorAttribute.createDiffuse(colour));
    }

    @Override
    public void update(Rover rover) {
        super.update(rover);

        //float bearing = rover.getBearing();

        bodyTransform.set(transform);
        // bodyTransform.rotate(new Vector3(0, 1, 0), 180 + bearing); // 180 + is because of all rover models are made 'backwards'
        bodyTransform.translate(-80f, 0, 55f);

        body.transform.set(bodyTransform);

        body.transform.translate(7.8f, -1.6f, 0f);
        body.transform.rotate(new Vector3(0, 1, 0), 90);

        fr.getTransform().set(bodyTransform);
        fl.getTransform().set(bodyTransform);
        br.getTransform().set(bodyTransform);
        bl.getTransform().set(bodyTransform);

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

        batch.render(body, environment);
    }

    @Override
    public void dispose() {
    }
}
