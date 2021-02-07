package org.ah.piwars.fishtank.world;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;

import org.ah.piwars.fishtank.VisibleObject;
import org.ah.piwars.fishtank.WiiMoteCameraController;
import org.ah.piwars.fishtank.game.CameraPositionObject;
import org.ah.piwars.fishtank.game.FishtankGame;
import org.ah.piwars.fishtank.input.FishtankPlayerInput;
import org.ah.themvsus.engine.common.game.GameObject;

public class CameraPositionLink implements VisibleObject {
    private int id;
    private Model fishModel;
    private FishtankGame game;
    private FishtankPlayerInput cameraInput = (FishtankPlayerInput)FishtankPlayerInput.INPUTS_FACTORY.obtain(); // TODO - is that OK? Why not set of inputs?

    float t = 0;
    boolean dontMove = false;

    public CameraPositionLink(FishtankGame game, int id) {
        this.game = game;
        this.id = id;
    }

    public void makeObject(AssetManager assetManager) {
    }

    @Override
    public void render(float delta, ModelBatch batch, Environment environment) {
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

    public FishtankPlayerInput getPlayerInput() {
        return cameraInput;
    }

    public void updateFrom(WiiMoteCameraController wiiMoteCameraController) {
        // TODO calculate distanc better
        cameraInput.camX(wiiMoteCameraController.getInputX());
        cameraInput.camY(wiiMoteCameraController.getInputY());
        cameraInput.camZ(500f);
    }

    public void updateTo(WiiMoteCameraController wiiMoteCameraController) {
        CameraPositionObject cameraPositionObject = getGameObject();
        if (cameraPositionObject != null) {
            float x = cameraPositionObject.getPosition().x;
            float y = cameraPositionObject.getPosition().y;
            // TODO Fetch z and calculate x and y properly!
            wiiMoteCameraController.setCamPosition(x, y);
        }
    }
}
