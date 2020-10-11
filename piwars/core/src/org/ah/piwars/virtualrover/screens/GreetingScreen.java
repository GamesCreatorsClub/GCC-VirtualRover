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
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
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
import org.ah.piwars.virtualrover.challenges.ChallengeDescription;
import org.ah.piwars.virtualrover.challenges.Challenges;
import org.ah.piwars.virtualrover.screens.greetingscreen.ChallengeSelectionActor;
import org.ah.piwars.virtualrover.screens.greetingscreen.RoversSelectionActor;
import org.ah.piwars.virtualrover.utils.SoundManager;
import org.ah.piwars.virtualrover.view.Console;
import org.ah.themvsus.engine.client.CommonServerCommunicationAdapter.AuthenticatedCallback;
import org.ah.themvsus.engine.client.CommonServerCommunicationAdapter.GameMapCallback;
import org.ah.themvsus.engine.client.CommonServerCommunicationAdapter.GameReadyCallback;
import org.ah.themvsus.engine.client.CommonServerCommunicationAdapter.ReceivedRegistrationServerCallback;
import org.ah.themvsus.engine.client.ServerCommunication.ServerConnectionCallback;

import java.util.ArrayList;
import java.util.List;

public class GreetingScreen implements Screen, InputProcessor, AuthenticatedCallback, GameReadyCallback, GameMapCallback, ReceivedRegistrationServerCallback, ControllerListener {

    private enum State {
        None(false, false),
        SelectChallenge(true, false),
        SelectRover(true, true),
//        ReadyToStart(true, true),
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

    private static final String START_GAME_TEXT = "Press SPACE to start";

    public static final int ARROW_BUTTON_WIDTH = 14;
    public static final int ARROW_BUTTON_MARGIN = 5;
    public static final int ARROW_WIDTH = 14;
    public static final int ARROW_HEIGHT = 20;
    public static final int DESCRIPTION_TEXT_MARGIN = 10;
    public static final Polygon LEFT_ARROW = new Polygon(new float[] { 0, ARROW_HEIGHT / 2, ARROW_WIDTH, 0, ARROW_WIDTH, ARROW_HEIGHT });
    public static final Polygon RIGHT_ARROW = new Polygon(new float[] { ARROW_WIDTH, ARROW_HEIGHT / 2, 0, 0, 0, ARROW_HEIGHT });
    public static final Polygon DOWN_ARROW = new Polygon(new float[] { 0, ARROW_WIDTH, ARROW_HEIGHT, ARROW_WIDTH, ARROW_HEIGHT / 2, 0 });


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

    private OrthographicCamera cornerCamera;
    private FrameBuffer cornerFrameBuffer;
    private Texture cornerTexture;

    private SpriteBatch spriteBatch;
    private ShapeRenderer shapeRenderer;
    private BitmapFont font;
    private BitmapFont fontBig;
    private OrthographicCamera camera;
    private Viewport viewport;

    private GlyphLayout glyphLayout = new GlyphLayout();

    private Stage stage;
    private Skin skin;
    private TextButton signInButton;
    private TextButton registerButton;
    private ChallengeSelectionActor challengeSelectionActor;
    private RoversSelectionActor roverSelectionActor;
    private Button startGameButton;

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

    private float startGameTextWidth;

    private List<Controller> connectedControllers = new ArrayList<>();

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

    public void create(MainGame mainGame, PlatformSpecific platformSpecific, ServerCommunicationAdapter serverCommunicationAdapter, Console console) {
        this.mainGame = mainGame;
        this.platformSpecific = platformSpecific;
        this.serverCommunicationAdapter = serverCommunicationAdapter;
        this.console = console;

        preferences = Gdx.app.getPreferences("org.ah.themvsus.server-details");

        font = assetManager.get("font/copper18.fnt");
        fontBig = assetManager.get("font/basic.fnt");

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

        createCorner();

        createScene();

        GlyphLayout layout = new GlyphLayout(); //dont do this every frame! Store it as member
        layout.setText(font, START_GAME_TEXT);
        startGameTextWidth = layout.width;

        setState(State.SelectChallenge);
    }

