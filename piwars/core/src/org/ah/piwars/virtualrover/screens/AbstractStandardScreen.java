package org.ah.piwars.virtualrover.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.IntSet;

import org.ah.piwars.virtualrover.MainGame;
import org.ah.piwars.virtualrover.ModelFactory;
import org.ah.piwars.virtualrover.PlatformSpecific;
import org.ah.piwars.virtualrover.ServerCommunicationAdapter;
import org.ah.piwars.virtualrover.backgrounds.Background;
import org.ah.piwars.virtualrover.camera.CameraControllersManager;
import org.ah.piwars.virtualrover.camera.CinematicCameraController;
import org.ah.piwars.virtualrover.challenges.ChallengeArena;
import org.ah.piwars.virtualrover.game.GameMessageObject;
import org.ah.piwars.virtualrover.game.PiWarsGame;
import org.ah.piwars.virtualrover.utils.SoundManager;
import org.ah.piwars.virtualrover.view.ChatColor;
import org.ah.piwars.virtualrover.view.ChatListener;
import org.ah.piwars.virtualrover.view.Console;
import org.ah.piwars.virtualrover.world.PlayerModelLink;
import org.ah.themvsus.engine.client.AbstractServerCommunication;
import org.ah.themvsus.engine.client.ClientEngine;
import org.ah.themvsus.engine.common.game.Player;

import static org.ah.piwars.virtualrover.MainGame.SCALE;

public abstract class AbstractStandardScreen extends ScreenAdapter implements ChatListener, InputProcessor  {

    public static Vector3 UP = Vector3.Y;

    protected MainGame mainGameApp;
    protected PlatformSpecific platformSpecific;
    protected ServerCommunicationAdapter serverCommunicationAdapter;

    protected AssetManager assetManager;
    protected SoundManager soundManager;
    protected ModelFactory modelFactory;

    protected ModelBatch batch;
    protected Environment environment;

    protected OrthographicCamera hudCamera;
    protected SpriteBatch spriteBatch;
    protected BitmapFont fontBig;
    protected BitmapFont fontSmallMono;
    protected Texture logo;

    protected boolean renderBackground = false;

    protected Console console;

    protected ChallengeArena challenge;
    protected Background background;

    private String bottomMessage;
    private boolean bottomMessageBlink;

    private String middleMessage;
    private boolean middleMessageBlink;

    private GlyphLayout glyphLayout = new GlyphLayout();
    private int a = 0;

    protected boolean leftShift;
    protected boolean rightShift;
    protected boolean leftAlt;
    protected boolean rightAlt;
    protected boolean leftCtrl;
    protected boolean rightCtrl;
    protected boolean escPressed;

    protected boolean suspended;

    protected boolean drawFPS = false;

    private IntSet unknownObjectIds = new IntSet();

    protected PerspectiveCamera camera;
    protected CameraControllersManager cameraControllersManager;
    protected InputMultiplexer cameraInputMultiplexer;
    protected CinematicCameraController cinematicCameraController;

