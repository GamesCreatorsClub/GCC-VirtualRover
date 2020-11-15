package org.ah.piwars.fishtank.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;

import org.ah.piwars.fishtank.FishtankMain;

public class LoadingScreen extends ScreenAdapter {

    private FishtankMain game;
    private AssetManager assetManager;
    public LoadingScreen(FishtankMain game, AssetManager assetManager) {

        this.game = game;
        this.assetManager = assetManager;
    }

    @Override
    public void render(float delta) {
        if (assetManager.update()) {
            game.finishedLoading();
        } else {
            float progress = assetManager.getProgress();
            Gdx.gl.glClearColor(progress, progress, progress, 1f);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        }
    }
}
