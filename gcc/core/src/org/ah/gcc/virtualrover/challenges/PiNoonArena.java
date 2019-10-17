package org.ah.gcc.virtualrover.challenges;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import org.ah.gcc.virtualrover.ModelFactory;

import java.util.ArrayList;
import java.util.List;

import static org.ah.gcc.virtualrover.MainGame.SCALE;

public class PiNoonArena implements Challenge {

    private ModelInstance arena;

    private List<BoundingBox> boxes;

    public PiNoonArena(ModelFactory modelFactory) {
        Model arenaModel = modelFactory.loadModel("arena.obj");

        arena = new ModelInstance(arenaModel);
        arena.transform.setToTranslationAndScaling(0, -70 * SCALE, 0, SCALE, SCALE, SCALE);
        arena.materials.get(0).set(ColorAttribute.createDiffuse(new Color(0.5f, 0.1f, 0.1f, 1f)));

        boxes = new ArrayList<BoundingBox>();
        float wallWidth = 0.1f;
        boxes.add(new BoundingBox(new Vector3(-1000 * SCALE, 100 * SCALE, -1000 * SCALE), new Vector3(1000 * SCALE, 100 * SCALE, (-1000 + wallWidth) * SCALE)));
        boxes.add(new BoundingBox(new Vector3(-1000 * SCALE, 100 * SCALE, 1000 * SCALE), new Vector3(1000 * SCALE, 100 * SCALE, (1000 - wallWidth) * SCALE)));

        boxes.add(new BoundingBox(new Vector3(-1000 * SCALE, 100 * SCALE, -1000 * SCALE), new Vector3((-1000 + wallWidth) * SCALE, 100 * SCALE, 1000 * SCALE)));
        boxes.add(new BoundingBox(new Vector3(1000 * SCALE, 100 * SCALE, -1000 * SCALE), new Vector3((1000 - wallWidth) * SCALE, 100 * SCALE, 1000 * SCALE)));
    }

    public void dispose() {
        // Nothing to do here
    }

    public void update() {
        // Nothing to update here
    }

    public void render(ModelBatch batch, Environment environment) {
        batch.render(arena, environment);
    }
}
