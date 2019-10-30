package org.ah.gcc.virtualrover.rovers;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.math.Vector3;

import org.ah.gcc.virtualrover.ModelFactory;

import java.util.NoSuchElementException;

import static org.ah.gcc.virtualrover.MainGame.SCALE;

public class CBiSRover extends AbstractRover {
    private ModelInstance body;

    private BigWheel fr;
    private BigWheel br;
    private BigWheel bl;
    private BigWheel fl;

    private static float ROVER_SCALE = 26;

    public CBiSRover(String name, ModelFactory modelFactory, Color colour) throws NoSuchElementException {
        super(name, colour);

        body = new ModelInstance(modelFactory.getcBody(), 0, 0, 0);
        body.materials.get(0).set(ColorAttribute.createDiffuse(colour));

        fr = new BigWheel(modelFactory, Color.YELLOW, ROVER_SCALE, 20f, -27f, -145f, 270);
        fl = new BigWheel(modelFactory, Color.YELLOW, ROVER_SCALE, 42f, -27f, 0f, 90);
        br = new BigWheel(modelFactory, Color.YELLOW, ROVER_SCALE, 165f, -27f, -145f, 270);
        bl = new BigWheel(modelFactory, Color.YELLOW, ROVER_SCALE, 190f, -27f, 0f, 90);
    }

    @Override
    public void update(Vector3 position, float headingDegs) {
        transform.setToTranslationAndScaling(position.x * SCALE, 0, -position.y * SCALE, SCALE, SCALE, SCALE);
        transform.rotate(new Vector3(0, 1, 0), 180 + headingDegs); // 180 + is because of all rover models are made 'backwards'
        transform.translate(-80f, 0, 55f);

        body.transform.set(transform);

        body.transform.scale(ROVER_SCALE, ROVER_SCALE, ROVER_SCALE);
        body.transform.translate(7.8f, -1.6f, 0f);
        body.transform.rotate(new Vector3(0, 1, 0), 90);

        fr.getTransform().set(transform);
        fl.getTransform().set(transform);
        br.getTransform().set(transform);
        bl.getTransform().set(transform);

        fr.update();
        fl.update();
        br.update();
        bl.update();

        super.update();
    }

    @Override
    public void render(ModelBatch batch, Environment environment) {
        bl.render(batch, environment);
        br.render(batch, environment);
        fl.render(batch, environment);
        fr.render(batch, environment);

        renderAttachment(batch, environment);

        batch.render(body, environment);
    }
}
