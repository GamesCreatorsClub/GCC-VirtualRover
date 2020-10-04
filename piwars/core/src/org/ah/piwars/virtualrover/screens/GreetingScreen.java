package org.ah.piwars.virtualrover.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import org.ah.piwars.virtualrover.MainGame;
import org.ah.piwars.virtualrover.PlatformSpecific;
import org.ah.piwars.virtualrover.PlatformSpecific.RegistrationCallback;
import org.ah.piwars.virtualrover.ServerCommunicationAdapter;
import org.ah.piwars.virtualrover.challenges.ChallengeArena;
import org.ah.piwars.virtualrover.challenges.ChallengeDescription;
import org.ah.piwars.virtualrover.challenges.Challenges;
import org.ah.piwars.virtualrover.game.rovers.Rover;
import org.ah.piwars.virtualrover.game.rovers.RoverType;
import org.ah.piwars.virtualrover.rovers.AbstractRoverModel;
import org.ah.piwars.virtualrover.rovers.CBiSRoverModel;
import org.ah.piwars.virtualrover.rovers.GCCRoverModelM16;
import org.ah.piwars.virtualrover.rovers.GCCRoverModelM18;
import org.ah.piwars.virtualrover.utils.SoundManager;
import org.ah.piwars.virtualrover.view.Console;
import org.ah.themvsus.engine.client.CommonServerCommunicationAdapter.AuthenticatedCallback;
import org.ah.themvsus.engine.client.CommonServerCommunicationAdapter.GameMapCallback;
import org.ah.themvsus.engine.client.CommonServerCommunicationAdapter.GameReadyCallback;
import org.ah.themvsus.engine.client.CommonServerCommunicationAdapter.ReceivedRegistrationServerCallback;
import org.ah.themvsus.engine.client.ServerCommunication.ServerConnectionCallback;

import java.util.ArrayList;
import java.util.List;

import static org.ah.piwars.virtualrover.MainGame.SCALE;
import static org.ah.piwars.virtualrover.screens.AbstractStandardScreen.UP;

public class GreetingScreen implements Screen, InputProcessor, AuthenticatedCallback, GameReadyCallback, GameMapCallback, ReceivedRegistrationServerCallback, ControllerListener {

    private enum State {
        None(false, false),
        SelectChallenge(true, false),
        SelectRover(true, true),
        ReadyToStart(true, true),
        SignInServer(false, false),
        SignInUsername(false, false),
        SignInPassword(false, false),
        RegisterServer(false, false),
        RegisterEmail(false, false),
        RegisterEmail2(false, false),
        RegisterUsername(false, false),
        RegisterPassword(false, false),
        RegisterPassword2(false, false);

        public boolean displaySelectingChallenge;
        public boolean displaySelectingRovers;

        State(boolean displaySelectingChallenge, boolean displaySelectingRovers) {
            this.displaySelectingChallenge = displaySelectingChallenge;
            this.displaySelectingRovers = displaySelectingRovers;
        }
    }

    private static RoverType[] ROVER_TYPE_VALUES = RoverType.values();

    private static final String START_GAME_TEXT = "Press SPACE to start";

    private static final int CHALLENGE_SELECTION_WIDTH = 160;
    private static final int CHALLENGE_SELECTION_HEIGHT = 128;
    private static final int CORNER_WIDTH = 32;
    private static final float TOP_OFFSET = 40;
    private static final int ROVER_SELECTION_WIDTH = 160;
    private static final int ROVER_SELECTION_HEIGHT = 128;
    private static final int MARGIN = 20;

    private MainGame mainGame;
    private PlatformSpecific platformSpecific;
    private ServerCommunicationAdapter serverCommunicationAdapter;

    private ModelBatch modelBatch;
    protected Environment environment;
    protected DirectionalLight directionalLight;

    private Local3DDisplay challenge;
    private Local3DDisplay rover1;
    private Local3DDisplay rover2;

    private SpriteBatch cornerBatch;
    private OrthographicCamera cornerCamera;
    private FrameBuffer cornerFrameBuffer;
    private Texture cornerTexture;

    private SpriteBatch spriteBatch;
    private ShapeRenderer shapeRenderer;
    private BitmapFont font;
    private OrthographicCamera camera;
    private Viewport viewport;

    private Stage stage;
    private Skin skin;
    private TextButton signInButton;
    private TextButton registerButton;
    private TextButtonStyle activeTextButtonStyle;

    private Console console;

    private float screenWidth;
    private float screenHeight;

    private State state = State.SelectChallenge;
    private String serverAddress;
    private int serverPort;
    private String username;
    private String email;
    private String password;

    private String mapId;
    private int playerId;

    private Preferences preferences;

    private boolean doStartGame = false;
    private boolean doLoadMap;
    private AssetManager assetManager;
//    private SoundManager soundManager;
//    private ModelFactory modelFactory;
    private Challenges challenges;

    private int currentlySelectedChallengeIndex;
    private ChallengeDescription currentlySelectedChallengeDescription;
    private ChallengeArena currentlySelectedChallengeArena;

