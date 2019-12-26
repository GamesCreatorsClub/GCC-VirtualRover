package org.ah.gcc.virtualrover.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;

import org.ah.gcc.virtualrover.MainGame;
import org.ah.themvsus.engine.client.ServerCommunication.ServerConnectionCallback;

public class ConnectingScreen extends ScreenAdapter implements ServerConnectionCallback {

    private MainGame game;
    @SuppressWarnings("unused")
    private AssetManager assetManager;

    private boolean connected = false;
    private boolean error = false;

    public ConnectingScreen(MainGame game, AssetManager assetManager) {
        this.game = game;
        this.assetManager = assetManager;
    }

    public void clear() {
        connected = false;
        error = false;
    }

    @Override
    public void render(float delta) {
        if (connected) {
            game.successfullyConnected();
        } else if (error) {
            Gdx.gl.glClearColor(1f, 0f, 0f, 1f);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        } else {
            Gdx.gl.glClearColor(1f, 0.5f, 0f, 1f);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        }
    }

    @Override
    public void successful() {
        connected = true;
    }

    @Override
    public void failed(String msg) {
    }
}
