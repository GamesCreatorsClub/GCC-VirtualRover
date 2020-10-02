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
import com.badlogic.gdx.utils.IntMap;

import org.ah.piwars.virtualrover.VisibleObject;

import static org.ah.piwars.virtualrover.MainGame.SCALE;
import static org.ah.piwars.virtualrover.game.challenge.TidyUpTheToysChallenge.CHALLENGE_WIDTH;

public class TidyUpTheToysArena extends AbstractChallenge {

    private Model arenaModel;
    private Model zoneModel;

    private Material zoneMaterial;
    private ModelInstance arenaModelInstance;
    private ModelInstance zoneModelInstance;
    private IntMap<VisibleObject> localVisibleObjects = new IntMap<>();

    public TidyUpTheToysArena(AssetManager assetManager) {
        super(assetManager);

        setDimensions(CHALLENGE_WIDTH, CHALLENGE_WIDTH);
    }

    @Override
    public void init() {
        setDimensions(CHALLENGE_WIDTH, CHALLENGE_WIDTH);

        arenaModel = assetManager.get("3d/challenges/tidy-up-the-toys-arena.obj");
        //arenaModel = assetManager.get("3d/challenges/feed-the-fish-tower.obj");
        arenaModelInstance = new ModelInstance(arenaModel);

        int attrs = Usage.Position | Usage.ColorUnpacked  | Usage.TextureCoordinates | Usage.Normal;
        zoneMaterial = new Material(ColorAttribute.createDiffuse(new Color(0.9f, 0.9f, 0.8f, 1f)));

        ModelBuilder modelBuilder = new ModelBuilder();
        zoneModel = modelBuilder.createBox(150, 5, 350, zoneMaterial, attrs);

        arenaModelInstance.transform.setToTranslationAndScaling(0, 143.5f * SCALE, 0, SCALE, SCALE, SCALE);
        arenaModelInstance.materials.get(0).set(ColorAttribute.createDiffuse(new Color(0.6f, 0.6f, 0.55f, 1f)));

        zoneModelInstance = new ModelInstance(zoneModel);
        zoneModelInstance.transform.setToTranslationAndScaling(-675 * SCALE, -59f * SCALE, +125 * SCALE, SCALE, SCALE, SCALE);

        prepareDebugAssets();
    }

    @Override protected boolean debugBox2D() { return true; }
    @Override protected int getChallengeWidth() { return (int)CHALLENGE_WIDTH; }
    @Override protected int getChallengeHeight() { return (int)CHALLENGE_WIDTH; }
    @Override protected float getFloorHeight() { return -55f; }

    @Override
    public void dispose() {
        zoneModel.dispose();

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
        // if (!showPlan) { batch.render(zoneModelInstance, environment); }

        batch.render(arenaModelInstance, environment);
        batch.render(zoneModelInstance, environment);

        if (showPlan) { batch.render(debugFloorModelInstance); }
    }
}
