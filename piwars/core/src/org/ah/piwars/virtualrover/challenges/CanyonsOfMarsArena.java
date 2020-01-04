package org.ah.piwars.virtualrover.challenges;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap;

import org.ah.piwars.virtualrover.ModelFactory;
import org.ah.piwars.virtualrover.VisibleObject;

import static org.ah.piwars.virtualrover.MainGame.SCALE;

public class CanyonsOfMarsArena extends AbstractChallenge {

    // private Mesh floorMesh;
    private Model floorModel;
    private ModelInstance floorModelInstance;
    private Material floorMaterial;

    // private Mesh normalWallMesh;
    private Material normalWallMaterial;
    private Model normalVerticalWallModel;
    private Model normalHorizontalWallModel;
    private Array<ModelInstance> walls = new Array<>();

    public CanyonsOfMarsArena(ModelFactory modelFactory) {
        super(modelFactory);

//        int primitiveType = GL20.GL_TRIANGLES;
        int attrs = Usage.Position | Usage.ColorUnpacked  | Usage.TextureCoordinates | Usage.Normal;
        floorMaterial = new Material(ColorAttribute.createDiffuse(new Color(0.3f, 0.3f, 0.3f, 1f)));
        normalWallMaterial = new Material(ColorAttribute.createDiffuse(new Color(0.8f, 0.0f, 0.0f, 1f)));

//        floorMesh = createRect(-1700 * SCALE, -915 * SCALE, -59f * SCALE, 1000 * SCALE, 1000 * SCALE, new Color(1f, 1f, 1f, 1f));
//        normalWallMesh = createRect(0, 0, -59f * SCALE, 1000 * SCALE, 1000 * SCALE, new Color(1f, 1f, 1f, 1f));

        ModelBuilder modelBuilder = new ModelBuilder();
        floorModel = modelBuilder.createBox(3400, 10, 1830, floorMaterial, attrs);

        floorModelInstance = new ModelInstance(floorModel);
        floorModelInstance.transform.setToTranslationAndScaling(0, -59f * SCALE, 0, SCALE, SCALE, SCALE);

        modelBuilder = new ModelBuilder();
        normalVerticalWallModel = modelBuilder.createBox(10, 200, 610, normalWallMaterial, attrs);

        modelBuilder = new ModelBuilder();
        normalHorizontalWallModel = modelBuilder.createBox(680, 200, 10, normalWallMaterial, attrs);

        addVerticalWall(1695, -610);
        addVerticalWall(1695, 0);
        addVerticalWall(1695, 610);
        addHorizontalWall(1360, 910);
        addHorizontalWall(680, 910);
        addHorizontalWall(0, 910);
        addHorizontalWall(-680, 910);
        addHorizontalWall(-1360, 910);
        addVerticalWall(-1695, 610);
        addVerticalWall(-1695, 0);
        addVerticalWall(-1695, -610);
        addHorizontalWall(-1360, -910);
        addHorizontalWall(-680, -910);
        addHorizontalWall(0, -910);

        addVerticalWall(1010, -610);
        addVerticalWall(1010, 0);
        addHorizontalWall(680, 305);
        addVerticalWall(340, 0);
        addHorizontalWall(0, -305);
        addHorizontalWall(-680, -305);
        addVerticalWall(-1010, 0);

        addVerticalWall(-340, 610);
    }

    private void addVerticalWall(float x, float y) {
        ModelInstance wall = new ModelInstance(normalVerticalWallModel);
        wall.transform.setToTranslationAndScaling(x * SCALE, (100 - 59) * SCALE, - y * SCALE, SCALE, SCALE, SCALE);
        walls.add(wall);
    }

    private void addHorizontalWall(float x, float y) {
        ModelInstance wall = new ModelInstance(normalHorizontalWallModel);
        wall.transform.setToTranslationAndScaling(x * SCALE, (100 - 59) * SCALE, - y * SCALE, SCALE, SCALE, SCALE);
        walls.add(wall);
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
        // floorMesh.dispose();
        normalVerticalWallModel.dispose();
        normalHorizontalWallModel.dispose();
        // normalWallMesh.dispose();
    }

    @Override
    protected void renderChallenge(ModelBatch batch, Environment environment, IntMap<VisibleObject> visibleObjects) {
        batch.render(floorModelInstance, environment);
        for (ModelInstance wall : walls) {
            batch.render(wall, environment);
        }
    }
}