    private int currentlySelectedRover1Index;
    private RoverType currentlySelectedRover1Type;
    private AbstractRoverModel currentlySelectedRover1Model;

    private int currentlySelectedRover2Index;
    private RoverType currentlySelectedRover2Type;
    private AbstractRoverModel currentlySelectedRover2Model;

    private long flashTime = 0;
    private boolean flash = true;

    public GreetingScreen(MainGame game,
            PlatformSpecific platformSpecific,
            AssetManager assetManager,
            SoundManager soundManager,
            Challenges challenges,
            ServerCommunicationAdapter serverCommunicationAdapter,
            Console console) {

        this.mainGame = game;
        this.platformSpecific = platformSpecific;
        this.assetManager = assetManager;
        this.challenges = challenges;
        this.assetManager = assetManager;
//        this.soundManager = soundManager;
//        this.modelFactory = modelFactory;
        this.serverCommunicationAdapter = serverCommunicationAdapter;
        this.console = console;

        create(game, platformSpecific, serverCommunicationAdapter, console);
    }

    private ServerConnectionCallback serverConnectionCallback = new ServerConnectionCallback() {
        @Override public void successful() {
            console.raw("Connected to " + serverAddress + ":" + serverPort);

            setupToReadSignInUsername();
        }

        @Override public void failed(String msg) {
            console.raw("Failed to connecto the server");
        }
    };

    private Rover roverGameObject;

    private float startGameTextWidth;

    private List<Controller> connectedControllers = new ArrayList<>();

