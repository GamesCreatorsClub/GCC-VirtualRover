package org.ah.piwars.virtualrover.challenges;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalShadowLight;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.g3d.utils.DepthShaderProvider;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.IntSet;

import org.ah.piwars.virtualrover.MainGame;
import org.ah.piwars.virtualrover.PlatformSpecific;
import org.ah.piwars.virtualrover.ServerCommunicationAdapter;
import org.ah.piwars.virtualrover.backgrounds.Background;
import org.ah.piwars.virtualrover.camera.CameraControllersManager;
import org.ah.piwars.virtualrover.camera.CinematicCameraController;
import org.ah.piwars.virtualrover.game.GameMessageObject;
import org.ah.piwars.virtualrover.game.PiWarsGame;
import org.ah.piwars.virtualrover.screens.RenderingContext;
import org.ah.piwars.virtualrover.utils.SoundManager;
import org.ah.piwars.virtualrover.view.ChatColor;
import org.ah.piwars.virtualrover.view.ChatListener;
import org.ah.piwars.virtualrover.view.Console;
import org.ah.piwars.virtualrover.world.PlayerModelLink;
import org.ah.themvsus.engine.client.AbstractServerCommunication;
import org.ah.themvsus.engine.client.ClientEngine;
import org.ah.themvsus.engine.common.game.Player;
import org.ah.themvsus.engine.common.input.PlayerInputs;

import static org.ah.piwars.virtualrover.MainGame.SCALE;

@SuppressWarnings("deprecation")
public abstract class AbstractChallengeScreen extends ScreenAdapter implements ChatListener, InputProcessor  {

    public static Vector3 UP = Vector3.Y;
    public static final int CORNER_WIDTH = 7;

    protected static final int ARROW_WIDTH = 32;
    protected static final int ARROW_HEIGHT = 32;

    protected static final Color VERY_TRANSPARENT = new Color(1f, 1f, 1f, .2f);
    protected static final Color TRANSPARENT = new Color(1f, 1f, 1f, .5f);
    protected static final Color SLIGHTLY_TRANSPARENT = new Color(1f, 1f, 1f, .7f);

    protected int width;
    protected int height;

    protected MainGame mainGameApp;
    protected PlatformSpecific platformSpecific;
    protected ServerCommunicationAdapter serverCommunicationAdapter;

    protected AssetManager assetManager;
    protected SoundManager soundManager;

    protected ModelBatch modelBatch;
    protected Environment environment;

    // public int shadowTextureSize = 8192;
//  public int shadowTextureSize = 4096;
    // public int shadowTextureSize = 2048;
    public int shadowTextureSize = 1280;

    protected DirectionalShadowLight shadowLight;
    protected Environment shadowEnvironment;
    protected ModelBatch shadowBatch;

    protected OrthographicCamera hudCamera;
    protected SpriteBatch spriteBatch;
    protected BitmapFont fontBig;
    protected BitmapFont fontSmallMono;
    protected Texture logo;

    protected Console console;

    protected ChallengeArena challenge;
    protected Background background;

    private String bottomMessage;
    private boolean bottomMessageBlink;

    private String middleMessage;
    private boolean middleMessageBlink;

    @SuppressWarnings("unused")
    private String topRightMessage;

    private GlyphLayout glyphLayout = new GlyphLayout();
    private int a = 0;

    protected boolean leftShift;
    protected boolean rightShift;
    protected boolean leftAlt;
    protected boolean rightAlt;
    protected boolean leftCtrl;
    protected boolean rightCtrl;
    protected boolean escPressed;
    protected LocalPlayerInputs player1Inputs = new LocalPlayerInputs();
    protected LocalPlayerInputs player2Inputs = new LocalPlayerInputs();

    protected boolean suspended;

    protected boolean drawFPS = false;

    protected RenderingContext renderingContext;

    private IntSet unknownObjectIds = new IntSet();

