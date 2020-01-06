package org.ah.piwars.virtualrover;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

import org.ah.piwars.virtualrover.screens.BlastOffScreen;
import org.ah.piwars.virtualrover.screens.CanyonsOfMarsScreen;
import org.ah.piwars.virtualrover.screens.ChallengeScreen;
import org.ah.piwars.virtualrover.screens.ConnectingScreen;
import org.ah.piwars.virtualrover.screens.EcoDisasterScreen;
import org.ah.piwars.virtualrover.screens.GreetingScreen;
import org.ah.piwars.virtualrover.screens.LoadingScreen;
import org.ah.piwars.virtualrover.screens.PiNoonScreen;
import org.ah.piwars.virtualrover.screens.StraightLineSpeedTestScreen;
import org.ah.piwars.virtualrover.utils.SoundManager;
import org.ah.piwars.virtualrover.view.Console;
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
    private ChallengeScreen challengeScreen;
    private PiNoonScreen piNoonScreen;
    private EcoDisasterScreen ecoDisasterScreen;
    private CanyonsOfMarsScreen canyonsOfMarsScreen;
    private StraightLineSpeedTestScreen straightLineSpeedTestScreen;
    private BlastOffScreen blastOffScreen;

    public MainGame(PlatformSpecific platformSpecific) {
        this.platformSpecific = platformSpecific;
    }

    @Override
    public void create() {

        assetManager = new AssetManager();

        assetManager.load("GCC_full.png", Texture.class);
        assetManager.load("3d/Alien.png", Texture.class);
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
        console.raw("Games Creators Club () Virtual Rover");
        console.raw("(c) Creative Sphere Limited");

        soundManager.fetchSounds(assetManager);
        serverCommunicationAdapter = new ServerCommunicationAdapter(serverCommunication, console, modelFactory);

        greetingScreen = new GreetingScreen(this, platformSpecific, assetManager, soundManager, modelFactory, serverCommunicationAdapter, console);
        piNoonScreen = new PiNoonScreen(this, platformSpecific, assetManager, soundManager, modelFactory, serverCommunicationAdapter, console);
        ecoDisasterScreen = new EcoDisasterScreen(this, platformSpecific, assetManager, soundManager, modelFactory, serverCommunicationAdapter, console);
        canyonsOfMarsScreen = new CanyonsOfMarsScreen(this, platformSpecific, assetManager, soundManager, modelFactory, serverCommunicationAdapter, console);
        straightLineSpeedTestScreen = new StraightLineSpeedTestScreen(this, platformSpecific, assetManager, soundManager, modelFactory, serverCommunicationAdapter, console);
        blastOffScreen = new BlastOffScreen(this, platformSpecific, assetManager, soundManager, modelFactory, serverCommunicationAdapter, console);

        if (platformSpecific.isLocalOnly()) {
            String requestedChallenge = platformSpecific.getRequestedChallenge();
            if (requestedChallenge == null || "".equals(requestedChallenge)) {
                requestedChallenge = "PiNoon";
            }
            serverCommunicationAdapter.startEngine(requestedChallenge, true, platformSpecific.isSimulation());
            setChallengeScreen(requestedChallenge);
        } else if (platformSpecific.hasServerDetails() && platformSpecific.isSimulation()) {
            setScreen(connectingScreen);
            connectingScreen.reset();
            serverCommunicationAdapter.connectToServer(
                    platformSpecific.getPreferredServerAddress(),
                    platformSpecific.getPreferredServerPort(),
                    connectingScreen);
            serverCommunicationAdapter.setGameMapCallback(connectingScreen);
            setScreen(connectingScreen);
        } else {
//            greetingScreen = new GreetingScreen(this, platformSpecific, assetManager, soundManager, modelFactory, serverCommunicationAdapter, console);
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
            challengeScreen = piNoonScreen;
        } else if ("EcoDisaster".equals(mapId)) {
            challengeScreen = ecoDisasterScreen;
        } else if ("CanyonsOfMars".equals(mapId)) {
            challengeScreen = canyonsOfMarsScreen;
        } else if ("StraightLineSpeedTest".equals(mapId)) {
            challengeScreen = straightLineSpeedTestScreen;
        } else if ("BlastOff".equals(mapId)) {
            challengeScreen = blastOffScreen;
        }
        challengeScreen.reset();
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

    public void successfullyConnected(String mapId) {
        serverCommunicationAdapter.startEngine(mapId, false, platformSpecific.isSimulation());
        setChallengeScreen(mapId);
    }
}
