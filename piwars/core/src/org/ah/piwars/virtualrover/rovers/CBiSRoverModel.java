package org.ah.piwars.virtualrover.rovers;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.math.Vector3;

import org.ah.piwars.virtualrover.ModelFactory;
import org.ah.piwars.virtualrover.game.rovers.Rover;

import java.util.NoSuchElementException;

public class CBiSRoverModel extends FourWheelRoverModel {

    private ModelInstance body;

    private static float ROVER_SCALE = 26;

    public CBiSRoverModel(ModelFactory modelFactory) throws NoSuchElementException {
        this("CBiS Rover", modelFactory, Color.WHITE);
    }

    public CBiSRoverModel(String name, ModelFactory modelFactory, Color colour) throws NoSuchElementException {
        super(name, colour);

        body = new ModelInstance(modelFactory.getcBody(), 0, 0, 0);
        body.materials.get(0).set(ColorAttribute.createDiffuse(colour));

        fr = new BigWheel(modelFactory, Color.YELLOW, ROVER_SCALE, 20f, -27f, -145f, 270);
        fl = new BigWheel(modelFactory, Color.YELLOW, ROVER_SCALE, 42f, -27f, 0f, 90);
        br = new BigWheel(modelFactory, Color.YELLOW, ROVER_SCALE, 165f, -27f, -145f, 270);
        bl = new BigWheel(modelFactory, Color.YELLOW, ROVER_SCALE, 190f, -27f, 0f, 90);
    }

    @Override
    public void setColour(Color colour) {
        super.setColour(colour);
        body.materials.get(0).set(ColorAttribute.createDiffuse(colour));
    }

    @Override
    public void update(Rover rover) {
        super.update(rover);

        float bearing = rover.getBearing();

        transform.rotate(new Vector3(0, 1, 0), 180 + bearing); // 180 + is because of all rover models are made 'backwards'
        transform.translate(-80f, 0, 55f);

        body.transform.set(transform);

        body.transform.translate(7.8f, -1.6f, 0f);
        body.transform.rotate(new Vector3(0, 1, 0), 90);

        fr.getTransform().set(transform);
        fl.getTransform().set(transform);
        br.getTransform().set(transform);
        bl.getTransform().set(transform);

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