    protected PerspectiveCamera camera;
    protected CameraControllersManager cameraControllersManager;
    protected InputMultiplexer cameraInputMultiplexer;
    protected CinematicCameraController cinematicCameraController;

    protected DirectionalLight directionalLight;
    private FrameBuffer textBackgroundCornerFrameBuffer;
    private Texture textBackgroundCornerTexture;
    private FrameBuffer textBackgroundFrameBuffer;
    private Texture textBackgroundTexture;
    protected int touchX;
    protected int touchY;
    private FrameBuffer upDownArrowFrameBuffer;
    private Texture upDownArrowTexture;
    private FrameBuffer leftRightArrowFrameBuffer;
    private Texture leftRightArrowTexture;
    private FrameBuffer dotFrameBuffer;
    private Texture dotTexture;

    protected AbstractChallengeScreen(MainGame game,
            PlatformSpecific platformSpecific,
            AssetManager assetManager,
            SoundManager soundManager,
            ServerCommunicationAdapter serverCommunicationAdapter,
            Console console) {

        this.mainGameApp = game;
        this.platformSpecific = platformSpecific;
        this.assetManager = assetManager;
        this.soundManager = soundManager;
        this.serverCommunicationAdapter = serverCommunicationAdapter;
        this.console = console;

        this.width = Gdx.graphics.getWidth();
        this.height = Gdx.graphics.getHeight();

        modelBatch = new ModelBatch();
        spriteBatch = new SpriteBatch();

        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.6f, 0.6f, 0.6f, 1f));
        directionalLight = new DirectionalLight();
        directionalLight.set(1f, 1f, 1f, new Vector3(-0.5f, -1f, 0.5f));
        environment.add(directionalLight);

        shadowLight = new DirectionalShadowLight(shadowTextureSize, shadowTextureSize, 8.5f, 8.5f, 0.01f, 100f);
        // shadowLight = new DirectionalShadowLight(shadowTextureSize, shadowTextureSize, 15f, 15f, 0.01f, 100f);
        shadowLight.set(1f, 1f, 1f, new Vector3(-0.5f, -1f, 0.5f));
        shadowEnvironment = new Environment();
        shadowEnvironment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.6f, 0.6f, 0.6f, 1f));
        shadowEnvironment.add(shadowLight);
        shadowEnvironment.shadowMap = shadowLight;

        shadowBatch = new ModelBatch(new DepthShaderProvider());
        hudCamera = new OrthographicCamera(width, height);
        hudCamera.setToOrtho(true);

        renderingContext = new RenderingContext(modelBatch, environment, null);

        setupCamera(); // TODO not the best idea to override method for constructor

        createTextBackgroundPanel();
        createArrow();
    }

    private void createArrow() {
        OrthographicCamera arrowCamera = new OrthographicCamera(ARROW_WIDTH, ARROW_HEIGHT);
        arrowCamera.setToOrtho(true, ARROW_WIDTH, ARROW_HEIGHT);

        upDownArrowFrameBuffer = new FrameBuffer(Format.RGBA8888, ARROW_WIDTH, ARROW_HEIGHT, false);
        upDownArrowTexture = upDownArrowFrameBuffer.getColorBufferTexture();
        ShapeRenderer shapeRenderer = new ShapeRenderer();
        shapeRenderer.setAutoShapeType(true);
        shapeRenderer.setProjectionMatrix(arrowCamera.combined);
        upDownArrowFrameBuffer.begin();
        Gdx.gl.glClearColor(0f, 0f, 0f, 0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        shapeRenderer.begin(ShapeType.Filled);
        shapeRenderer.setColor(1f, 1f, 1f, 1f);
        shapeRenderer.triangle(0, 0, ARROW_WIDTH / 2, ARROW_HEIGHT, ARROW_WIDTH, 0);
        shapeRenderer.end();
        upDownArrowFrameBuffer.end();

        leftRightArrowFrameBuffer = new FrameBuffer(Format.RGBA8888, ARROW_WIDTH, ARROW_HEIGHT, false);
        leftRightArrowTexture = leftRightArrowFrameBuffer.getColorBufferTexture();
        shapeRenderer = new ShapeRenderer();
        shapeRenderer.setAutoShapeType(true);
        shapeRenderer.setProjectionMatrix(arrowCamera.combined);
        leftRightArrowFrameBuffer.begin();
        Gdx.gl.glClearColor(0f, 0f, 0f, 0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        shapeRenderer.begin(ShapeType.Filled);
        shapeRenderer.setColor(1f, 1f, 1f, 1f);
        shapeRenderer.triangle(0, 0, ARROW_WIDTH, ARROW_HEIGHT / 2, 0, ARROW_HEIGHT);
        shapeRenderer.end();
        leftRightArrowFrameBuffer.end();

        dotFrameBuffer = new FrameBuffer(Format.RGBA8888, ARROW_WIDTH, ARROW_HEIGHT, false);
        dotTexture = dotFrameBuffer.getColorBufferTexture();

        shapeRenderer.setAutoShapeType(true);
        shapeRenderer.setProjectionMatrix(arrowCamera.combined);
        dotFrameBuffer.begin();
        Gdx.gl.glClearColor(0f, 0f, 0f, 0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        shapeRenderer.begin(ShapeType.Filled);
        shapeRenderer.setColor(1f, 1f, 1f, 1f);
        shapeRenderer.circle(ARROW_WIDTH / 2, ARROW_WIDTH / 2, ARROW_WIDTH / 2 - 1);
        shapeRenderer.end();
        dotFrameBuffer.end();
    }

    private void createTextBackgroundPanel() {
        OrthographicCamera cornerCamera = new OrthographicCamera(CORNER_WIDTH, CORNER_WIDTH);
        cornerCamera.setToOrtho(true, CORNER_WIDTH, CORNER_WIDTH);

        textBackgroundFrameBuffer = new FrameBuffer(Format.RGBA8888, 16, 16, false);
        textBackgroundTexture = textBackgroundFrameBuffer.getColorBufferTexture();

        textBackgroundFrameBuffer.begin();
        Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 0.1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        textBackgroundFrameBuffer.end();

        textBackgroundCornerFrameBuffer = new FrameBuffer(Format.RGBA8888, CORNER_WIDTH, CORNER_WIDTH, false);
        textBackgroundCornerTexture = textBackgroundCornerFrameBuffer.getColorBufferTexture();

        ShapeRenderer shapeRenderer = new ShapeRenderer();
        shapeRenderer.setAutoShapeType(true);
        shapeRenderer.setProjectionMatrix(cornerCamera.combined);
        textBackgroundCornerFrameBuffer.begin();
        Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        shapeRenderer.begin(ShapeType.Filled);
        shapeRenderer.setColor(0.0f, 0.0f, 0.0f, 0.1f);
        shapeRenderer.circle(CORNER_WIDTH, CORNER_WIDTH, CORNER_WIDTH);
        shapeRenderer.end();
        textBackgroundCornerFrameBuffer.end();
    }

    @Override
    public void dispose() {
        shadowBatch.dispose();
        modelBatch.dispose();
        spriteBatch.dispose();
        fontBig.dispose();
        if (logo == null) { logo.dispose(); }
        if (challenge != null) { challenge.dispose(); }
        if (background != null) { background.dispose(); }
        if (upDownArrowTexture != null) { upDownArrowTexture.dispose(); }
        if (upDownArrowFrameBuffer != null) { upDownArrowFrameBuffer.dispose(); }
        if (dotTexture != null) { dotTexture.dispose(); }
        if (dotFrameBuffer != null) { dotFrameBuffer.dispose(); }
    }

    protected void setupCamera() {
        camera = new PerspectiveCamera(45, width, height);
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

    protected void resetCameraPosition() {
        camera.position.set(300f * SCALE, 480f * SCALE, 300f * SCALE);
        camera.lookAt(0f, 0f, 0f);
    }

    public void reset() {
        setMiddleMessage("", false);
        suspended = false;
        serverCommunicationAdapter.reset();

        resetCameraPosition();

        challenge.init();
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
        challenge.setChallenge(serverCommunicationAdapter.getEngine().getGame().getChallenge());
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
        Gdx.input.setInputProcessor(cameraInputMultiplexer);
        Gdx.input.setCursorCatched(false);
    }

    @Override
    public void hide() {
        if (console != null) {
            console.removeListener(this);
        }
    }

    @Override
    public void resize(int width, int height) {
        this.width = width;
        this.height = height;
        if (console != null) {
            console.setConsoleWidth(width);
        }
        if (hudCamera != null) {
            hudCamera.setToOrtho(false, width, height);
            hudCamera.update();
        }
        if (camera != null) {
            camera.viewportWidth = width;
            camera.viewportHeight = height;
        }
    }

    protected void progressEngine() {
        ClientEngine<PiWarsGame> engine = serverCommunicationAdapter.getEngine();
        if (engine != null) {
            PlayerModelLink playerOne = serverCommunicationAdapter.getPlayerOneVisualObject();
            if (playerOne != null) {
                serverCommunicationAdapter.setPlayerOneInput(playerOne.roverInput);
            }
            PlayerModelLink playerTwo = serverCommunicationAdapter.getPlayerTwoVisualObject();
            if (playerTwo != null) {
                serverCommunicationAdapter.setPlayerTwoInput(playerTwo.roverInput);
                PiWarsGame game = engine.getGame();
                if (game.isServer()) {
                    PlayerInputs playerTwoInputs = serverCommunicationAdapter.getPlayerTwoInputs();
                    int currentForwardFrameNo = game.getCurrentFrameId();
                    if (playerTwoInputs != null) {
                        playerTwoInputs.trimBeforeFrame(currentForwardFrameNo);
                    }
                }
            }

            long now = System.currentTimeMillis();
            engine.progressEngine(now, unknownObjectIds);

            if (unknownObjectIds.size > 0) {
                serverCommunicationAdapter.requestFullUpdate(unknownObjectIds);
            }
        }
    }

    protected void drawFPS() {
        ClientEngine<PiWarsGame> engine = serverCommunicationAdapter.getEngine();
        AbstractServerCommunication<?> abstractServerCommunication = serverCommunicationAdapter.getServerCommmunication();

        String fps = "f:" + Integer.toString(Gdx.graphics.getFramesPerSecond());
        fontSmallMono.draw(spriteBatch, fps, width - 60, height - 48);

        String rtt = "RTT:" + Integer.toString(engine.getAverageRTT()) + "/" + Integer.toString(engine.getMaxRTT()) + "/" + Integer.toString(engine.getCurrentRTT());
        fontSmallMono.draw(spriteBatch, rtt, width - 200, height - 12);

        String debugDelay = "DD:" + Integer.toString(abstractServerCommunication.getReceivingDelay()) + "/" + Integer.toString(abstractServerCommunication.getSendingDelay());
        fontSmallMono.draw(spriteBatch, debugDelay, width - 200, height - 30);

        String frameTickInfo = "RUDA:" + Integer.toString(engine.getRebuiltFramesNumber())
                                       + "/" + Integer.toString(engine.getSpedUpFrames())
                                       + "/" + Integer.toString(engine.getSlowedDownFrames())
                                       + "/" + Integer.toString(engine.getAdjustedForMissingInputsFrames());
        fontSmallMono.draw(spriteBatch, frameTickInfo, width - 200, height - 48);
    }


    protected void drawStandardMessages() {
        a++;
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
            float textWidth = textWidth(fontBig, message);
            drawText(spriteBatch, fontBig, message, (width - textWidth) / 2, (height - fontBig.getLineHeight()) / 2, textWidth);

        } else if (middleMessage != null && (!middleMessageBlink || Math.floor(a / 20.0) % 2 == 0)) {
            String message = middleMessage;
            float textWidth = textWidth(fontBig, message);
            drawText(spriteBatch, fontBig, message, (width - textWidth) / 2, (height - fontBig.getLineHeight()) / 2, textWidth);
        }

        if (gameMessageObject != null && gameMessageObject.hasTimer()) {
            topRightMessage = "";
            int timer = gameMessageObject.getTimerTens(serverCommunicationAdapter.getEngine().getGame());
            String message = (timer / 10) + "." + (timer % 10);
            float textWidth = textWidth(fontBig, message);
            drawText(spriteBatch, fontBig, message, (width - textWidth) - 20, height - 10, textWidth);
        }
    }

    private void drawText(SpriteBatch spriteBatch, BitmapFont font, String message, float x, float y, float textWidth) {
        font.setColor(Color.BLACK);
        if (message.length() < 8) {
            font.draw(spriteBatch, message, x - 1, y - 1);
            font.draw(spriteBatch, message, x, y - 1);
            font.draw(spriteBatch, message, x + 1, y - 1);
            font.draw(spriteBatch, message, x - 1, y);
            font.draw(spriteBatch, message, x + 1, y);
            font.draw(spriteBatch, message, x - 1, y + 1);
            font.draw(spriteBatch, message, x, y - 1);
            font.draw(spriteBatch, message, x + 1, y + 1);
        } else {
            float lineHeight = font.getLineHeight();
            //  float x, float y, float width, float height, int srcX, int srcY, int srcWidth, int srcHeight, boolean flipX, boolean flipY
            spriteBatch.draw(textBackgroundTexture, x, y - lineHeight - CORNER_WIDTH, textWidth, CORNER_WIDTH);
            spriteBatch.draw(textBackgroundTexture, x - CORNER_WIDTH, y - lineHeight, textWidth + CORNER_WIDTH + CORNER_WIDTH, lineHeight);
            spriteBatch.draw(textBackgroundTexture, x, y, textWidth, CORNER_WIDTH);
            spriteBatch.draw(textBackgroundCornerTexture, x - CORNER_WIDTH, y - lineHeight - CORNER_WIDTH, CORNER_WIDTH, CORNER_WIDTH, 0, 0, CORNER_WIDTH, CORNER_WIDTH, false, false);
            spriteBatch.draw(textBackgroundCornerTexture, x - CORNER_WIDTH, y, CORNER_WIDTH, CORNER_WIDTH, 0, 0, CORNER_WIDTH, CORNER_WIDTH, false, true);
            spriteBatch.draw(textBackgroundCornerTexture, x + textWidth, y - lineHeight - CORNER_WIDTH, CORNER_WIDTH, CORNER_WIDTH, 0, 0, CORNER_WIDTH, CORNER_WIDTH, true, false);
            spriteBatch.draw(textBackgroundCornerTexture, x + textWidth, y, CORNER_WIDTH, CORNER_WIDTH, 0, 0, CORNER_WIDTH, CORNER_WIDTH, true, true);
            y = y - 4;
        }
        fontBig.setColor(Color.WHITE);
        fontBig.draw(spriteBatch, message, x, y);
    }

    protected void drawTouchDrag(SpriteBatch spriteBatch, LocalPlayerInputs input) {
        int x = this.touchX;
        int y = height - this.touchY;

        spriteBatch.setColor(input.y < 0f ? SLIGHTLY_TRANSPARENT: VERY_TRANSPARENT);
        spriteBatch.draw(upDownArrowTexture, x - ARROW_WIDTH / 2, y - ARROW_HEIGHT * 2, ARROW_WIDTH, ARROW_HEIGHT, 0, 0, ARROW_WIDTH, ARROW_HEIGHT, true, true);

        spriteBatch.setColor(input.y > 0f ? SLIGHTLY_TRANSPARENT : VERY_TRANSPARENT);
        spriteBatch.draw(upDownArrowTexture, x - ARROW_WIDTH / 2,  y + ARROW_HEIGHT, ARROW_WIDTH, ARROW_HEIGHT, 0, 0, ARROW_WIDTH, ARROW_HEIGHT, true, false);

        spriteBatch.setColor(input.rotateX < 0f ? SLIGHTLY_TRANSPARENT : VERY_TRANSPARENT);
        spriteBatch.draw(leftRightArrowTexture, x + ARROW_WIDTH , y - ARROW_HEIGHT / 2, ARROW_WIDTH, ARROW_HEIGHT, 0, 0, ARROW_WIDTH, ARROW_HEIGHT, false, false);

        spriteBatch.setColor(input.rotateX > 0f ? SLIGHTLY_TRANSPARENT : VERY_TRANSPARENT);
        spriteBatch.draw(leftRightArrowTexture, x - ARROW_WIDTH * 2, y - ARROW_HEIGHT / 2, ARROW_WIDTH, ARROW_HEIGHT, 0, 0, ARROW_WIDTH, ARROW_HEIGHT, true, false);

        spriteBatch.setColor(TRANSPARENT);
        spriteBatch.draw(dotTexture, x - ARROW_WIDTH / 2, y - ARROW_HEIGHT / 2);

        spriteBatch.setColor(Color.WHITE);
    }

    protected boolean isSuspended() {
        return suspended;
    }

    protected void leave() {
        mainGameApp.returnToMainScreen();
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
            player1.roverInput.moveY(player1Inputs.y);
            player1.roverInput.moveX(player1Inputs.x);
            player1.roverInput.rotateX(player1Inputs.rotateX);
            player1.roverInput.rotateY(player1Inputs.rotateY);
            player1.roverInput.rightTrigger(player1Inputs.rightTrigger);
            player1.roverInput.leftTrigger(player1Inputs.leftTrigger);
            player1.roverInput.circle(player1Inputs.circle);
            player1.roverInput.cross(player1Inputs.cross);
            player1.roverInput.square(player1Inputs.square);
            player1.roverInput.triangle(player1Inputs.triangle);
        }

        PlayerModelLink player2 = serverCommunicationAdapter.getPlayerTwoVisualObject();
        if (player2 != null) {
            player2.roverInput.moveY(player2Inputs.y);
            player2.roverInput.moveX(player2Inputs.x);
            player2.roverInput.rotateX(player2Inputs.rotateX);
            player2.roverInput.rotateY(player2Inputs.rotateY);
            player2.roverInput.rightTrigger(player2Inputs.rightTrigger);
            player2.roverInput.leftTrigger(player2Inputs.leftTrigger);
            player2.roverInput.circle(player2Inputs.circle);
            player2.roverInput.cross(player2Inputs.cross);
            player2.roverInput.square(player2Inputs.square);
            player2.roverInput.triangle(player2Inputs.triangle);
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

            if (keycode == Input.Keys.W) {
                player1Inputs.y = 1f;
            } else if (keycode == Input.Keys.S) {
                player1Inputs.y = -1f;
            }
            if (keycode == Input.Keys.A) {
                player1Inputs.x = 1f;
            } else if (keycode == Input.Keys.D) {
                player1Inputs.x = -1f;
            }
            if (keycode == Input.Keys.Q) {
                player1Inputs.rotateX = 1f;
            } else if (keycode == Input.Keys.E) {
                player1Inputs.rotateX = -1f;
            }
            if (keycode == Input.Keys.SHIFT_LEFT) {
                player1Inputs.rightTrigger = 1f;
            }
            if (keycode == Input.Keys.Z) {
                player1Inputs.circle = true;
            }
            if (keycode == Input.Keys.X) {
                player1Inputs.cross = true;
            }
            if (keycode == Input.Keys.C) {
                player1Inputs.square = true;
            }
            if (keycode == Input.Keys.V) {
                player1Inputs.triangle = true;
            }

            if (keycode == Input.Keys.I) {
                player2Inputs.y = 1f;
            } else if (keycode == Input.Keys.K) {
                player2Inputs.y = -1f;
            }
            if (keycode == Input.Keys.J) {
                player2Inputs.x = 1f;
            } else if (keycode == Input.Keys.L) {
                player2Inputs.x = -1f;
            }
            if (keycode == Input.Keys.U) {
                player2Inputs.rotateX = 1f;
            } else if (keycode == Input.Keys.O) {
                player2Inputs.rotateX = -1f;
            }
            if (keycode == Input.Keys.SHIFT_RIGHT) {
                player2Inputs.rightTrigger = 1f;
            }
            if (keycode == Input.Keys.N) {
                player2Inputs.circle = true;
            }
            if (keycode == Input.Keys.M) {
                player2Inputs.cross = true;
            }
            if (keycode == Input.Keys.COMMA) {
                player2Inputs.square = true;
            }
            if (keycode == Input.Keys.PERIOD) {
                player2Inputs.triangle = true;
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
        if (keycode == Input.Keys.H && challenge instanceof AbstractChallenge) {
            if (renderingContext.showRovers && !renderingContext.showPlan) {
                renderingContext.showRovers = true;
                renderingContext.showPlan = true;
            } else if (renderingContext.showRovers && renderingContext.showPlan) {
                renderingContext.showRovers = false;
                renderingContext.showPlan = true;
            } else {
                renderingContext.showRovers = true;
                renderingContext.showPlan = false;
            }
        }
        if (keycode == Input.Keys.G && challenge instanceof AbstractChallenge) {
            renderingContext.showShadows = !renderingContext.showShadows;
        }
        if (keycode == Input.Keys.T && challenge instanceof AbstractChallenge) {
            renderingContext.showRovers = !renderingContext.showRovers;
        }
        if (keycode == Input.Keys.B && challenge instanceof AbstractChallenge) {
            renderingContext.renderBackground = !renderingContext.renderBackground;
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
        if (keycode == Input.Keys.F) {
            drawFPS = !drawFPS;
        }

        if (keycode == Input.Keys.W) {
            if (Gdx.input.isKeyPressed(Input.Keys.S)) {
                player1Inputs.y = -1f;
            } else {
                player1Inputs.y = 0f;
            }
        } else if (keycode == Input.Keys.S) {
            if (Gdx.input.isKeyPressed(Input.Keys.W)) {
                player1Inputs.y = 1f;
            } else {
                player1Inputs.y = 0f;
            }
        }
        if (keycode == Input.Keys.A) {
            if (Gdx.input.isKeyPressed(Input.Keys.D)) {
                player1Inputs.x = -1f;
            } else {
                player1Inputs.x = 0f;
            }
        } else if (keycode == Input.Keys.D) {
            if (Gdx.input.isKeyPressed(Input.Keys.A)) {
                player1Inputs.x = 1f;
            } else {
                player1Inputs.x = 0f;
            }
        }
        if (keycode == Input.Keys.Q) {
            if (Gdx.input.isKeyPressed(Input.Keys.E)) {
                player1Inputs.rotateX = -1f;
            } else {
                player1Inputs.rotateX = 0f;
            }
        } else if (keycode == Input.Keys.E) {
            if (Gdx.input.isKeyPressed(Input.Keys.Q)) {
                player1Inputs.rotateX = 1f;
            } else {
                player1Inputs.rotateX = 0f;
            }
        }
        if (keycode == Input.Keys.SHIFT_LEFT) {
            player1Inputs.rightTrigger = 0;
        }
        if (keycode == Input.Keys.Z) {
            player1Inputs.circle = false;
        }
        if (keycode == Input.Keys.X) {
            player1Inputs.cross = false;
        }
        if (keycode == Input.Keys.C) {
            player1Inputs.square = false;
        }
        if (keycode == Input.Keys.V) {
            player1Inputs.triangle = false;
        }

        if (keycode == Input.Keys.I) {
            if (Gdx.input.isKeyPressed(Input.Keys.K)) {
                player2Inputs.y = -1f;
            } else {
                player2Inputs.y = 0f;
            }
        } else if (keycode == Input.Keys.K) {
            if (Gdx.input.isKeyPressed(Input.Keys.I)) {
                player2Inputs.y = 1f;
            } else {
                player2Inputs.y = 0f;
            }
        }
        if (keycode == Input.Keys.J) {
            if (Gdx.input.isKeyPressed(Input.Keys.L)) {
                player2Inputs.x = -1f;
            } else {
                player2Inputs.x = 0f;
            }
        } else if (keycode == Input.Keys.L) {
            if (Gdx.input.isKeyPressed(Input.Keys.J)) {
                player2Inputs.x = 1f;
            } else {
                player2Inputs.x = 0f;
            }
        }
        if (keycode == Input.Keys.U) {
            if (Gdx.input.isKeyPressed(Input.Keys.O)) {
                player2Inputs.rotateX = -1f;
            } else {
                player2Inputs.rotateX = 0f;
            }
        } else if (keycode == Input.Keys.O) {
            if (Gdx.input.isKeyPressed(Input.Keys.U)) {
                player2Inputs.rotateX = 1f;
            } else {
                player2Inputs.rotateX = 0f;
            }
        }
        if (keycode == Input.Keys.SHIFT_RIGHT) {
            player2Inputs.rightTrigger = 0;
        }
        if (keycode == Input.Keys.M) {
            player2Inputs.circle = false;
        }
        if (keycode == Input.Keys.N) {
            player2Inputs.cross = false;
        }
        if (keycode == Input.Keys.COMMA) {
            player2Inputs.square = false;
        }
        if (keycode == Input.Keys.PERIOD) {
            player2Inputs.triangle = false;
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

    @Override public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        this.touchX = screenX;
        this.touchY = screenY;

        return false;
    }

    @Override public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        player1Inputs.y = 0f;
        player1Inputs.rotateX = 0f;
        this.touchX = -1;
        this.touchY = -1;
        return false;
    }

    @Override public boolean touchDragged(int screenX, int screenY, int pointer) {
        if (this.touchX < 0) { this.touchX = screenX; }
        if (this.touchY < 0) { this.touchY = screenY; }

        int xDelta = screenX - touchX;
        int yDelta = screenY - touchY;
        int absXDelta = xDelta >= 0 ? xDelta : -xDelta;
        int absYDelta = yDelta >= 0 ? yDelta : -yDelta;

        if (absXDelta > absYDelta) {
            if (xDelta > ARROW_WIDTH / 2) {
                player1Inputs.rotateX = -1f;
            } else if (xDelta < -ARROW_WIDTH / 2) {
                player1Inputs.rotateX = 1f;
            } else {
                player1Inputs.rotateX = 0f;
            }
            player1Inputs.y = 0f;
        } else if (absYDelta > absXDelta) {
            if (yDelta > ARROW_HEIGHT / 2) {
                player1Inputs.y = -1f;
            } else if (yDelta < -ARROW_HEIGHT / 2) {
                player1Inputs.y = 1f;
            } else {
                player1Inputs.y = 0f;
            }
            player1Inputs.rotateX = 0f;
        } else {
            player1Inputs.x = 0f;
            player1Inputs.rotateX = 0f;
        }

        return false;
    }

    @Override public boolean mouseMoved(int screenX, int screenY) { return false; }

    @Override public boolean scrolled(int amount) { return false; }

    protected float textWidth(BitmapFont font, String text) {
        glyphLayout.setText(font, text);
        return glyphLayout.width;
    }
}
