package org.ah.piwars.fishtank.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;

import org.ah.piwars.fishtank.FishtankMain;
import org.ah.piwars.fishtank.ServerCommunicationAdapter;
import org.ah.themvsus.engine.client.CommonServerCommunicationAdapter.AuthenticatedCallback;
import org.ah.themvsus.engine.client.CommonServerCommunicationAdapter.GameMapCallback;
import org.ah.themvsus.engine.client.CommonServerCommunicationAdapter.GameReadyCallback;
import org.ah.themvsus.engine.client.ServerCommunication.ServerConnectionCallback;

public class ConnectingScreen extends ScreenAdapter implements ServerConnectionCallback, GameReadyCallback, GameMapCallback, AuthenticatedCallback {

    private ServerCommunicationAdapter serverCommunicationAdapter;

    private FishtankMain game;
    @SuppressWarnings("unused")
    private AssetManager assetManager;

    private boolean connected = false;
    private boolean error = false;
    private boolean gameReady;

    public ConnectingScreen(ServerCommunicationAdapter serverCommunicationAdapter, FishtankMain game, AssetManager assetManager) {
        this.serverCommunicationAdapter = serverCommunicationAdapter;
        this.game = game;
        this.assetManager = assetManager;

        serverCommunicationAdapter.setGameMapCallback(this);
        serverCommunicationAdapter.setAuthenticatedCallback(this);
    }

    public void reset() {
        gameReady = false;
        connected = false;
        error = false;
    }

    @Override
    public void render(float delta) {
        if (gameReady) {
            game.showTank();
        } else if (connected) {
            Gdx.gl.glClearColor(0.8f, 1f, 0.8f, 1f);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
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
        serverCommunicationAdapter.authenticate("", "");
    }

    @Override
    public void failed(String msg) {
    }

    @Override
    public void gameMap(String mapId, int playerId) {
        this.gameReady = true;
    }

    @Override
    public void gameReady() {
        this.gameReady = true;
    }

    @Override
    public void authenticated() {
        serverCommunicationAdapter.sendJoinGame("");
    }
}
