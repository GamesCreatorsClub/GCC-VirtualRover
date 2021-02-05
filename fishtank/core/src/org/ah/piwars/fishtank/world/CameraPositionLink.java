package org.ah.piwars.fishtank.world;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;

import org.ah.piwars.fishtank.VisibleObject;
import org.ah.piwars.fishtank.game.FishtankGame;
import org.ah.themvsus.engine.common.game.GameObject;

public class CameraPositionLink implements VisibleObject {
    private int id;
    private Model fishModel;
    private FishtankGame game;

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
}
