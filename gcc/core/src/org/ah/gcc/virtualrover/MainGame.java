package org.ah.gcc.virtualrover;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

import org.ah.gcc.virtualrover.screens.ConnectingScreen;
import org.ah.gcc.virtualrover.screens.EcoDisasterScreen;
import org.ah.gcc.virtualrover.screens.LoadingScreen;
import org.ah.gcc.virtualrover.screens.PiNoonScreen;
import org.ah.gcc.virtualrover.utils.SoundManager;
import org.ah.gcc.virtualrover.view.Console;
import org.ah.themvsus.engine.client.ServerCommunication;

public class MainGame extends Game {

    public static final float SCALE = 0.0015f;

    private PlatformSpecific platformSpecific;

    private ServerCommunication serverCommunication;
    private ServerCommunicationAdapter serverCommunicationAdapter;

    private AssetManager assetManager;
    private SoundManager soundManager;
    private ModelFactory modelFactory;

    private Console console;

    private LoadingScreen loadingScreen;
    private ConnectingScreen connectingScreen;
    private GreetingScreen greetingScreen;
    private Screen challengeScreen;


    public MainGame(PlatformSpecific platformSpecific) {
        this.platformSpecific = platformSpecific;
    }

    @Override
    public void create() {

        assetManager = new AssetManager();

        assetManager.load("GCC_full.png", Texture.class);
        assetManager.load("font/basic.fnt", BitmapFont.class);
        assetManager.load("font/copper18.fnt", BitmapFont.class);
        // assetManager.load("font/droidsansmono-8.fnt", BitmapFont.class);
        assetManager.load("font/droidsansmono-15.fnt", BitmapFont.class);
        // assetManager.load("font/font18.fnt", BitmapFont.class);
        // assetManager.load("font/font24.fnt", BitmapFont.class);
        // assetManager.load("font/font32.fnt", BitmapFont.class);

        soundManager = new SoundManager(!platformSpecific.hasSound());
        soundManager.requestAssets(assetManager);

        modelFactory = new ModelFactory();
        modelFactory.load();

        serverCommunication = platformSpecific.getServerCommunication();

        loadingScreen = new LoadingScreen(this, assetManager);
        connectingScreen = new ConnectingScreen(this, assetManager);

        setScreen(loadingScreen);
    }

    public void finishedLoading() {
        console = new Console();
        console.raw("Welcome to PiWars Virtual PiNoon v.0.6");
        console.raw("Games Creators Club (GCC) Virtual Rover");
        console.raw("(c) Creative Sphere Limited");

        soundManager.fetchSounds(assetManager);
        serverCommunicationAdapter = new ServerCommunicationAdapter(serverCommunication, console, modelFactory);

        if (platformSpecific.hasServerDetails() && platformSpecific.isSimulation()) {
            setScreen(connectingScreen);
            connectingScreen.clear();
            serverCommunicationAdapter.connectToServer(
                    platformSpecific.getPreferredServerAddress(),
                    platformSpecific.getPreferredServerPort(),
                    connectingScreen);
            setScreen(connectingScreen);
        } else if (platformSpecific.isLocalOnly()) {
            String requestedChallenge = platformSpecific.getRequestedChallenge();
            if (requestedChallenge == null || "".equals(requestedChallenge)) {
                requestedChallenge = "PiNoon";
            }
            serverCommunicationAdapter.startEngine(requestedChallenge, true, platformSpecific.isSimulation());
            setChallengeScreen(requestedChallenge);
        } else {
            greetingScreen = new GreetingScreen(this, platformSpecific, assetManager, soundManager, modelFactory, serverCommunicationAdapter, console);
//            if (platformSpecific.hasServerDetails() && platformSpecific.isSimulation()) {
//                greetingScreen.connectToServer(platformSpecific.getPreferredServerAddress(), platformSpecific.getPreferredServerPort());
//            }
            greetingScreen.reset();
            setScreen(greetingScreen);
        }
    }

    public void setChallengeScreen(String mapId) {
        Gdx.input.setOnscreenKeyboardVisible(false);

        if ("PiNoon".equals(mapId)) {
            challengeScreen = new PiNoonScreen(this, platformSpecific, assetManager, soundManager, modelFactory, serverCommunicationAdapter, console);
        } else if ("EcoDisaster".equals(mapId)) {
            challengeScreen = new EcoDisasterScreen(this, platformSpecific, assetManager, soundManager, modelFactory, serverCommunicationAdapter, console);
        }
        setScreen(challengeScreen);
    }

    @Override
    public void dispose() {
        assetManager.dispose();
        soundManager.dispose();
        modelFactory.dispose();
        if (console != null) { console.dispose(); }
        if (loadingScreen != null) { loadingScreen.dispose(); }
        if (challengeScreen != null) { challengeScreen.dispose(); }
    }

    public void successfullyConnected() {
        String requestedChallenge = platformSpecific.getRequestedChallenge();
        if (requestedChallenge == null || "".equals(requestedChallenge)) {
            requestedChallenge = "PiNoon";
        }
        serverCommunicationAdapter.startEngine(requestedChallenge, false, platformSpecific.isSimulation());
        setChallengeScreen(requestedChallenge);
    }
}