    protected AbstractStandardScreen(MainGame game,
            PlatformSpecific platformSpecific,
            AssetManager assetManager,
            SoundManager soundManager,
            ModelFactory modelFactory,
            ServerCommunicationAdapter serverCommunicationAdapter,
            Console console) {

        this.mainGameApp = game;
        this.platformSpecific = platformSpecific;
        this.assetManager = assetManager;
        this.soundManager = soundManager;
        this.modelFactory = modelFactory;
        this.serverCommunicationAdapter = serverCommunicationAdapter;
        this.console = console;

        batch = new ModelBatch();
        spriteBatch = new SpriteBatch();

        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.6f, 0.6f, 0.6f, 1f));
        DirectionalLight light = new DirectionalLight();
        light.set(1f, 1f, 1f, new Vector3(-0.5f, -1f, 0.5f));
        environment.add(light);

        hudCamera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        hudCamera.setToOrtho(true);

        setupCamera(); // TODO not the best idea to override method for constructor
    }

    @Override
    public void dispose() {
        batch.dispose();
        spriteBatch.dispose();
        fontBig.dispose();
        if (logo == null) { logo.dispose(); }
        if (challenge != null) { challenge.dispose(); }
        if (background != null) { background.dispose(); }
    }

    protected void setupCamera() {
        camera = new PerspectiveCamera(45, 800, 480);
        camera.position.set(300f * SCALE, 480f * SCALE, 300f * SCALE);
        camera.lookAt(0f, 0f, 0f);
        camera.near = 0.02f;
        camera.far = 1000f;

        cameraInputMultiplexer = new InputMultiplexer();
        Gdx.input.setInputProcessor(cameraInputMultiplexer);
        Gdx.input.setCursorCatched(false);

        cameraControllersManager = new CameraControllersManager();
        cameraInputMultiplexer.addProcessor(this);
        cameraInputMultiplexer.addProcessor(cameraControllersManager);

        cinematicCameraController = new CinematicCameraController(camera, serverCommunicationAdapter);
        cameraControllersManager.addCameraController("Cinematic", cinematicCameraController);
        cameraControllersManager.addCameraController("Default", new CameraInputController(camera));
        // cameraControllersManager.addCameraController("Other", new CinematicCameraController2(camera, players));
    }

    public void reset() {
        setMiddleMessage("", false);
        suspended = false;
        serverCommunicationAdapter.reset();
    }

    protected Background getBackground() {
        return background;
    }

    protected void setBackground(Background background) {
        this.background = background;
    }

    public ChallengeArena getChallengeArena() {
        return challenge;
    }

    public void setChallengeArena(ChallengeArena challenge) {
        this.challenge = challenge;
    }

    protected void setBottomMessage(String message, boolean blink) {
        this.bottomMessage = message;
        this.bottomMessageBlink = blink;
    }

    protected void setMiddleMessage(String message, boolean blink) {
        this.middleMessage = message;
        this.middleMessageBlink = blink;
    }

    @Override
    public void show() {
        if (fontBig == null) {
            fontBig = assetManager.get("font/basic.fnt");
        }
        if (fontSmallMono == null) {
            fontSmallMono = assetManager.get("font/droidsansmono-15.fnt");
        }
        if (logo == null) {
            // logo = assetManager.get("GCC_full.png");
            logo = assetManager.get("PiWarsLogo-small.png");
        }

        if (console != null) {
            console.setCamera(hudCamera);
            console.addListener(this);
        }
    }

    @Override
    public void hide() {
        if (console != null) {
            console.removeListener(this);
        }
    }

    @Override
    public void resize(int width, int height) {
        if (console != null) {
            console.setConsoleWidth(width);
        }
        if (hudCamera != null) {
            hudCamera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            hudCamera.update();
        }
    }

    protected void progressEngine() {
        ClientEngine<PiWarsGame> engine = serverCommunicationAdapter.getEngine();
        if (engine != null) {
            long now = System.currentTimeMillis();
            engine.progressEngine(now, unknownObjectIds);

            if (unknownObjectIds.size > 0) {
                serverCommunicationAdapter.requestFullUpdate(unknownObjectIds);
            }

            PlayerModelLink playerOne = serverCommunicationAdapter.getPlayerOneVisualObject();
            if (playerOne != null) {
                serverCommunicationAdapter.setPlayerOneInput(playerOne.roverInput);
            }
            PlayerModelLink playerTwo = serverCommunicationAdapter.getPlayerTwoVisualObject();
            if (playerTwo != null) {
                serverCommunicationAdapter.setPlayerTwoInput(playerTwo.roverInput);
            }
        }
    }

    protected void drawFPS() {
        ClientEngine<PiWarsGame> engine = serverCommunicationAdapter.getEngine();
        AbstractServerCommunication<?> abstractServerCommunication = serverCommunicationAdapter.getServerCommmunication();

        String fps = "f:" + Integer.toString(Gdx.graphics.getFramesPerSecond());
        fontSmallMono.draw(spriteBatch, fps, Gdx.graphics.getWidth() - 60, Gdx.graphics.getHeight() - 12);

        String rtt = "RTT:" + Integer.toString(engine.getAverageRTT()) + "/" + Integer.toString(engine.getMaxRTT()) + "/" + Integer.toString(engine.getCurrentRTT());
        fontSmallMono.draw(spriteBatch, rtt, Gdx.graphics.getWidth() - 200, Gdx.graphics.getHeight() - 12);

        String debugDelay = "DD:" + Integer.toString(abstractServerCommunication.getReceivingDelay()) + "/" + Integer.toString(abstractServerCommunication.getSendingDelay());
        fontSmallMono.draw(spriteBatch, debugDelay, Gdx.graphics.getWidth() - 200, Gdx.graphics.getHeight() - 30);

        String frameTickInfo = "RUDA:" + Integer.toString(engine.getRebuiltFramesNumber())
                                       + "/" + Integer.toString(engine.getSpedUpFrames())
                                       + "/" + Integer.toString(engine.getSlowedDownFrames())
                                       + "/" + Integer.toString(engine.getAdjustedForMissingInputsFrames());
        fontSmallMono.draw(spriteBatch, frameTickInfo, Gdx.graphics.getWidth() - 200, Gdx.graphics.getHeight() - 48);

    }


    protected void drawStandardMessages() {
        a++;
        spriteBatch.begin();
        if (!platformSpecific.isSimulation()) {
            spriteBatch.draw(logo, 0, Gdx.graphics.getHeight() - logo.getHeight());
        }
        if (bottomMessage != null && (!bottomMessageBlink || Math.floor(a / 20.0) % 2 == 0)) {
            fontBig.draw(spriteBatch, bottomMessage, 64, 128);
            // font.draw(spriteBatch, "Press space to start", margin, margin * 2);
        }

        GameMessageObject gameMessageObject = serverCommunicationAdapter.getGameMessageObject();

        if (gameMessageObject != null && gameMessageObject.getMessage() != null && !"".equals(gameMessageObject.getMessage())) {
            middleMessage = "";
            String message = gameMessageObject.getMessage();
            fontBig.draw(spriteBatch, message, (Gdx.graphics.getWidth() - textWidth(fontBig, message)) / 2, (Gdx.graphics.getHeight() - fontBig.getLineHeight()) / 2);
        } else if (middleMessage != null && (!middleMessageBlink || Math.floor(a / 20.0) % 2 == 0)) {
            String message = middleMessage;
            fontBig.draw(spriteBatch, message, (Gdx.graphics.getWidth() - textWidth(fontBig, message)) / 2, (Gdx.graphics.getHeight() - fontBig.getLineHeight()) / 2);
        }

        spriteBatch.end();
    }

    protected boolean isSuspended() {
        return suspended;
    }

    protected void leave() {
        mainGameApp.selelectChallenge();
    }

    @Override
    public void onCommand(Player from, String cmdName, String[] args) {
        if ("hello".equals(cmdName)) {
            console.chat("Bot", "Hello", ChatColor.PURPLE);
        } else if ("help".equals(cmdName)) {
            console.raw(ChatColor.PURPLE + "/hello" + ChatColor.YELLOW + " | " + ChatColor.GREEN + "says hello");
            console.raw(ChatColor.PURPLE + "/time" + ChatColor.GREEN + "gives current time in millis");
            console.raw(ChatColor.PURPLE + "/help" + ChatColor.YELLOW + " | " + ChatColor.GREEN + "Shows this");
            console.raw(ChatColor.PURPLE + "/cpu" + ChatColor.YELLOW + " | " + ChatColor.GREEN + "Shows this");
        } else if ("colors".equals(cmdName)) {
            console.raw(ChatColor.RED + "o" + ChatColor.ORANGE + "o" + ChatColor.YELLOW + "o" + ChatColor.GREEN + "o" + ChatColor.BLUE + "o" + ChatColor.INDIGO
                    + "o" + ChatColor.PURPLE + "o" + ChatColor.GRAY + "o" + ChatColor.BLACK + "o");
        } else if ("time".equals(cmdName)) {
            console.info(ChatColor.INDIGO + "millis: " + ChatColor.GREEN + System.currentTimeMillis());
        } else {
            console.error("Unknow command, type /help for list");
        }
    }

    protected void moveRovers() {
        PlayerModelLink player1 = serverCommunicationAdapter.getPlayerOneVisualObject();
        if (player1 != null) {
            player1.roverInput.moveY(Gdx.input.isKeyPressed(Input.Keys.W) ? 1f : Gdx.input.isKeyPressed(Input.Keys.S) ? -1f : 0f);
            player1.roverInput.moveX(Gdx.input.isKeyPressed(Input.Keys.A) ? 1f : Gdx.input.isKeyPressed(Input.Keys.D) ? -1f : 0f);
            player1.roverInput.rotateX(Gdx.input.isKeyPressed(Input.Keys.Q) ? 1f : Gdx.input.isKeyPressed(Input.Keys.E) ? -1f : 0f);
            player1.roverInput.rightTrigger(Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) ? 1f : 0f);
            player1.roverInput.circle(Gdx.input.isKeyPressed(Input.Keys.Z));
            player1.roverInput.cross(Gdx.input.isKeyPressed(Input.Keys.X));
            player1.roverInput.square(Gdx.input.isKeyPressed(Input.Keys.C));
            player1.roverInput.triangle(Gdx.input.isKeyPressed(Input.Keys.V));
        }

        PlayerModelLink player2 = serverCommunicationAdapter.getPlayerTwoVisualObject();
        if (player2 != null) {
            player2.roverInput.moveY(Gdx.input.isKeyPressed(Input.Keys.I) ? 1f : Gdx.input.isKeyPressed(Input.Keys.K) ? -1f : 0f);
            player2.roverInput.moveX(Gdx.input.isKeyPressed(Input.Keys.J) ? 1f : Gdx.input.isKeyPressed(Input.Keys.L) ? -1f : 0f);
            player2.roverInput.rotateX(Gdx.input.isKeyPressed(Input.Keys.U) ? 1f : Gdx.input.isKeyPressed(Input.Keys.O) ? -1f : 0f);
            player2.roverInput.rightTrigger(Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT) ? 1f : 0f);
            player2.roverInput.circle(Gdx.input.isKeyPressed(Input.Keys.N));
            player2.roverInput.cross(Gdx.input.isKeyPressed(Input.Keys.M));
            player2.roverInput.square(Gdx.input.isKeyPressed(Input.Keys.COMMA));
            player2.roverInput.triangle(Gdx.input.isKeyPressed(Input.Keys.PERIOD));
        }
    }

    @Override
    public void onChat(String playerName, String text) {

    }

    @Override
    public void onText(String text) {

    }

    @Override
    public boolean keyDown(int keycode) {
        if (!suspended) {
            if (keycode == Input.Keys.ESCAPE && !escPressed) {
                suspended = true;
                escPressed = true;
            }
            if (keycode == Input.Keys.SHIFT_LEFT) {
                leftShift = true;
            }
            if (keycode == Input.Keys.SHIFT_RIGHT) {
                rightShift = true;
            }
            if (keycode == Input.Keys.ALT_LEFT) {
                leftAlt = true;
            }
            if (keycode == Input.Keys.ALT_RIGHT) {
                rightAlt = true;
            }
            if (keycode == Input.Keys.CONTROL_LEFT) {
                leftCtrl = true;
            }
            if (keycode == Input.Keys.CONTROL_RIGHT) {
                rightCtrl = true;
            }
            if (keycode == Input.Keys.F) {
                drawFPS = !drawFPS;
            }
        } else {
            if (keycode == Input.Keys.ESCAPE && !escPressed) {
                leave();
                escPressed = true;
            } else {
                setMiddleMessage("", true);
                suspended = false;
            }
        }
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        if (keycode == Input.Keys.ESCAPE) {
            escPressed = false;
        }
        if (keycode == Input.Keys.SHIFT_LEFT) {
            leftShift = false;
        }
        if (keycode == Input.Keys.SHIFT_RIGHT) {
            rightShift = false;
        }
        if (keycode == Input.Keys.ALT_LEFT) {
            leftAlt = false;
        }
        if (keycode == Input.Keys.ALT_RIGHT) {
            rightAlt = false;
        }
        if (keycode == Input.Keys.CONTROL_LEFT) {
            leftCtrl = false;
        }
        if (keycode == Input.Keys.CONTROL_RIGHT) {
            rightCtrl = false;
        }
        return false;
    }

    @Override public boolean keyTyped(char character) {
        if (character == '=') {
            AbstractServerCommunication<?> serverCommmunication = serverCommunicationAdapter.getServerCommmunication();
            serverCommmunication.setSendingDelay(serverCommmunication.getSendingDelay() + 5);
        } else if (character == '-') {
            AbstractServerCommunication<?> serverCommmunication = serverCommunicationAdapter.getServerCommmunication();
            int newSendingDelay = serverCommmunication.getSendingDelay() - 5;
            if (newSendingDelay < 0) {
                newSendingDelay = 0;
            }
            serverCommmunication.setSendingDelay(newSendingDelay);
        } else if (character == '+') {
            AbstractServerCommunication<?> serverCommmunication = serverCommunicationAdapter.getServerCommmunication();
            serverCommmunication.setReceivingDelay(serverCommmunication.getReceivingDelay() + 5);
        } else if (character == '_') {
            AbstractServerCommunication<?> serverCommmunication = serverCommunicationAdapter.getServerCommmunication();
            int newReceivingDelay = serverCommmunication.getReceivingDelay() - 5;
            if (newReceivingDelay < 0) {
                newReceivingDelay = 0;
            }
            serverCommmunication.setReceivingDelay(newReceivingDelay);
        }
        return false;
    }

    @Override public boolean touchDown(int screenX, int screenY, int pointer, int button) { return false; }

    @Override public boolean touchUp(int screenX, int screenY, int pointer, int button) { return false; }

    @Override public boolean touchDragged(int screenX, int screenY, int pointer) { return false; }

    @Override public boolean mouseMoved(int screenX, int screenY) { return false; }

    @Override public boolean scrolled(int amount) { return false; }

    protected float textWidth(BitmapFont font, String text) {
        glyphLayout.setText(font, text);
        return glyphLayout.width;
    }
}
