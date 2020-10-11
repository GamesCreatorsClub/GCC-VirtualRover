package org.ah.piwars.virtualrover.challenges.ecodisaster;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.utils.IntMap;

import org.ah.piwars.virtualrover.VisibleObject;
import org.ah.piwars.virtualrover.challenges.AbstractChallenge;
import org.ah.piwars.virtualrover.game.challenge.EcoDisasterChallenge;
import org.ah.piwars.virtualrover.screens.RenderingContext;

import static org.ah.piwars.virtualrover.MainGame.SCALE;
import static org.ah.piwars.virtualrover.game.challenge.EcoDisasterChallenge.CHALLENGE_WIDTH;

public class EcoDisasterArena extends AbstractChallenge {

    private Model arenaModel;
    private Model zoneModel;

    private ModelInstance arenaModelInstance;
    private ModelInstance clearZoneModelInstance;
    private ModelInstance contaminatedZoneModelInstance;

    public EcoDisasterChallenge ecoDisasterChallenge;

    protected IntMap<VisibleObject> localVisibleObjects = new IntMap<>();

    public EcoDisasterArena(AssetManager assetManager) {
        super(assetManager);

        setDimensions(CHALLENGE_WIDTH, CHALLENGE_WIDTH);
    }

    @Override
    public void init() {
        arenaModel = assetManager.get("3d/challenges/eco-disaster-arena.obj");
        arenaModelInstance = new ModelInstance(arenaModel);

        zoneModel = assetManager.get("3d/challenges/eco-disaster-zone.obj");
        clearZoneModelInstance = new ModelInstance(zoneModel);
        contaminatedZoneModelInstance = new ModelInstance(zoneModel);

        arenaModelInstance.transform.setToTranslationAndScaling(0, 250 * SCALE, 0, SCALE, SCALE, SCALE);
        arenaModelInstance.materials.get(0).set(ColorAttribute.createDiffuse(new Color(0.5f, 0.5f, 0.5f, 1f)));

        clearZoneModelInstance.transform.setToTranslationAndScaling(-400 * SCALE, 250 * SCALE, -1000 * SCALE, SCALE, SCALE, SCALE);
        clearZoneModelInstance.materials.get(0).set(ColorAttribute.createDiffuse(new Color(0.0f, 0.0f, 0.8f, 1f)));

        contaminatedZoneModelInstance.transform.setToTranslationAndScaling(400 * SCALE, 250 * SCALE, -1000 * SCALE, SCALE, SCALE, SCALE);
        contaminatedZoneModelInstance.materials.get(0).set(ColorAttribute.createDiffuse(new Color(0.8f, 0.8f, 0.0f, 1f)));

        prepareDebugAssets();
    }

    @Override protected boolean debugBox2D() { return true; }
    @Override protected int getChallengeWidth() { return (int)CHALLENGE_WIDTH; }
    @Override protected int getChallengeHeight() { return (int)CHALLENGE_WIDTH; }
    @Override protected float getFloorHeight() { return -54f; }


    @Override
    public void dispose() {
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
        renderingContext.modelBatch.render(arenaModelInstance, renderingContext.environment);
        renderingContext.modelBatch.render(clearZoneModelInstance, renderingContext.environment);
        renderingContext.modelBatch.render(contaminatedZoneModelInstance, renderingContext.environment);

        if (renderingContext.showPlan) { renderingContext.modelBatch.render(debugFloorModelInstance); }
    }
}
