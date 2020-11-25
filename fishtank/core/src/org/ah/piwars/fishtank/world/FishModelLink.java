package org.ah.piwars.fishtank.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;

import org.ah.piwars.fishtank.FishtankScreen;
import org.ah.piwars.fishtank.VisibleObject;
import org.ah.piwars.fishtank.game.FishtankGame;
import org.ah.piwars.fishtank.game.fish.Fish;
import org.ah.themvsus.engine.common.game.GameObject;

public class FishModelLink implements VisibleObject {
    private int id;
    private Model fishModel;
    private ModelInstance fishModelInstance;
    private FishtankGame game;

    private Vector3 position = new Vector3();
    private Quaternion spine1Orientation = new Quaternion();
    private Quaternion spine2Orientation = new Quaternion();
    private Quaternion spine3Orientation = new Quaternion();
    private Quaternion spine4Orientation = new Quaternion();
    private Node spine1;
    private Node spine2;
    private Node spine3;
    private Node spine4;
    float t = 0;
    boolean dontMove = false;

    public FishModelLink(FishtankGame game, int id) {
        this.game = game;
        this.id = id;
    }

    public void makeObject(AssetManager assetManager) {
        fishModel = assetManager.get("fish/spadefish/spadefish.g3db", Model.class);

        BoundingBox bb = new BoundingBox();
        fishModel.calculateBoundingBox(bb);
        System.out.println("spadefish bb=" + bb);
        fishModelInstance = new ModelInstance(fishModel);

        spine1 = fishModelInstance.getNode("spine1");
        spine2 = fishModelInstance.getNode("spine2");
        spine3 = fishModelInstance.getNode("spine3");
        spine4 = fishModelInstance.getNode("spine4");
        spine1.isAnimated = true;
        spine2.isAnimated = true;
        spine3.isAnimated = true;
        spine4.isAnimated = true;
    }

    @Override
    public void render(ModelBatch batch, Environment environment) {
        if (fishModelInstance != null) {
            Fish fish = game.getCurrentGameState().get(id);
            if (fish != null) {

                final float delta = Math.min(1f / 10f, Gdx.graphics.getDeltaTime());
    //          animationController.update(delta);


                t = (t + delta * 0.02f) % 1f;

                position.set(fish.getPosition());
                position.scl(FishtankScreen.WORLD_SCALE);

                fishModelInstance.transform.idt();
                fishModelInstance.transform.set(position, fish.getOrientation());
//                fishModelInstance.transform.set(fish.getOrientation());
//                fishModelInstance.transform.setTranslation(position);
                fishModelInstance.transform.scale(FishtankScreen.WORLD_SCALE, FishtankScreen.WORLD_SCALE, FishtankScreen.WORLD_SCALE);
                fishModelInstance.transform.rotate(0f, 1f, 0f, -90);

                final float step = MathUtils.PI / 16;

                float yaw1 = 2f * MathUtils.sin(t * 200f) * 15 / MathUtils.PI;
                float yaw2 = 2f * MathUtils.sin(t * 200f + step) * 15 / MathUtils.PI;
                float yaw3 = 2f * MathUtils.sin(t * 200f + step * 2) * 15 / MathUtils.PI;
                float yaw4 = 2f * MathUtils.sin(t * 200f + step * 3) * 15 / MathUtils.PI;

                spine1Orientation.setEulerAngles(0f, 0f, yaw1).mul(spine1.rotation);
                spine2Orientation.setEulerAngles(0f, 0f, yaw2).mul(spine2.rotation);
                spine3Orientation.setEulerAngles(0f, 0f, yaw3).mul(spine3.rotation);
                spine4Orientation.setEulerAngles(0f, 0f, yaw4).mul(spine4.rotation);
                spine1.localTransform.set(spine1.translation, spine1Orientation, spine1.scale);
                spine2.localTransform.set(spine2.translation, spine2Orientation, spine2.scale);
                spine3.localTransform.set(spine3.translation, spine3Orientation, spine3.scale);
                spine4.localTransform.set(spine4.translation, spine4Orientation, spine4.scale);
                fishModelInstance.calculateTransforms();

                batch.render(fishModelInstance, environment);
            }
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public  <T extends GameObject> T getGameObject() {
        return (T)game.getCurrentGameState().get(id);
    }

    @Override
    public Color getColour() {
        return null;
    }

    @Override
    public void dispose() {
        fishModel.dispose();
        game = null;
    }
}