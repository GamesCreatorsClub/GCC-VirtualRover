package org.ah.piwars.virtualrover;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g3d.Model;

import org.ah.piwars.virtualrover.challenges.Challenges;
import org.ah.piwars.virtualrover.game.rovers.RoverType;
import org.ah.piwars.virtualrover.rovers.RoverModels;
import org.ah.piwars.virtualrover.screens.ChallengeScreen;
import org.ah.piwars.virtualrover.screens.ChallengeScreens;
import org.ah.piwars.virtualrover.screens.ConnectingScreen;
import org.ah.piwars.virtualrover.screens.GreetingScreen;
import org.ah.piwars.virtualrover.screens.LoadingScreen;
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

    private Console console;

    private Challenges challenges;

    private LoadingScreen loadingScreen;
    private ConnectingScreen connectingScreen;
    private GreetingScreen greetingScreen;
    private ChallengeScreen challengeScreen;

    private ChallengeScreens challengeScreens;
    private RoverType rover1Selection;
    private RoverType rover2Selection;

    public MainGame(PlatformSpecific platformSpecific) {
        this.platformSpecific = platformSpecific;
    }

    @Override
    public void create() {

        assetManager = new AssetManager();

        assetManager.load("GCC_full.png", Texture.class);
        assetManager.load("PiWarsLogo-small.png", Texture.class);
        assetManager.load("3d/Alien.png", Texture.class);
        assetManager.load("font/basic.fnt", BitmapFont.class);
        assetManager.load("font/copper18.fnt", BitmapFont.class);
        // assetManager.load("font/droidsansmono-8.fnt", BitmapFont.class);
        assetManager.load("font/droidsansmono-15.fnt", BitmapFont.class);
        // assetManager.load("font/font18.fnt", BitmapFont.class);
        // assetManager.load("font/font24.fnt", BitmapFont.class);
        // assetManager.load("font/font32.fnt", BitmapFont.class);

        assetManager.load("3d/FullWheel.obj", Model.class);
        assetManager.load("3d/rovers/attachments/pi_noon.obj", Model.class);
        assetManager.load("3d/rovers/attachments/balloon.obj", Model.class);
        assetManager.load("3d/rovers/attachments/teapot.g3db", Model.class);

        assetManager.load("3d/challenges/barrel.obj", Model.class);

        assetManager.load("3d/challenges/pi-noon-arena.obj", Model.class);
        assetManager.load("3d/challenges/eco-disaster-arena.obj", Model.class);
        assetManager.load("3d/challenges/eco-disaster-zone.obj", Model.class);

        RoverModels roverModels = new RoverModels();
        roverModels.load(assetManager);

        soundManager = new SoundManager(!platformSpecific.hasSound());
        soundManager.requestAssets(assetManager);

        serverCommunication = platformSpecific.getServerCommunication();

        loadingScreen = new LoadingScreen(this, assetManager);
        connectingScreen = new ConnectingScreen(this, assetManager);

        setScreen(loadingScreen);
    }

    public void finishedLoading() {
        console = new Console();
        console.raw("Welcome to Virtual PiWars v.0.7");
        console.raw("Games Creators Club Virtual Rover");
        console.raw("(c) Creative Sphere Limited");

        soundManager.fetchSounds(assetManager);
        serverCommunicationAdapter = new ServerCommunicationAdapter(serverCommunication, console, assetManager);

        challenges = new Challenges(assetManager, serverCommunicationAdapter);
        challengeScreens = new ChallengeScreens(this, platformSpecific, challenges, assetManager, soundManager, serverCommunicationAdapter, console);
        greetingScreen = new GreetingScreen(this, platformSpecific, assetManager, soundManager, challenges, serverCommunicationAdapter, console);

        selelectChallenge();
    }

    public void selelectChallenge() {
        if (serverCommunicationAdapter.getEngine() != null) {
            serverCommunicationAdapter.getEngine().resetGame();
        }

        if (platformSpecific.isLocalOnly()) {
            String requestedChallenge = platformSpecific.getRequestedChallenge();
            if (requestedChallenge == null || "".equals(requestedChallenge)) {
                greetingScreen.reset();
                setScreen(greetingScreen);
            } else {
                setChallengeScreen(requestedChallenge, 1, true);
            }
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

    public void setChallengeScreen(String mapId, int playerId, boolean local) {
        Gdx.input.setOnscreenKeyboardVisible(false);

        serverCommunicationAdapter.startEngine(mapId, playerId, local, platformSpecific.isSimulation());

        challengeScreen = challengeScreens.getChallengeScreen(mapId);
        setScreen(challengeScreen);
        // challengeScreen.reset();
    }

    @Override
    public void dispose() {
        assetManager.dispose();
        soundManager.dispose();
        if (console != null) { console.dispose(); }
        if (loadingScreen != null) { loadingScreen.dispose(); }
        if (challengeScreen != null) { challengeScreen.dispose(); }
    }

    public void setSelectedRover1(RoverType roverType) {
        rover1Selection = roverType;
    }

    public RoverType getSelectedRover1() {
        if (rover1Selection == null) {
            return RoverType.GCCM16;
        }
        return rover1Selection;
    }

    public void setSelectedRover2(RoverType roverType) {
        rover2Selection = roverType;
    }

    public RoverType getSelectedRover2() {
        if (rover2Selection == null) {
            return RoverType.CBIS;
        }
        return rover2Selection;
    }
}
