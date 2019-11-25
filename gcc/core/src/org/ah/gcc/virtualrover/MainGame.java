package org.ah.gcc.virtualrover;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

import org.ah.gcc.virtualrover.screens.LoadingScreen;
import org.ah.gcc.virtualrover.screens.PiNoonScreen;
import org.ah.gcc.virtualrover.utils.SoundManager;
import org.ah.gcc.virtualrover.view.Console;
import org.ah.themvsus.engine.client.ServerCommunication;
import org.ah.themvsus.engine.client.ServerCommunication.ServerConnectionCallback;

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
    private GreetingScreen greetingScreen;
    private PiNoonScreen challengeScreen;


    public MainGame(PlatformSpecific platformSpecific) {
        this.platformSpecific = platformSpecific;
    }

    @Override
    public void create() {

        assetManager = new AssetManager();

        assetManager.load("GCC_full.png", Texture.class);
        assetManager.load("font/basic.fnt", BitmapFont.class);
        assetManager.load("font/copper18.fnt", BitmapFont.class);

        soundManager = new SoundManager(!platformSpecific.hasSound());
        soundManager.requestAssets(assetManager);

        modelFactory = new ModelFactory();
        modelFactory.load();

        serverCommunication = platformSpecific.getServerCommunication();

        loadingScreen = new LoadingScreen(this, assetManager);

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
            serverCommunicationAdapter.connectToServer(
                    platformSpecific.getPreferredServerAddress(),
                    platformSpecific.getPreferredServerPort(),
                    new ServerConnectionCallback() {  // TODO factor this out so it can be reused on other connectToServer attempts
                        @Override public void successful() {
                            serverCommunicationAdapter.startEngine("PiNoon", true);
                            startChallenge("PiNoon");
                        }

                        @Override public void failed(String msg) {
                            // TODO log something to console
                        }
                });
        } else if (platformSpecific.isLocalOnly()) {
            serverCommunicationAdapter.startEngine("PiNoon", true);
            startChallenge("PiNoon");
        } else {
            greetingScreen = new GreetingScreen(this, platformSpecific, assetManager, soundManager, modelFactory, serverCommunicationAdapter, console);
//            if (platformSpecific.hasServerDetails() && platformSpecific.isSimulation()) {
//                greetingScreen.connectToServer(platformSpecific.getPreferredServerAddress(), platformSpecific.getPreferredServerPort());
//            }
            greetingScreen.reset();
            setScreen(greetingScreen);
        }
    }

    public void startChallenge(String mapId) {
        Gdx.input.setOnscreenKeyboardVisible(false);

        challengeScreen = new PiNoonScreen(this, platformSpecific, assetManager, soundManager, modelFactory, serverCommunicationAdapter, console);
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
}