    private void createCorner() {
        cornerCamera = new OrthographicCamera(CORNER_WIDTH, CORNER_WIDTH);
        cornerCamera.setToOrtho(true, CORNER_WIDTH, CORNER_WIDTH);

        cornerFrameBuffer = new FrameBuffer(Format.RGBA8888, CORNER_WIDTH, CORNER_WIDTH, false);
        cornerTexture = cornerFrameBuffer.getColorBufferTexture();

        ShapeRenderer shapeRenderer = new ShapeRenderer();
        shapeRenderer.setAutoShapeType(true);
        shapeRenderer.setProjectionMatrix(cornerCamera.combined);
        cornerFrameBuffer.begin();
        Gdx.gl.glClearColor(0f, 0f, 0f, 0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        shapeRenderer.begin(ShapeType.Filled);
        shapeRenderer.setColor(1f, 1f, 1f, 1f);
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
        skin.add("defaultFont", font);
        skin.add("bigFont", fontBig);

        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = skin.getFont("defaultFont");
        labelStyle.fontColor = Color.WHITE;
        skin.add("default", labelStyle);

        TextButtonStyle activeTextButtonStyle = new TextButtonStyle();
        activeTextButtonStyle.up = skin.newDrawable("white", Color.DARK_GRAY);
        activeTextButtonStyle.down = skin.newDrawable("white", Color.DARK_GRAY);
        activeTextButtonStyle.checked = skin.newDrawable("white", Color.BLUE);
        activeTextButtonStyle.over = skin.newDrawable("white", Color.LIGHT_GRAY);
        activeTextButtonStyle.font = skin.getFont("defaultFont");
        activeTextButtonStyle.fontColor = Color.WHITE;
        activeTextButtonStyle.disabled = skin.newDrawable("white", Color.DARK_GRAY);
        activeTextButtonStyle.disabledFontColor = Color.GRAY;

        skin.add("default", activeTextButtonStyle);
        skin.add("disabled", activeTextButtonStyle);

        ButtonStyle opaqueButtonStyle = new ButtonStyle();
        opaqueButtonStyle.up = skin.newDrawable("white", new Color(0f, 0f, 0f, 0f));
        opaqueButtonStyle.down = skin.newDrawable("white", new Color(0f, 0f, 0f, 0.1f));
        opaqueButtonStyle.checked = skin.newDrawable("white", new Color(0f, 0f, 0f, 0f));
        opaqueButtonStyle.over = skin.newDrawable("white", Color.LIGHT_GRAY);
        opaqueButtonStyle.checkedOver = skin.newDrawable("white", new Color(0f, 0f, 0f, 0.1f));
        opaqueButtonStyle.disabled = skin.newDrawable("white", new Color(0f, 0f, 0f, 0f));

        skin.add("opaque", opaqueButtonStyle);

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
            @Override public void changed(ChangeEvent event, Actor actor) {
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
            @Override public void changed(ChangeEvent event, Actor actor) {
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

        challengeSelectionActor = new ChallengeSelectionActor(
                cornerTexture,
                modelBatch, environment,
                challenges,
                skin,
                CHALLENGE_SELECTION_WIDTH, CHALLENGE_SELECTION_HEIGHT);

        challengeSelectionActor.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent event, Actor actor) {
                if (actor == challengeSelectionActor) {
                    if (state == State.SelectRover) {
                        // set new selected course
                        setState(State.SelectRover);
                    } else if (state == State.SelectChallenge) {
                        select(0);
                    }
                }
            }
        });
        stage.addActor(challengeSelectionActor);

        roverSelectionActor = new RoversSelectionActor(
                cornerTexture,
                modelBatch, environment,
                assetManager,
                skin,
                ROVER_SELECTION_WIDTH, ROVER_SELECTION_HEIGHT);
//
//        roverSelectionActor.addListener(new ChangeListener() {
//            @Override public void changed(ChangeEvent event, Actor actor) {
//                if (actor == roverSelectionActor) {
//                    select(0);
//                }
//            }
//        });

        roverSelectionActor.setVisible(false);
        stage.addActor(roverSelectionActor);

        startGameButton = new TextButton("Press SPACE to start", skin);
        startGameButton.setSize(startGameButton.getPrefWidth() + ARROW_BUTTON_MARGIN * 4, startGameButton.getPrefHeight() * 2 + ARROW_BUTTON_MARGIN);
        startGameButton.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent event, Actor actor) {
                startChallenge();
            }});
        stage.addActor(startGameButton);

        resize((int)screenWidth, (int)screenHeight);
    }

    public void reset() {
        challengeSelectionActor.reset();

        serverCommunicationAdapter.setAuthenticatedCallback(this);
        serverCommunicationAdapter.setGameMapCallback(this);
        serverCommunicationAdapter.setGameReadyCallback(this);
        serverCommunicationAdapter.setReceivedRegistrationServerCallback(this);

        doStartGame = false;
        // setupToReadSignInServer();
        setState(State.SelectChallenge);
        // challengeDisplay.updateCurrentlySelectedChallenge();
    }

    private void doLoadMap() {
        serverCommunicationAdapter.startEngine(mapId, playerId, false, platformSpecific.isSimulation());
    }

    private void startRemoteGame() {
        mainGame.setChallengeScreen(mapId, 1, false);
    }

    @Override
    public void render(float delta) {
        if (flashTime < System.currentTimeMillis()) {
            flash = !flash;
            flashTime = System.currentTimeMillis() + 1000;
        }

        updateCamera();

        Gdx.gl.glClearColor(0.35f, 0.21f, 0.06f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        spriteBatch.setProjectionMatrix(camera.combined);
        shapeRenderer.setProjectionMatrix(camera.combined);

        shapeRenderer.begin();
        shapeRenderer.end();
        spriteBatch.begin();

        if (state == State.SelectRover && flash) {
            font.draw(spriteBatch, START_GAME_TEXT,
                (screenWidth - startGameTextWidth) / 2,
                screenHeight - CHALLENGE_SELECTION_HEIGHT / 2 - TOP_OFFSET - ROVER_SELECTION_HEIGHT * 2f - MARGIN *2);
        }

        spriteBatch.end();

        stage.act(delta);

        shapeRenderer.begin();
        stage.draw();
        shapeRenderer.end();

        console.render();

        if (doLoadMap) {
            doLoadMap();
        }

        if (doStartGame) {
            startRemoteGame();
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
        challengeSelectionActor.setPosition(
                (screenWidth - CHALLENGE_SELECTION_WIDTH) / 2,
                screenHeight - CHALLENGE_SELECTION_HEIGHT - TOP_OFFSET);

        roverSelectionActor.setPosition(
                (screenWidth - CHALLENGE_SELECTION_WIDTH) / 2,
                (int)(challengeSelectionActor.getY() - roverSelectionActor.getHeight() - MARGIN));

        startGameButton.setPosition((width - startGameButton.getWidth()) / 2, roverSelectionActor.getY() - startGameButton.getHeight() * 2.5f);
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
        challengeSelectionActor.dispose();
        cornerTexture.dispose();
        cornerFrameBuffer.dispose();
    }

    public void updateCamera() {
        camera.setToOrtho(false, screenWidth, screenHeight);
        camera.update();
    }

    protected void setState(State state) {
        this.state = state;

        challengeSelectionActor.setVisible(state.displaySelectingChallenge);
        challengeSelectionActor.setSelecting(!state.displaySelectingRovers);
        roverSelectionActor.setVisible(state.displaySelectingRovers);

        challengeSelectionActor.setFocused(state.displaySelectingChallenge && !state.displaySelectingRovers);

        ChallengeDescription selectedChallengeDescription = challengeSelectionActor.getSelectedChallengeDescription();
        roverSelectionActor.setVisible(state.displaySelectingRovers);

        roverSelectionActor.setTwoRoversChallenge(selectedChallengeDescription.getMaxRovers() == 2);
        startGameButton.setVisible(state.displaySelectingRovers);
    }

    public void connectToServer(String serverAddress, int serverPort) {
        setState(State.None);
        // doConnectToServer = true;
        serverCommunicationAdapter.connectToServer(serverAddress, serverPort, serverConnectionCallback);
    }

    private void previous(int player) {
        if (state == State.SelectChallenge) {
            challengeSelectionActor.previous();
        } else if (state == State.SelectRover) {
            if (player == 0) {
                roverSelectionActor.getRover1().previous();
            }
            if (player == 1) {
                roverSelectionActor.getRover2().previous();
            }
        }
    }

    private void next(int player) {
        if (state == State.SelectChallenge) {
            challengeSelectionActor.next();
        } else if (state == State.SelectRover) {
            if (player == 0) {
                roverSelectionActor.getRover1().next();
            }
            if (player == 1) {
                roverSelectionActor.getRover2().next();
            }
        }
    }

    private void select(int player) {
        if (state == State.SelectChallenge) {
            setState(State.SelectRover);
//        } else if (state == State.SelectRover) {
//            setState(State.ReadyToStart);
//            flashTime = System.currentTimeMillis() + 1000;
//            flash = true;
        } else if (state == State.SelectRover) {
            startChallenge();
        } else {
            textEntered();
        }
    }

    private void startChallenge() {
        mainGame.setSelectedRover1(roverSelectionActor.getRover1().getSelectedRoverType());
        mainGame.setSelectedRover2(roverSelectionActor.getRover2().getSelectedRoverType());

        ChallengeDescription currentlySelectedChallengeDescription = challengeSelectionActor.getSelectedChallengeDescription();
        mainGame.setChallengeScreen(currentlySelectedChallengeDescription.getName(), 1, !currentlySelectedChallengeDescription.isRemote());
    }

    private void back(int player) {
        if (state == State.SelectRover) {
            setState(State.SelectChallenge);
//        } else if (state == State.ReadyToStart) {
//            setState(State.SelectRover);
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
            setState(State.None);
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
                setState(State.None);
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
        setState(State.SignInServer);

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
        setState(State.SignInUsername);
        console.startTyping("Username: ", false);
        String username = preferences.getString("username");
        if (username == null) { username = ""; }
        console.setTypingtext(username);
    }

    private void setupToReadSignInPassword() {
        setState(State.SignInPassword);
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
        setState(State.None);
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
        setState(State.RegisterEmail);
        console.startTyping("Email: ", false);
    }

    private void setupToReadRegisterEmail2() {
        setState(State.RegisterEmail2);
        console.startTyping("Re-enter email: ", false);
    }

    private void setupToReadRegisterPassword() {
        setState(State.RegisterPassword);
        console.startTyping("Password: ", true);
    }

    private void setupToReadRegisterPassword2() {
        setState(State.RegisterPassword2);
        console.startTyping("Re-enter password: ", true);
    }

    private void setupToReadRegisterUsername() {
        setState(State.RegisterUsername);
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

    protected float textWidth(BitmapFont font, String text) {
        glyphLayout.setText(font, text);
        return glyphLayout.width;
    }
}
