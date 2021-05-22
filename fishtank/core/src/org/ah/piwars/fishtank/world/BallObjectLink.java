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
import org.ah.piwars.fishtank.game.fish.BallObject;
import org.ah.themvsus.engine.common.game.GameObject;

public class BallObjectLink implements VisibleObject {
    private int id;
    private Model objectModel;
    private ModelInstance ballModelInstance;
    private FishtankGame game;

    private Vector3 position = new Vector3();
    private Quaternion orientation = new Quaternion();
    private Quaternion newOrientation = new Quaternion();
    private Quaternion tempQuaternion = new Quaternion();

    boolean dontMove = false;

    public BallObjectLink(FishtankGame game, int id) {
        this.game = game;
        this.id = id;
    }

    public void makeObject(AssetManager assetManager) {
        objectModel = assetManager.get("decorations/ball.g3db", Model.class);

        BoundingBox bb = new BoundingBox();
        objectModel.calculateBoundingBox(bb);
        System.out.println("ball " + id + " bb=" + bb);
        ballModelInstance = new ModelInstance(objectModel);
    }

    @Override
    public void render(float delta, ModelBatch batch, Environment environment) {
        if (ballModelInstance != null) {
            BallObject ball = game.getCurrentGameState().get(id);
            if (ball != null) {

                position.set(ball.getPosition());
                position.scl(FishtankScreen.WORLD_SCALE);

                float s = ball.getSize();

                orientation  = ball.getOrientation();
                newOrientation.setFromAxisRad(0f, 1f, 0f, orientation.getYawRad());
                tempQuaternion.setFromAxisRad(0f, 0f, 1f, orientation.getPitchRad());
                newOrientation.mul(tempQuaternion);

                ballModelInstance.transform.idt();
                ballModelInstance.transform.set(position, newOrientation);
                ballModelInstance.transform.scale(FishtankScreen.WORLD_SCALE * s, FishtankScreen.WORLD_SCALE * s, FishtankScreen.WORLD_SCALE * s);

                batch.render(ballModelInstance, environment);
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