    public void create(MainGame mainGame, PlatformSpecific platformSpecific, ServerCommunicationAdapter serverCommunicationAdapter, Console console) {
        this.mainGame = mainGame;
        this.platformSpecific = platformSpecific;
        this.serverCommunicationAdapter = serverCommunicationAdapter;
        this.console = console;

        font = new BitmapFont(Gdx.files.internal("font/copper18.fnt"), Gdx.files.internal("font/copper18.png"), false);

        spriteBatch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        shapeRenderer.setAutoShapeType(true);

        camera = new OrthographicCamera(screenWidth, screenHeight);
        camera.setToOrtho(true);

        modelBatch = new ModelBatch();

        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.6f, 0.6f, 0.6f, 1f));
        directionalLight = new DirectionalLight();
        directionalLight.set(1f, 1f, 1f, new Vector3(-0.5f, -1f, 0.5f));
        environment.add(directionalLight);

        createScene();

        preferences = Gdx.app.getPreferences("org.ah.themvsus.server-details");

        currentlySelectedChallengeDescription = challenges.getAvailableChallenges().get(currentlySelectedChallengeIndex);
        currentlySelectedChallengeArena = challenges.getChallengeArena(currentlySelectedChallengeDescription.getName());

        roverGameObject = new Rover(null, 0, RoverType.GCCM16) { };
        updateCurrentlySelectedRover1();
        updateCurrentlySelectedRover2();

        challenge = new Local3DDisplay(
                CHALLENGE_SELECTION_WIDTH,
                CHALLENGE_SELECTION_HEIGHT,
                currentlySelectedChallengeArena.getWidth(),
                currentlySelectedChallengeArena.getLength()) {

                    @Override
                    public void drawModel(RenderingContext renderingContext) {
                        currentlySelectedChallengeArena.render(renderingContext, currentlySelectedChallengeArena.defaultVisibleObjets());
                    }
        };

        rover1 = new Local3DDisplay(
                ROVER_SELECTION_WIDTH,
                ROVER_SELECTION_HEIGHT,
                210,
                290) {

                    @Override
                    public void drawModel(RenderingContext renderingContext) {
                        currentlySelectedRover1Model.render(renderingContext.modelBatch, renderingContext.environment);
                    }
        };

        rover2 = new Local3DDisplay(
                ROVER_SELECTION_WIDTH,
                ROVER_SELECTION_HEIGHT,
                210,
                290) {

                    @Override
                    public void drawModel(RenderingContext renderingContext) {
                        currentlySelectedRover2Model.render(renderingContext.modelBatch, renderingContext.environment);
                    }
        };

        createCorner();

        GlyphLayout layout = new GlyphLayout(); //dont do this every frame! Store it as member
        layout.setText(font, START_GAME_TEXT);
        startGameTextWidth = layout.width;
    }

    private void createCorner() {
        cornerBatch = new SpriteBatch();
        cornerCamera = new OrthographicCamera(CORNER_WIDTH, CORNER_WIDTH);
        cornerCamera.setToOrtho(true, CORNER_WIDTH, CORNER_WIDTH);

        cornerFrameBuffer = new FrameBuffer(Format.RGBA8888, CORNER_WIDTH, CORNER_WIDTH, false);
        cornerTexture = cornerFrameBuffer.getColorBufferTexture();

        ShapeRenderer shapeRenderer = new ShapeRenderer();
        shapeRenderer.setAutoShapeType(true);
        shapeRenderer.setProjectionMatrix(cornerCamera.combined);
        cornerFrameBuffer.begin();
        Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        shapeRenderer.begin(ShapeType.Filled);
        shapeRenderer.setColor(1.0f, 1.0f, 1.0f, 1.0f);
        shapeRenderer.circle(CORNER_WIDTH, CORNER_WIDTH, CORNER_WIDTH);
        shapeRenderer.end();
        cornerFrameBuffer.end();
    }

    private void createScene() {
        viewport = new ScreenViewport(camera);
        stage = new Stage(viewport, spriteBatch);
        skin = new Skin();

        // Generate a 1x1 white texture and store it in the skin named "white".
        Pixmap pixmap = new Pixmap(1, 1, Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        skin.add("white", new Texture(pixmap));
        skin.add("default", font);

        activeTextButtonStyle = new TextButtonStyle();
        activeTextButtonStyle.up = skin.newDrawable("white", Color.DARK_GRAY);
        activeTextButtonStyle.down = skin.newDrawable("white", Color.DARK_GRAY);
        activeTextButtonStyle.checked = skin.newDrawable("white", Color.BLUE);
        activeTextButtonStyle.over = skin.newDrawable("white", Color.LIGHT_GRAY);
        activeTextButtonStyle.font = skin.getFont("default");
        activeTextButtonStyle.fontColor = Color.WHITE;
        activeTextButtonStyle.disabled = skin.newDrawable("white", Color.DARK_GRAY);
        activeTextButtonStyle.disabledFontColor = Color.GRAY;

        skin.add("default", activeTextButtonStyle);
        skin.add("disabled", activeTextButtonStyle);

        signInButton = new TextButton("Sign in", skin);
        signInButton.setSize(100, 32);
        signInButton.setChecked(true);
        signInButton.setProgrammaticChangeEvents(false);

        registerButton = new TextButton("Register", skin, "disabled");
        registerButton.setSize(100, 32);
        registerButton.setDisabled(true);
        registerButton.setProgrammaticChangeEvents(false);

        stage.addActor(signInButton);
        stage.addActor(registerButton);

        signInButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (signInButton.isDisabled()) {
                    event.cancel();
                } else {
                    if (signInButton.isChecked()) {
                        registerButton.setChecked(false);
                        setupToReadSignInUsername();
                    } else if (!registerButton.isChecked()) {
                        event.cancel();
                    }
                }
            }
        });

        registerButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (registerButton.isDisabled()) {
                    event.cancel();
                } else {
                    if (registerButton.isChecked()) {
                        signInButton.setChecked(false);
                        setupToReadRegisterEmail();
                    } else if (!signInButton.isChecked()) {
                        event.cancel();
                    }
                }
            }
        });
    }

    public void reset() {
        currentlySelectedChallengeIndex = 0;
        currentlySelectedChallengeDescription = challenges.getAvailableChallenges().get(currentlySelectedChallengeIndex);
        currentlySelectedChallengeArena = challenges.getChallengeArena(currentlySelectedChallengeDescription.getName());

        serverCommunicationAdapter.setAuthenticatedCallback(this);
        serverCommunicationAdapter.setGameMapCallback(this);
        serverCommunicationAdapter.setGameReadyCallback(this);
        serverCommunicationAdapter.setReceivedRegistrationServerCallback(this);

        doStartGame = false;
        // setupToReadSignInServer();
        state = State.SelectChallenge;
        updateCurrentlySelectedChallenge();
    }

    private void doLoadMap() {
        serverCommunicationAdapter.startEngine(mapId, playerId, false, platformSpecific.isSimulation());
    }

    private void startRemoteGame() {
        mainGame.setChallengeScreen(mapId, 1, false);
    }

    private void updateCurrentlySelectedChallenge() {
        if (currentlySelectedChallengeIndex < 0) {
            currentlySelectedChallengeIndex = challenges.getAvailableChallenges().size() - 1;
        } else if (currentlySelectedChallengeIndex >= challenges.getAvailableChallenges().size()) {
            currentlySelectedChallengeIndex = 0;
        }
        currentlySelectedChallengeDescription = challenges.getAvailableChallenges().get(currentlySelectedChallengeIndex);
        currentlySelectedChallengeArena = challenges.getChallengeArena(currentlySelectedChallengeDescription.getName());
        challenge.setModelDimensions(currentlySelectedChallengeArena.getWidth(), currentlySelectedChallengeArena.getLength());
    }

    private void updateCurrentlySelectedRover1() {
        if (currentlySelectedRover1Index < 0) {
            currentlySelectedRover1Index = ROVER_TYPE_VALUES.length - 1;
        } else if (currentlySelectedRover1Index >= ROVER_TYPE_VALUES.length) {
            currentlySelectedRover1Index = 0;
        }
        currentlySelectedRover1Type = ROVER_TYPE_VALUES[currentlySelectedRover1Index];
        if (currentlySelectedRover1Model != null) {
            currentlySelectedRover1Model.dispose();
        }
        if (currentlySelectedRover1Type == RoverType.GCCM16) {
            currentlySelectedRover1Model = new GCCRoverModelM16(assetManager);
        } else if (currentlySelectedRover1Type == RoverType.GCCM18) {
            currentlySelectedRover1Model = new GCCRoverModelM18(assetManager);
        } else if (currentlySelectedRover1Type == RoverType.CBIS) {
            currentlySelectedRover1Model = new CBiSRoverModel(assetManager);
        }
        currentlySelectedRover1Model.update(roverGameObject);
    }

    private void updateCurrentlySelectedRover2() {
        if (currentlySelectedRover2Index < 0) {
            currentlySelectedRover2Index = ROVER_TYPE_VALUES.length - 1;
        } else if (currentlySelectedRover2Index >= ROVER_TYPE_VALUES.length) {
            currentlySelectedRover2Index = 0;
        }
        currentlySelectedRover2Type = ROVER_TYPE_VALUES[currentlySelectedRover2Index];
        if (currentlySelectedRover2Model != null) {
            currentlySelectedRover2Model.dispose();
        }
        if (currentlySelectedRover2Type == RoverType.GCCM16) {
            currentlySelectedRover2Model = new GCCRoverModelM16(assetManager);
        } else if (currentlySelectedRover2Type == RoverType.GCCM18) {
            currentlySelectedRover2Model = new GCCRoverModelM18(assetManager);
        } else if (currentlySelectedRover2Type == RoverType.CBIS) {
            currentlySelectedRover2Model = new CBiSRoverModel(assetManager);
        }
        currentlySelectedRover2Model.update(roverGameObject);
    }

    @Override
    public void render(float delta) {
        if (flashTime < System.currentTimeMillis()) {
            flash = !flash;
            flashTime = System.currentTimeMillis() + 1000;
        }

        if (state.displaySelectingChallenge) {
            challenge.update();
            challenge.render();
            challenge.setPosition((screenWidth - CHALLENGE_SELECTION_WIDTH) / 2, screenHeight - CHALLENGE_SELECTION_HEIGHT - TOP_OFFSET);
        }
        if (state.displaySelectingRovers) {
            if (currentlySelectedChallengeDescription.getMaxRovers() == 2) {
                rover1.update();
                rover1.render();
                rover1.setPosition(
                        screenWidth * 5 / 16 - CHALLENGE_SELECTION_WIDTH / 2,
                        screenHeight - CHALLENGE_SELECTION_HEIGHT - TOP_OFFSET - ROVER_SELECTION_HEIGHT - MARGIN);
                rover2.update();
                rover2.render();
                rover2.setPosition(
                        screenWidth * 11 / 16 - CHALLENGE_SELECTION_WIDTH / 2,
                        screenHeight - CHALLENGE_SELECTION_HEIGHT - TOP_OFFSET - ROVER_SELECTION_HEIGHT - MARGIN);
            } else {
                rover1.update();
                rover1.render();
                rover1.setPosition(
                        (screenWidth - CHALLENGE_SELECTION_WIDTH) / 2,
                        screenHeight - CHALLENGE_SELECTION_HEIGHT - TOP_OFFSET - ROVER_SELECTION_HEIGHT - MARGIN);
            }
        }

        updateCamera();

        Gdx.gl.glClearColor(0.35f, 0.21f, 0.06f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        spriteBatch.setProjectionMatrix(camera.combined);
        shapeRenderer.setProjectionMatrix(camera.combined);

        shapeRenderer.begin();
        if (state.displaySelectingChallenge) { challenge.drawTextureBorder(shapeRenderer); }
        if (state.displaySelectingRovers) { rover1.drawTextureBorder(shapeRenderer); }
        shapeRenderer.end();
        spriteBatch.begin();
        drawStateHelpText(spriteBatch);
        if (state.displaySelectingChallenge) {
            challenge.drawTexture(spriteBatch);
            font.draw(spriteBatch, currentlySelectedChallengeDescription.getDescription(), (screenWidth + CHALLENGE_SELECTION_WIDTH) / 2 + 20, screenHeight - CHALLENGE_SELECTION_HEIGHT / 2 - TOP_OFFSET);
        }
        if (state.displaySelectingRovers) {
            if (currentlySelectedChallengeDescription.getMaxRovers() == 2) {
                rover1.drawTexture(spriteBatch);
                rover2.drawTexture(spriteBatch);
                font.draw(spriteBatch, currentlySelectedRover1Type.getName(),
                        screenWidth * 5 / 16 - CHALLENGE_SELECTION_WIDTH / 2 + 20,
                        screenHeight - CHALLENGE_SELECTION_HEIGHT - TOP_OFFSET - ROVER_SELECTION_HEIGHT - MARGIN * 2);

                font.draw(spriteBatch, currentlySelectedRover2Type.getName(),
                        screenWidth * 11 / 16 - CHALLENGE_SELECTION_WIDTH / 2 + 20,
                        screenHeight - CHALLENGE_SELECTION_HEIGHT - TOP_OFFSET - ROVER_SELECTION_HEIGHT - MARGIN * 2);
            } else {
                rover1.drawTexture(spriteBatch);
                font.draw(spriteBatch, currentlySelectedRover1Type.getName(),
                        (screenWidth + CHALLENGE_SELECTION_WIDTH) / 2 + 20,
                        screenHeight - CHALLENGE_SELECTION_HEIGHT / 2 - TOP_OFFSET - ROVER_SELECTION_HEIGHT - MARGIN);
            }
        }
        if (state == State.ReadyToStart && flash) {

            font.draw(spriteBatch, START_GAME_TEXT,
                (screenWidth - startGameTextWidth) / 2,
                screenHeight - CHALLENGE_SELECTION_HEIGHT / 2 - TOP_OFFSET - ROVER_SELECTION_HEIGHT * 2f - MARGIN *2 );
        }

        spriteBatch.end();

        stage.draw();

        console.render();

        if (doLoadMap) {
            doLoadMap();
        }

        if (doStartGame) {
            startRemoteGame();
        }
    }

    private void drawStateHelpText(SpriteBatch batch) {
        if (state == State.SelectChallenge) {
            font.draw(batch, "Select Challenge", 10,
                    screenHeight - CHALLENGE_SELECTION_HEIGHT / 2 - TOP_OFFSET);
        } else if (state == State.SelectRover) {
            font.draw(batch, "Select Rover", 10,
                    screenHeight - CHALLENGE_SELECTION_HEIGHT / 2 - TOP_OFFSET - ROVER_SELECTION_HEIGHT - MARGIN);
        }
    }

    @Override
    public void resize(int width, int height) {
        this.screenWidth = width;
        this.screenHeight = height;
        console.setConsoleWidth(width);
        stage.getViewport().update(width, height);
        camera.setToOrtho(true, width, height);
        signInButton.setPosition(width - 200, height - 32);
        registerButton.setPosition(width - 100, height - 32);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void show() {
        console.setCamera(camera);
        Gdx.input.setOnscreenKeyboardVisible(platformSpecific.needOnScreenKeyboard());
        Gdx.input.setInputProcessor(this);
        connectedControllers.clear();
        for (Controller controller : Controllers.getControllers()) {
            connectedControllers.add(controller);
        }
        Controllers.addListener(this);
    }

    @Override
    public void hide() {
        Controllers.removeListener(this);
    }

    @Override
    public void dispose() {
        spriteBatch.dispose();
        challenge.dispose();
        cornerTexture.dispose();
        cornerFrameBuffer.dispose();
    }

    public void updateCamera() {
        camera.setToOrtho(false, screenWidth, screenHeight);
        camera.update();
    }

    public void connectToServer(String serverAddress, int serverPort) {
        state = State.None;
        // doConnectToServer = true;
        serverCommunicationAdapter.connectToServer(serverAddress, serverPort, serverConnectionCallback);
    }

    private void previous(int player) {
        if (state == State.SelectChallenge) {
            currentlySelectedChallengeIndex = currentlySelectedChallengeIndex - 1;
            updateCurrentlySelectedChallenge();
        } else if (state == State.SelectRover) {
            if (player == 0) {
                currentlySelectedRover1Index = currentlySelectedRover1Index - 1;
                updateCurrentlySelectedRover1();
            }
            if (player == 1) {
                currentlySelectedRover2Index = currentlySelectedRover2Index - 1;
                updateCurrentlySelectedRover2();
            }
        }
    }

    private void next(int player) {
        if (state == State.SelectChallenge) {
            currentlySelectedChallengeIndex = currentlySelectedChallengeIndex + 1;
            updateCurrentlySelectedChallenge();
        } else if (state == State.SelectRover) {
            if (player == 0) {
                currentlySelectedRover1Index = currentlySelectedRover1Index + 1;
                updateCurrentlySelectedRover1();
            }
            if (player == 1) {
                currentlySelectedRover2Index = currentlySelectedRover2Index + 1;
                updateCurrentlySelectedRover2();
            }
        }
    }

    private void select(int player) {
        if (state == State.SelectChallenge) {
            state = State.SelectRover;
        } else if (state == State.SelectRover) {
            state = State.ReadyToStart;
            flashTime = System.currentTimeMillis() + 1000;
            flash = true;
        } else if (state == State.ReadyToStart) {
            mainGame.setSelectedRover1(currentlySelectedRover1Type);
            mainGame.setSelectedRover2(currentlySelectedRover2Type);
            mainGame.setChallengeScreen(currentlySelectedChallengeDescription.getName(), 1, !currentlySelectedChallengeDescription.isRemote());
        } else {
            textEntered();
        }
    }

    private void back(int player) {
        if (state == State.SelectRover) {
            state = State.SelectChallenge;
        } else if (state == State.ReadyToStart) {
            state = State.SelectRover;
        }
    }

    private void textEntered() {
        if (state == State.SignInServer) {
            boolean ok = false;
            if (console.getTypingtext().indexOf(':') > 0) {
                try {
                    String[] split = console.getTypingtext().split(":");
                    int port = Integer.parseInt(split[1]);
                    serverAddress = split[0];
                    serverPort = port;
                    ok = true;
                } catch (NumberFormatException ignore) { }
            }
            if (ok) {
                preferences.putString("serverAddress", serverAddress);
                preferences.putInteger("serverPort", serverPort);
                preferences.flush();

                connectToServer(serverAddress, serverPort);
            } else {
                console.raw("Please enter correct server address in form <host-ip-or-name>:<port>");
                console.startTyping("Server: ", false);
            }
        } else if (state == State.SignInUsername) {
            setupToReadSignInPassword();
        } else if (state == State.SignInPassword) {
            state = State.None;
            console.stopTyping();
            password = console.getTypingtext();
            doSignIn();
        } else if (state == State.RegisterServer) {
            boolean ok = false;
            if (console.getTypingtext().indexOf(':') > 0) {
                try {
                    String[] split = console.getTypingtext().split(":");
                    int port = Integer.parseInt(split[1]);
                    serverAddress = split[0];
                    serverPort = port;
                    ok = true;
                } catch (NumberFormatException ignore) { }
            }
            if (ok) {
                setupToReadRegisterEmail();
            } else {
                console.raw("Please enter correct server address in form <host-ip-or-name>:<port>");
                console.startTyping("Server: ", false);
            }
        } else if (state == State.RegisterEmail) {
            email = console.getTypingtext();
            if (email.contains("@")) {
                setupToReadRegisterEmail2();
            } else {
                console.raw("Please enter correct email address");
                setupToReadRegisterEmail();
            }
        } else if (state == State.RegisterEmail2) {
            String email2 = console.getTypingtext();
            if (email.equalsIgnoreCase(email2)) {
                setupToReadRegisterPassword();
            } else {
                console.raw("Email address does not match - try again");
                setupToReadRegisterEmail();
            }
        } else if (state == State.RegisterPassword) {
            password = console.getTypingtext();
            if (password.length() > 4) {
                setupToReadRegisterPassword2();
            } else {
                console.raw("Wrong password. Try longer more complex password.");
                setupToReadRegisterPassword();
            }
        } else if (state == State.RegisterPassword2) {
            String password2 = console.getTypingtext();
            if (password.equals(password2)) {
                setupToReadRegisterUsername();
            } else {
                console.raw("Password does not match. Try again.");
                setupToReadRegisterPassword();
            }
        } else if (state == State.RegisterUsername) {
            username = console.getTypingtext();
            if (username.length() > 3) {
                state = State.None;
                console.stopTyping();
                doRegister();
                signInButton.setChecked(true);
                registerButton.setChecked(false);
            } else {
                console.raw("Username is too short.");
                setupToReadRegisterUsername();
            }
        } else if (state == State.None) {
            reset();
        }
    }

    @SuppressWarnings("unused")
    private void setupToReadSignInServer() {
        state = State.SignInServer;

        if (platformSpecific.readServerDetails()) {
            console.startTyping("Server: ", false);

            if (serverAddress == null) {
                String preferredServerAddress = platformSpecific.getPreferredServerAddress();
                if (serverAddress == null) {
                    serverAddress = preferredServerAddress;
                    serverPort = platformSpecific.getPreferredServerPort();
                }
                if (serverAddress == null) {
                    serverAddress = preferences.getString("serverAddress");
                    serverPort = preferences.getInteger("serverPort");
                }
            }
            if (serverAddress != null) {
                console.setTypingtext(serverAddress + ":" + serverPort);
            } else {
                console.setTypingtext("");
            }
        } else {
            serverAddress = platformSpecific.getPreferredServerAddress();
            serverPort = platformSpecific.getPreferredServerPort();

            serverCommunicationAdapter.connectToServer(serverAddress, serverPort, serverConnectionCallback);
            setupToReadSignInUsername();
        }
    }

    private void setupToReadSignInUsername() {
        state = State.SignInUsername;
        console.startTyping("Username: ", false);
        String username = preferences.getString("username");
        if (username == null) { username = ""; }
        console.setTypingtext(username);
    }

    private void setupToReadSignInPassword() {
        state = State.SignInPassword;
        username = console.getTypingtext();
        console.startTyping("Password: ", true);
        console.setTypingtext("");
    }

    private void doSignIn() {
        preferences.putString("username", username);
        preferences.flush();

        serverCommunicationAdapter.authenticate(username, password);
    }

    private void doRegister() {
        console.stopTyping();
        state = State.None;
        preferences.putString("username", username);
        preferences.flush();

        String registrationServerURL = serverCommunicationAdapter.getRegistrationServerURL();
        console.debug("Trying to register at " + registrationServerURL);

        platformSpecific.register(registrationServerURL, username, email, password, new RegistrationCallback() {
            @Override public void success() {
                console.debug("Registeration sent. Check your e-mail to finish the process.");
            }

            @Override public void failure(String message) {
                console.debug("Failed registering: " + message);
            }
        });
    }

    private void setupToReadRegisterEmail() {
        state = State.RegisterEmail;
        console.startTyping("Email: ", false);
    }

    private void setupToReadRegisterEmail2() {
        state = State.RegisterEmail2;
        console.startTyping("Re-enter email: ", false);
    }

    private void setupToReadRegisterPassword() {
        state = State.RegisterPassword;
        console.startTyping("Password: ", true);
    }

    private void setupToReadRegisterPassword2() {
        state = State.RegisterPassword2;
        console.startTyping("Re-enter password: ", true);
    }

    private void setupToReadRegisterUsername() {
        state = State.RegisterUsername;
        console.startTyping("Choose username: ", false);
    }

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Keys.LEFT || keycode == Keys.A) {
            previous(0);
        } else if (keycode == Keys.J) {
            previous(1);
        } else if (keycode == Keys.RIGHT || keycode == Keys.D) {
            next(0);
        } else if (keycode == Keys.L) {
            next(1);
        } else if (keycode == Keys.DOWN || keycode == Keys.S || keycode == Keys.SPACE || keycode == Keys.ENTER) {
            select(0);
        } else if (keycode == Keys.K) {
            select(1);
        } else if (keycode == Keys.ESCAPE || keycode == Keys.W || keycode == Keys.UP) {
            back(0);
        }
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        if (character == '\b' && console.isTyping() && console.getTypingtext().length() > 0) {
            console.setTypingtext(console.getTypingtext().substring(0, console.getTypingtext().length() - 1));
        } else if (character >= ' ') {
            console.keyTyped(character);
        }

        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return stage.touchDown(screenX, screenY, pointer, button);
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return stage.touchUp(screenX, screenY, pointer, button);
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return stage.touchDragged(screenX, screenY, pointer);
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return stage.mouseMoved(screenX, screenY);
    }

    @Override
    public boolean scrolled(int amount) {
        return stage.scrolled(amount);
    }

    @Override
    public void authenticated() {
        console.raw("Authentication successful.");
    }

    @Override
    public void receivedRegistrationServerURL() {
        registerButton.setDisabled(false);
    }

    @Override
    public void gameMap(String mapId, int sessionId) {
        this.mapId = mapId;
        // TODO this makes no sense!!!
        // this.playerId = playerId;
        doLoadMap = true;
    }

    @Override
    public void gameReady() {
        doStartGame = true;
    }

    abstract class Local3DDisplay {
        private PerspectiveCamera camera;
        private FrameBuffer frameBuffer;
        private Texture texture;
        private float rotationAngle = 0;
        private float x;
        private float y;
        private int width;
        private int height;
        private float modelWidth;
        private float modelHeight;

        private RenderingContext renderingContext;

        public Local3DDisplay(int width, int height, float modelWidth, float modelHeight) {
            this.width = width;
            this.height = height;
            this.modelWidth = modelWidth;
            this.modelHeight = modelHeight;

            create();
        }

        public void create() {
            camera = new PerspectiveCamera(45, width, height);
            camera.position.set(300f * SCALE, 3000 * SCALE, 300f * SCALE);
            camera.lookAt(0f, 0f, 0f);
            camera.near = 0.02f;
            camera.far = 1000f;
            camera.up.set(UP);
            camera.fieldOfView = 45f;
            camera.update();

            frameBuffer = new FrameBuffer(Format.RGBA8888, width, height, true);
            texture = frameBuffer.getColorBufferTexture();
            renderingContext = new RenderingContext(modelBatch, environment, frameBuffer);
        }

        public void dispose() {
            texture.dispose();
            frameBuffer.dispose();
        }

        public void setPosition(float x, float y) {
            this.x = x;
            this.y = y;
        }

        public void setModelDimensions(float modelWidth, float modelHeight) {
            this.modelWidth = modelWidth;
            this.modelHeight =  modelHeight;
        }

        public void update() {
            rotationAngle = rotationAngle + (float)(Math.PI / 180.0);
            if (rotationAngle > (float)(rotationAngle * Math.PI * 2f)) {
                rotationAngle = 0f;
            }

            float radius = modelWidth > modelHeight ? modelWidth : modelHeight;
            radius = radius * 1.2f;

            float x = (float)(Math.sin(rotationAngle) * radius) * SCALE;
            float y = (float)(Math.cos(rotationAngle) * radius) * SCALE;

            camera.position.set(x, radius * SCALE, y);
            camera.lookAt(0f, 0f, 0f);
            camera.up.set(UP);
            camera.update();
        }

        private void render() {
            renderingContext.frameBuffer.begin();
            Gdx.gl.glClearColor(0.6f, 0.75f, 1f, 1f);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
            Gdx.gl.glEnable(GL20.GL_BLEND);
            Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);

            renderingContext.modelBatch = modelBatch;
            renderingContext.modelBatch.begin(camera);
            drawModel(renderingContext);
            drawCorners(CHALLENGE_SELECTION_WIDTH, CHALLENGE_SELECTION_HEIGHT);
            renderingContext.modelBatch.end();
            renderingContext.frameBuffer.end();
        }

        private void drawCorners(int width, int height) {
            cornerCamera.setToOrtho(true, width, height);
            cornerBatch.setProjectionMatrix(cornerCamera.combined);

            cornerBatch.begin();
            cornerBatch.setBlendFunction(GL20.GL_ZERO, GL20.GL_SRC_COLOR);
            cornerBatch.draw(cornerTexture, 0, 0, CORNER_WIDTH, CORNER_WIDTH, 0, 0, CORNER_WIDTH, CORNER_WIDTH, false, false);
            cornerBatch.draw(cornerTexture, width - CORNER_WIDTH, 0, CORNER_WIDTH, CORNER_WIDTH, 0, 0, CORNER_WIDTH, CORNER_WIDTH, true, false);
            cornerBatch.draw(cornerTexture, 0,  height - CORNER_WIDTH, CORNER_WIDTH, CORNER_WIDTH, 0, 0, CORNER_WIDTH, CORNER_WIDTH, false, true);
            cornerBatch.draw(cornerTexture, width - CORNER_WIDTH, height - CORNER_WIDTH, CORNER_WIDTH, CORNER_WIDTH, 0, 0, CORNER_WIDTH, CORNER_WIDTH, true, true);
            cornerBatch.end();
        }

        public void drawTexture(SpriteBatch batch) {
            batch.draw(texture, x, y, width, height, 0, 0, width, height, false, true);
        }

        public void drawTextureBorder(ShapeRenderer shapeRenderer) {
            shapeRenderer.set(ShapeType.Filled);
            shapeRenderer.setColor(Color.YELLOW);
            shapeRenderer.rectLine(x - 1, y + CORNER_WIDTH - 2, x - 1, y + height - CORNER_WIDTH + 2, 2, Color.YELLOW, Color.YELLOW);
            shapeRenderer.rectLine(x + width + 1, y + CORNER_WIDTH - 2, x + width + 1, y + 1 + height - CORNER_WIDTH + 2, 2, Color.YELLOW, Color.YELLOW);
            shapeRenderer.rectLine(x + CORNER_WIDTH - 2, y - 1, x + width - CORNER_WIDTH + 2, y - 1, 2, Color.YELLOW, Color.YELLOW);
            shapeRenderer.rectLine(x + CORNER_WIDTH - 2, y + height + 1, x + width - CORNER_WIDTH + 2, y + height + 1, 2, Color.YELLOW, Color.YELLOW);
            //shapeRenderer.set(ShapeType.Line);
            shapeRenderer.arc(x - 2 + CORNER_WIDTH, y - 2 + CORNER_WIDTH, CORNER_WIDTH, 180, 90, 5);
            shapeRenderer.arc(x + 2 + width - CORNER_WIDTH, y - 2 + CORNER_WIDTH, CORNER_WIDTH, 270, 90, 5);
            shapeRenderer.arc(x + 2 + width - CORNER_WIDTH, y + height + 2 - CORNER_WIDTH, CORNER_WIDTH, 0, 90, 5);
            shapeRenderer.arc(x - 2 + CORNER_WIDTH, y + height + 2 - CORNER_WIDTH, CORNER_WIDTH, 90, 90, 5);
        }

        public abstract void drawModel(RenderingContext renderingContext);
    }

    @Override
    public void connected(Controller controller) {
        connectedControllers.add(controller);
        System.out.println("Added controller " + connectedControllers.indexOf(controller));
    }

    @Override
    public void disconnected(Controller controller) {
        // System.out.println("Removed controller " + connectedControllers.indexOf(controller));
        connectedControllers.remove(controller);
    }

    @Override
    public boolean buttonDown(Controller controller, int buttonCode) {
        // System.out.println("Controller " + connectedControllers.indexOf(controller) + " button " + buttonCode + " pressed.");
        return false;
    }

    @Override
    public boolean buttonUp(Controller controller, int buttonCode) {
        return false;
    }

    @Override
    public boolean axisMoved(Controller controller, int axisCode, float value) {
        // System.out.println("Controller " + connectedControllers.indexOf(controller) + " axis " + axisCode + " - " + value);
        return false;
    }

    @Override
    public boolean povMoved(Controller controller, int povCode, PovDirection value) {
        // System.out.println("Controller " + connectedControllers.indexOf(controller) + " pov " + povCode + " - " + value);
        return false;
    }

    @Override
    public boolean xSliderMoved(Controller controller, int sliderCode, boolean value) {
        return false;
    }

    @Override
    public boolean ySliderMoved(Controller controller, int sliderCode, boolean value) {
        return false;
    }

    @Override
    public boolean accelerometerMoved(Controller controller, int accelerometerCode, Vector3 value) {
        return false;
    }
}
