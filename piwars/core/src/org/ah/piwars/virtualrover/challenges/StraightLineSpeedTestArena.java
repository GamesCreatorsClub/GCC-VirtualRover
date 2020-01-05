package org.ah.piwars.virtualrover.challenges;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.g3d.utils.shapebuilders.BoxShapeBuilder;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap;

import org.ah.piwars.virtualrover.ModelFactory;
import org.ah.piwars.virtualrover.VisibleObject;

import static org.ah.piwars.virtualrover.MainGame.SCALE;
import static org.ah.piwars.virtualrover.game.challenge.StraightLineSpeedTestChallenge.CHICANE_LENGTH;
import static org.ah.piwars.virtualrover.game.challenge.StraightLineSpeedTestChallenge.CHICANE_WIDTH;
import static org.ah.piwars.virtualrover.game.challenge.StraightLineSpeedTestChallenge.COURSE_LENGTH;
import static org.ah.piwars.virtualrover.game.challenge.StraightLineSpeedTestChallenge.CUT_MODIFIER;;

public class StraightLineSpeedTestArena extends AbstractChallenge {

    private Model floorModel;
    private ModelInstance floorModelInstance;
    private Material floorMaterial;

    private Material wallMaterial;
    private Model horizontalWallModel;
    private ModelInstance topWall;
    private ModelInstance bottomWall;

    private Material lineMaterial;
    private Model lineModel;
    private ModelInstance line;

    private Material chicaneMaterial;
    private Model chicaneModel;
    private Array<ModelInstance> chicanes = new Array<ModelInstance>();

    public StraightLineSpeedTestArena(ModelFactory modelFactory) {
        super(modelFactory);

        int courseLength = COURSE_LENGTH;

        int attrs = Usage.Position | Usage.ColorUnpacked  | Usage.TextureCoordinates | Usage.Normal;
        floorMaterial = new Material(ColorAttribute.createDiffuse(new Color(0.2f, 0.2f, 0.2f, 1f)));
        wallMaterial = new Material(ColorAttribute.createDiffuse(new Color(0.2f, 0.2f, 0.2f, 1f)));
        lineMaterial = new Material(ColorAttribute.createDiffuse(new Color(0.9f, 0.9f, 0.9f, 1f)));
        chicaneMaterial = new Material(ColorAttribute.createDiffuse(new Color(0.8f, 0.0f, 0.0f, 1f)));

        ModelBuilder modelBuilder = new ModelBuilder();
        floorModel = modelBuilder.createBox(COURSE_LENGTH, 10, 610, floorMaterial, attrs);

        floorModelInstance = new ModelInstance(floorModel);
        floorModelInstance.transform.setToTranslationAndScaling(0, -59f * SCALE, 0, SCALE, SCALE, SCALE);

        horizontalWallModel = modelBuilder.createBox(COURSE_LENGTH, 64, 10, wallMaterial, attrs);
        topWall = new ModelInstance(horizontalWallModel);
        topWall.transform.setToTranslationAndScaling(0, (32 - 59) * SCALE, - 310 * SCALE, SCALE, SCALE, SCALE);
        bottomWall = new ModelInstance(horizontalWallModel);
        bottomWall.transform.setToTranslationAndScaling(0, (32 - 59) * SCALE, 310 * SCALE, SCALE, SCALE, SCALE);

        lineModel = modelBuilder.createRect(-COURSE_LENGTH / 2, 0, -5, -COURSE_LENGTH / 2, 0, 5, COURSE_LENGTH / 2, 0, 5, COURSE_LENGTH / 2, 0, -5, 0f, 1f, 0f, lineMaterial, attrs);
        line = new ModelInstance(lineModel);
        line.transform.setToTranslationAndScaling(0, - 50 * SCALE, 0 * SCALE, SCALE, SCALE, SCALE);
        chicaneModel = createChicaneModel(modelBuilder, CHICANE_LENGTH, attrs);

        addChicane(-COURSE_LENGTH / 4, 305 - CHICANE_WIDTH / 2);
        addChicane(-COURSE_LENGTH / 4, -305 + CHICANE_WIDTH / 2);
        addChicane(COURSE_LENGTH / 4, 305 - CHICANE_WIDTH / 2);
        addChicane(COURSE_LENGTH / 4, -305 + CHICANE_WIDTH / 2);
    }

    private Model createChicaneModel(ModelBuilder modelBuilder, int length, int attributes) {
        modelBuilder.begin();
        MeshPartBuilder meshPartBuilder = modelBuilder.part("box", GL20.GL_TRIANGLES, attributes, chicaneMaterial);
        ChicaneBShapeuilder.build(meshPartBuilder, length);
        return modelBuilder.end();
    }

    private void addChicane(float x, float y) {
        ModelInstance chicane = new ModelInstance(chicaneModel);
        chicane.transform.setToTranslationAndScaling(x * SCALE, (19 - 59) * SCALE, - y * SCALE, SCALE, SCALE, SCALE);
        if (y < 0) {
            chicane.transform.rotate(1f, 0f, 0f, 180);
        }
        chicanes.add(chicane);
    }

    @Override
    public void init() {
    }

    protected ModelInstance createChallengeModelInstance(ModelFactory modelFactory) {
        Model arenaModel = modelFactory.loadModel("arena.obj");

        ModelInstance arena = new ModelInstance(arenaModel);
        arena.transform.setToTranslationAndScaling(0, -70 * SCALE, 0, SCALE, SCALE, SCALE);
        arena.materials.get(0).set(ColorAttribute.createDiffuse(new Color(0.5f, 0.1f, 0.1f, 1f)));

        return arena;
    }

    @Override
    public void dispose() {
        floorModel.dispose();
        horizontalWallModel.dispose();
        lineModel.dispose();
        // chicaneModel.dispose();
    }

    @Override
    protected void renderChallenge(ModelBatch batch, Environment environment, IntMap<VisibleObject> visibleObjects) {
        batch.render(floorModelInstance, environment);
        batch.render(topWall, environment);
        batch.render(bottomWall, environment);
        for (ModelInstance chicane : chicanes) {
            batch.render(chicane, environment);
        }
        batch.render(line, environment);
    }

    public static class ChicaneBShapeuilder extends BoxShapeBuilder {
        public static void build(MeshPartBuilder meshPartBuilder, int length) {
            float width = 38;
            float x = 0;
            float y = 0;
            float z = 0;
            final float hw = length * 0.5f;
            final float hh = width * 0.5f;
            final float hd = width * 0.5f;

            final float x0 = x - hw;
            final float y0 = y - hh;
            final float z0 = z - hd;
            final float x1 = x + hw;
            final float y1 = y + hh;
            final float z1 = z + hd;

            build(meshPartBuilder,
                    obtainV3().set(x0 - width * CUT_MODIFIER, y0, z0),
                    obtainV3().set(x0 - width * CUT_MODIFIER, y1, z0),
                    obtainV3().set(x1 + width * CUT_MODIFIER, y0, z0),
                    obtainV3().set(x1 + width * CUT_MODIFIER, y1, z0),
                    obtainV3().set(x0, y0, z1),
                    obtainV3().set(x0, y1, z1),
                    obtainV3().set(x1, y0, z1),
                    obtainV3().set(x1, y1, z1));
            freeAll();
        }
    }
}
