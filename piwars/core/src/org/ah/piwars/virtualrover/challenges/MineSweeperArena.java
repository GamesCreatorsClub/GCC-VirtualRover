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
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap;

import org.ah.piwars.virtualrover.ModelFactory;
import org.ah.piwars.virtualrover.ServerCommunicationAdapter;
import org.ah.piwars.virtualrover.VisibleObject;
import org.ah.piwars.virtualrover.game.MineSweeperStateObject;

import static org.ah.piwars.virtualrover.MainGame.SCALE;
import static org.ah.piwars.virtualrover.game.challenge.MineSweeperChallenge.MINE_POLYGONS;
import static org.ah.piwars.virtualrover.game.challenge.MineSweeperChallenge.WALL_HEIGHT;
import static org.ah.piwars.virtualrover.game.challenge.MineSweeperChallenge.WALL_POLYGONS;
import static org.ah.piwars.virtualrover.utils.MeshUtils.extrudePolygonY;

public class MineSweeperArena extends AbstractChallenge {

    private Material floorOffMaterial;
    private Material floorOnMaterial;
    private Array<Model> floorModels = new Array<>();
    private Array<ModelInstance> floorInstances = new Array<>();

    private Material wallMaterial;
    private Array<Model> wallModels = new Array<>();
    private Array<ModelInstance> wallInstances = new Array<>();
    private ServerCommunicationAdapter serverCommunicationAdapter;

    private static ColorAttribute FLOOR_LIT_COLOUR = ColorAttribute.createDiffuse(new Color(0.8f, 0.8f, 0.8f, 1f));
    private static ColorAttribute FLOOR_NOT_LIT_COLOUR = ColorAttribute.createDiffuse(new Color(0.5f, 0.5f, 0.5f, 1f));

    public MineSweeperArena(ModelFactory modelFactory, ServerCommunicationAdapter serverCommunicationAdapter) {
        super(modelFactory);

        this.serverCommunicationAdapter = serverCommunicationAdapter;

        int attrs = Usage.Position | Usage.ColorUnpacked  | Usage.TextureCoordinates | Usage.Normal;
        floorOffMaterial = new Material(FLOOR_NOT_LIT_COLOUR);
        floorOnMaterial = new Material(FLOOR_LIT_COLOUR);
        wallMaterial = new Material(ColorAttribute.createDiffuse(new Color(0.2f, 0.2f, 0.2f, 1f)));

        ModelBuilder modelBuilder = new ModelBuilder();

        for (Polygon floorPolygon : MINE_POLYGONS) {
            Model floorModel = extrudePolygonY(modelBuilder, floorPolygon, 10, attrs, floorOffMaterial);
            ModelInstance floorModelInstance = new ModelInstance(floorModel);
            floorModelInstance.transform.setToTranslationAndScaling(0, (- 59) * SCALE, 0, SCALE, SCALE, SCALE);
            floorModels.add(floorModel);
            floorInstances.add(floorModelInstance);
        }

        for (Polygon wallPolygon : WALL_POLYGONS) {
            Model wallModel = extrudePolygonY(modelBuilder, wallPolygon, WALL_HEIGHT, attrs, wallMaterial);
            ModelInstance wallInstance = new ModelInstance(wallModel);
            wallInstance.transform.setToTranslationAndScaling(0, (WALL_HEIGHT / 2 - 59) * SCALE, 0, SCALE, SCALE, SCALE);
            wallModels.add(wallModel);
            wallInstances.add(wallInstance);
        }
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
        for (Model floorModel : floorModels) {
            floorModel.dispose();
        }
        for (Model wallModel : wallModels) {
            wallModel.dispose();
        }
    }

    @Override
    protected void renderChallenge(ModelBatch batch, Environment environment, IntMap<VisibleObject> visibleObjects) {
        int bits = 0;
        MineSweeperStateObject mineSweeperStateObject = serverCommunicationAdapter.getMineSweeperStateObject();
        if (mineSweeperStateObject != null) {
            bits = mineSweeperStateObject.getStateBits();
        }
        int bit = 1;
        for (ModelInstance floor : floorInstances) {
            if ((bits & bit) != 0) {
                floor.materials.get(0).set(FLOOR_LIT_COLOUR);
            } else {
                floor.materials.get(0).set(FLOOR_NOT_LIT_COLOUR);
            }
            batch.render(floor, environment);
            bit = bit << 1;
        }
        for (ModelInstance wall : wallInstances) {
            batch.render(wall, environment);
        }
    }
}
