package org.ah.piwars.virtualrover.challenges;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap;

import org.ah.piwars.virtualrover.VisibleObject;

import static org.ah.piwars.virtualrover.MainGame.SCALE;
import static org.ah.piwars.virtualrover.game.challenge.TidyUpTheToysChallenge.CHALLENGE_WIDTH;
import static org.ah.piwars.virtualrover.game.challenge.TidyUpTheToysChallenge.FLOOR_POLYGON;
import static org.ah.piwars.virtualrover.game.challenge.TidyUpTheToysChallenge.WALL_HEIGHT;
import static org.ah.piwars.virtualrover.game.challenge.TidyUpTheToysChallenge.WALL_POLYGONS;
import static org.ah.piwars.virtualrover.utils.MeshUtils.extrudePolygonY;

public class FeedTheFishArena extends AbstractChallenge {

    private Model floorModel;
    private ModelInstance floorModelInstance;
    private Material floorMaterial;

    private Material wallMaterial;

    // private ModelInstance targetBoxModelInstance;

    private Array<Model> wallModels = new Array<Model>();
    private Array<ModelInstance> wallInstances = new Array<>();

    private IntMap<VisibleObject> localVisibleObjects = new IntMap<>();

    public FeedTheFishArena(AssetManager assetManager) {
        super(assetManager);

        setDimensions(CHALLENGE_WIDTH, CHALLENGE_WIDTH);
    }

    @Override
    public void init() {
        setDimensions(CHALLENGE_WIDTH, CHALLENGE_WIDTH);

        int attrs = Usage.Position | Usage.ColorUnpacked  | Usage.TextureCoordinates | Usage.Normal;
        floorMaterial = new Material(ColorAttribute.createDiffuse(new Color(0.8f, 0.8f, 0.7f, 1f)));
        wallMaterial = new Material(ColorAttribute.createDiffuse(new Color(0.8f, 0.8f, 0.7f, 1f)));

        ModelBuilder modelBuilder = new ModelBuilder();
        floorModel = modelBuilder.createBox(3400, 10, 1830, floorMaterial, attrs);
        floorModel = extrudePolygonY(modelBuilder, FLOOR_POLYGON, 10, attrs, floorMaterial);

        floorModelInstance = new ModelInstance(floorModel);
        floorModelInstance.transform.setToTranslationAndScaling(0, -59f * SCALE, 0, SCALE, SCALE, SCALE);

        for (Polygon wallPolygon : WALL_POLYGONS) {
            Model wallModel = extrudePolygonY(modelBuilder, wallPolygon, WALL_HEIGHT, attrs, wallMaterial);
            ModelInstance wallInstance = new ModelInstance(wallModel);
            wallInstance.transform.setToTranslationAndScaling(0, (WALL_HEIGHT / 2 - 59) * SCALE, 0, SCALE, SCALE, SCALE);
            wallModels.add(wallModel);
            wallInstances.add(wallInstance);
        }
    }

    @Override
    public void dispose() {
        floorModel.dispose();
        for (Model wallModel : wallModels) {
            wallModel.dispose();
        }
        for (VisibleObject localVisibleObject : localVisibleObjects.values()) {
            localVisibleObject.dispose();
        }
    }

    @Override
    public void render(ModelBatch batch, Environment environment, FrameBuffer frameBuffer, IntMap<VisibleObject> visibleObjects) {

        IntMap<VisibleObject> newMap = new IntMap<VisibleObject>();
        newMap.putAll(visibleObjects);
        newMap.putAll(localVisibleObjects);

        super.render(batch, environment, frameBuffer, newMap);
    }

    @Override
    protected void renderChallenge(ModelBatch batch, Environment environment, IntMap<VisibleObject> visibleObjects) {
        batch.render(floorModelInstance, environment);
        for (ModelInstance wall : wallInstances) {
            batch.render(wall, environment);
        }
    }
}
