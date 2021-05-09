package org.ah.piwars.fishtank.world;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;

import org.ah.piwars.fishtank.FishtankScreen;
import org.ah.piwars.fishtank.VisibleObject;
import org.ah.piwars.fishtank.game.FishtankGame;
import org.ah.piwars.fishtank.game.FishtankGameTypeObject;
import org.ah.themvsus.engine.common.game.GameObject;
import org.ah.themvsus.engine.common.game.GameObjectWithPositionAndOrientation;

public class StaticObjectLink implements VisibleObject {
    private int id;
    private Model objectModel;
    private ModelInstance fishModelInstance;
    private FishtankGame game;

    private Vector3 position = new Vector3();
    private Quaternion orientation = new Quaternion();
    private Quaternion newOrientation = new Quaternion();
    private Quaternion tempQuaternion = new Quaternion();

    boolean dontMove = false;
    private FishtankGameTypeObject objectType;

    public StaticObjectLink(FishtankGame game, int id, FishtankGameTypeObject objectType) {
        this.game = game;
        this.id = id;
        this.objectType = objectType;
    }

    public void makeObject(AssetManager assetManager) {
        if (objectType == FishtankGameTypeObject.Anchor) {
            objectModel = assetManager.get("decorations/anchor.g3db", Model.class);
        } else if (objectType == FishtankGameTypeObject.Tresure) {
            objectModel = assetManager.get("decorations/tresure.g3db", Model.class);
        } else if (objectType == FishtankGameTypeObject.Benchy) {
            objectModel = assetManager.get("decorations/benchy.g3db", Model.class);
        }

        BoundingBox bb = new BoundingBox();
        objectModel.calculateBoundingBox(bb);
        System.out.println("anchor bb=" + bb);
        fishModelInstance = new ModelInstance(objectModel);
    }

    @Override
    public void render(float delta, ModelBatch batch, Environment environment) {
        if (fishModelInstance != null) {
            GameObjectWithPositionAndOrientation staticObject = game.getCurrentGameState().get(id);
            if (staticObject != null) {

                position.set(staticObject.getPosition());
                position.scl(FishtankScreen.WORLD_SCALE);

                orientation  = staticObject.getOrientation();
                newOrientation.setFromAxisRad(0f, 1f, 0f, orientation.getYawRad());
                tempQuaternion.setFromAxisRad(0f, 0f, 1f, orientation.getPitchRad());
                newOrientation.mul(tempQuaternion);

                fishModelInstance.transform.idt();
                fishModelInstance.transform.set(position, newOrientation);
                fishModelInstance.transform.scale(FishtankScreen.WORLD_SCALE, FishtankScreen.WORLD_SCALE, FishtankScreen.WORLD_SCALE);
                fishModelInstance.transform.rotate(0f, 1f, 0f, -90);

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
        objectModel.dispose();
        game = null;
    }
}
