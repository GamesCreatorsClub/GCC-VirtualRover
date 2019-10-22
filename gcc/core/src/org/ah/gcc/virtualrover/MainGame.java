package org.ah.gcc.virtualrover;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

import org.ah.gcc.virtualrover.message.GCCMessageFactory;
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
    private GCCMessageFactory messageFactory;

    private AssetManager assetManager;
    private SoundManager soundManager;
    private ModelFactory modelFactory;

    private Console console;

    private PiNoonScreen piNoonScreen;
    private LoadingScreen loadingScreen;

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

        messageFactory = new GCCMessageFactory();
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
        serverCommunicationAdapter = new ServerCommunicationAdapter(serverCommunication, messageFactory, console);
        serverCommunicationAdapter.startEngine("PiNoon", true);
        piNoonScreen = new PiNoonScreen(this, assetManager, soundManager, modelFactory, serverCommunicationAdapter, console);
        setScreen(piNoonScreen);
    }

    @Override
    public void dispose() {
        assetManager.dispose();
        soundManager.dispose();
        modelFactory.dispose();
        if (console != null) { console.dispose(); }
        if (loadingScreen != null) { loadingScreen.dispose(); }
        if (piNoonScreen != null) { piNoonScreen.dispose(); }
    }
}
