package org.ah.piwars.virtualrover.challenges.feedthefish;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap;

import org.ah.piwars.virtualrover.VisibleObject;
import org.ah.piwars.virtualrover.challenges.AbstractChallenge;
import org.ah.piwars.virtualrover.screens.RenderingContext;

import static org.ah.piwars.virtualrover.MainGame.SCALE;
import static org.ah.piwars.virtualrover.game.challenge.FeedTheFishChallenge.CHALLENGE_WIDTH;
import static org.ah.piwars.virtualrover.game.challenge.FeedTheFishChallenge.FLOOR_POLYGON;
import static org.ah.piwars.virtualrover.game.challenge.FeedTheFishChallenge.WALL_HEIGHT;
import static org.ah.piwars.virtualrover.game.challenge.FeedTheFishChallenge.WALL_POLYGONS;
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
        floorMaterial = new Material(ColorAttribute.createDiffuse(new Color(0.6f, 0.6f, 0.55f, 1f)));
        wallMaterial = new Material(ColorAttribute.createDiffuse(new Color(0.6f, 0.6f, 0.55f, 1f)));

        ModelBuilder modelBuilder = new ModelBuilder();
        //floorModel = modelBuilder.createBox(3400, 10, 1830, floorMaterial, attrs);
        floorModel = extrudePolygonY(modelBuilder, FLOOR_POLYGON, 10, attrs, floorMaterial);

        float downOffset = 61f;

        floorModelInstance = new ModelInstance(floorModel);
        floorModelInstance.transform.setToTranslationAndScaling(0, -downOffset * SCALE, 0, SCALE, SCALE, SCALE);

        for (Polygon wallPolygon : WALL_POLYGONS) {
            Model wallModel = extrudePolygonY(modelBuilder, wallPolygon, WALL_HEIGHT, attrs, wallMaterial);
            ModelInstance wallInstance = new ModelInstance(wallModel);
            wallInstance.transform.setToTranslationAndScaling(0, (WALL_HEIGHT / 2 - downOffset) * SCALE, 0, SCALE, SCALE, SCALE);
            wallModels.add(wallModel);
            wallInstances.add(wallInstance);
        }

        prepareDebugAssets();
    }

    @Override protected boolean debugBox2D() { return true; }
    @Override protected int getChallengeWidth() { return (int)CHALLENGE_WIDTH; }
    @Override protected int getChallengeHeight() { return (int)CHALLENGE_WIDTH; }
    @Override protected float getFloorHeight() { return -59f; }

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
    public void render(RenderingContext renderingContext, IntMap<VisibleObject> visibleObjects) {

        IntMap<VisibleObject> newMap = new IntMap<VisibleObject>();
        newMap.putAll(visibleObjects);
        newMap.putAll(localVisibleObjects);

        super.render(renderingContext, newMap);
    }

    @Override
    protected void renderChallenge(RenderingContext renderingContext, IntMap<VisibleObject> visibleObjects) {
        if (!renderingContext.showPlan) { renderingContext.modelBatch.render(floorModelInstance, renderingContext.environment); }
        for (ModelInstance wall : wallInstances) {
            renderingContext.modelBatch.render(wall, renderingContext.environment);
        }

        if (renderingContext.showPlan) { renderingContext.modelBatch.render(debugFloorModelInstance); }
    }
}
