package org.ah.piwars.virtualrover.challenges;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.utils.IntMap;

import org.ah.piwars.virtualrover.VisibleObject;
import org.ah.piwars.virtualrover.game.challenge.EcoDisasterChallenge;

import static org.ah.piwars.virtualrover.MainGame.SCALE;
import static org.ah.piwars.virtualrover.game.challenge.EcoDisasterChallenge.CHALLENGE_WIDTH;

public class EcoDisasterArena extends AbstractChallenge {

//    private OrthographicCamera floorCamera;

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

        zoneModel = assetManager.get("3d/challenges/eco-disaster-zone.obj");;
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
    public void render(ModelBatch batch, Environment environment, FrameBuffer otherFrameBuffer, IntMap<VisibleObject> visibleObjects) {

        IntMap<VisibleObject> newMap = new IntMap<VisibleObject>();
        newMap.putAll(visibleObjects);
        newMap.putAll(localVisibleObjects);

        super.render(batch, environment, otherFrameBuffer, newMap);
    }

    @Override
    protected void renderChallenge(ModelBatch batch, Environment environment, IntMap<VisibleObject> visibleObjects) {
        batch.render(arenaModelInstance, environment);
        batch.render(clearZoneModelInstance, environment);
        batch.render(contaminatedZoneModelInstance, environment);

        if (showPlan) { batch.render(debugFloorModelInstance); }
    }
}
