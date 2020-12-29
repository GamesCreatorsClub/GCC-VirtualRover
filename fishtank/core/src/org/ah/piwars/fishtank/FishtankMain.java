package org.ah.piwars.fishtank;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.model.Node;

import org.ah.piwars.fishtank.screens.ConnectingScreen;
import org.ah.piwars.fishtank.screens.LoadingScreen;
import org.ah.piwars.fishtank.view.Console;
import org.ah.themvsus.engine.client.ServerCommunication;

public class FishtankMain extends Game {

    // public static final String TANK_BOTTOM_MODEL = "tank/tank-2-150.g3db";
    // public static final String TANK_BOTTOM_MODEL = "tank/tank-1-75.g3db";
    // public static final String TANK_BOTTOM_MODEL = "tank/tank-1-100.g3db";
    public static final String TANK_BOTTOM_MODEL = "tank/tank-2-100.g3db";

    private PlatformSpecific platformSpecific;

    private ServerCommunication serverCommunication;
    private ServerCommunicationAdapter serverCommunicationAdapter;

    private AssetManager assetManager;

    private Console console;

    private LoadingScreen loadingScreen;
    private ConnectingScreen connectingScreen;
    private FishtankScreen tankScreen;

    public FishtankMain(PlatformSpecific platformSpecific) {
        this.platformSpecific = platformSpecific;
    }

    @Override
    public void create() {

        assetManager = new AssetManager();

        assetManager.load("GCC_full.png", Texture.class);
        assetManager.load("PiWarsLogo-small.png", Texture.class);
        assetManager.load("font/basic.fnt", BitmapFont.class);
        assetManager.load("font/copper18.fnt", BitmapFont.class);
        assetManager.load("font/droidsansmono-15.fnt", BitmapFont.class);

        assetManager.load("fish/spadefish/spadefish.g3db", Model.class);
        assetManager.load("fish/tetra/tetra.g3db", Model.class);

        assetManager.load(TANK_BOTTOM_MODEL, Model.class);
//        assetManager.load("Pebbles_025_BaseColor.jpg", Texture.class);
//        assetManager.load("Pebbles_025_Normal.jpg", Texture.class);

        serverCommunication = platformSpecific.getServerCommunication();

        loadingScreen = new LoadingScreen(this, assetManager);

        setScreen(loadingScreen);
    }

    public void finishedLoading() {
        String timestamp = "#202011021618";

        String version = "v.0.8";

        if (timestamp.startsWith("#")) {
            version = version + " (" + timestamp.substring(1) + ")";
        }

        console = new Console();
        console.raw("Welcome to Virtual PiWars " + version);
        console.raw("Games Creators Club Virtual Rover");
        console.raw("(c) Creative Sphere Limited");

//        Model fishModel = assetManager.get("fish/spadefish/spadefish.g3db", Model.class);
        applyWorldScale(assetManager.get("fish/spadefish/spadefish.g3db", Model.class));
        applyWorldScale(assetManager.get("fish/tetra/tetra.g3db", Model.class));

        serverCommunicationAdapter = new ServerCommunicationAdapter(serverCommunication, console, assetManager);

        tankScreen = new FishtankScreen(platformSpecific, assetManager, console, serverCommunicationAdapter);
        connectingScreen = new ConnectingScreen(serverCommunicationAdapter, this, assetManager);

        tankScreen.create();

        connectingScreen.reset();
        serverCommunicationAdapter.connectToServer(
                platformSpecific.getServerAddress(),
                platformSpecific.getServerPort(),
                connectingScreen);
        setScreen(connectingScreen);
    }

    public void showTank() {
        setScreen(tankScreen);
    }

    @Override
    public void dispose() {
        assetManager.dispose();
        if (console != null) { console.dispose(); }
        if (loadingScreen != null) { loadingScreen.dispose(); }
        if (tankScreen != null) { tankScreen.dispose(); }
    }

    private void applyWorldScale(Model fishModel) {
        for (Node node : fishModel.nodes) {
          node.scale.scl(FishtankScreen.WORLD_SCALE, FishtankScreen.WORLD_SCALE, FishtankScreen.WORLD_SCALE);
          node.translation.setZero();
          node.calculateTransforms(true);
      }
    }
}
