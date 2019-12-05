package org.ah.gcc.virtualrover;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import org.ah.gcc.virtualrover.PlatformSpecific.RegistrationCallback;
import org.ah.gcc.virtualrover.utils.SoundManager;
import org.ah.gcc.virtualrover.view.Console;
import org.ah.themvsus.engine.client.CommonServerCommunicationAdapter.AuthenticatedCallback;
import org.ah.themvsus.engine.client.CommonServerCommunicationAdapter.GameMapCallback;
import org.ah.themvsus.engine.client.CommonServerCommunicationAdapter.GameReadyCallback;
import org.ah.themvsus.engine.client.CommonServerCommunicationAdapter.ReceivedRegistrationServerCallback;
import org.ah.themvsus.engine.client.ServerCommunication.ServerConnectionCallback;

public class GreetingScreen implements Screen, InputProcessor, AuthenticatedCallback, GameReadyCallback, GameMapCallback, ReceivedRegistrationServerCallback {

    private enum State {
        None, SignInServer, SignInUsername, SignInPassword, RegisterServer, RegisterEmail, RegisterEmail2, RegisterUsername, RegisterPassword, RegisterPassword2;
    }

    private MainGame game;
    private PlatformSpecific platformSpecific;
    private ServerCommunicationAdapter serverCommunicationAdapter;

    private SpriteBatch batch;
    private BitmapFont font;
    private OrthographicCamera camera;
    private Viewport viewport;

    private Stage stage;
    private Skin skin;
    private TextButton signInButton;
    private TextButton registerButton;
    private TextButtonStyle activeTextButtonStyle;

    private Console console;

    private State state = State.SignInServer;
    private String serverAddress;
    private int serverPort;
    private String username;
    private String email;
    private String password;

    private String mapId;

    private Preferences preferences;

    private boolean doStartGame = false;
    private boolean doLoadMap;
//    private AssetManager assetManager;
//    private SoundManager soundManager;
//    private ModelFactory modelFactory;


    public GreetingScreen(MainGame game,
            PlatformSpecific platformSpecific,
            AssetManager assetManager,
            SoundManager soundManager,
            ModelFactory modelFactory,
            ServerCommunicationAdapter serverCommunicationAdapter,
            Console console) {

        this.game = game;
        this.platformSpecific = platformSpecific;
//        this.assetManager = assetManager;
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

    public void create(MainGame game, PlatformSpecific platformSpecific, ServerCommunicationAdapter serverCommunicationAdapter, Console console) {
        this.game = game;
        this.platformSpecific = platformSpecific;
        this.serverCommunicationAdapter = serverCommunicationAdapter;
        this.console = console;

        font = new BitmapFont(Gdx.files.internal("font/copper18.fnt"), Gdx.files.internal("font/copper18.png"), false);

        batch = new SpriteBatch();

        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.setToOrtho(true);

        viewport = new ScreenViewport(camera);
        stage = new Stage(viewport, batch);
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

        preferences = Gdx.app.getPreferences("org.ah.themvsus.server-details");

        serverCommunicationAdapter.setAuthenticatedCallback(this);
        serverCommunicationAdapter.setGameMapCallback(this);
        serverCommunicationAdapter.addGameReadyCallback(this);
        serverCommunicationAdapter.setReceivedRegistrationServerCallback(this);
    }

    public void reset() {
        doStartGame = false;
        setupToReadSignInServer();
    }

    private void doLoadMap() {
        serverCommunicationAdapter.startEngine(mapId, false, platformSpecific.isSimulation());
    }

    private void startGame() {
        game.setChallengeScreen(mapId);
    }

    @Override
    public void show() {
        console.setCamera(camera);
        Gdx.input.setOnscreenKeyboardVisible(platformSpecific.needOnScreenKeyboard());
        Gdx.input.setInputProcessor(this);
    }

    @Override
    public void render(float delta) {
        updateCamera();

        Gdx.gl.glClearColor(0.35f, 0.21f, 0.06f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        batch.end();

        stage.draw();

        console.render();

        if (doLoadMap) {
            doLoadMap();
        }

        if (doStartGame) {
            startGame();
        }
    }

    @Override
    public void resize(int width, int height) {
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
    public void hide() {

    }

    @Override
    public void dispose() {
        batch.dispose();
    }

    public void updateCamera() {
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.update();
    }

    public void connectToServer(String serverAddress, int serverPort) {
        state = State.None;
        // doConnectToServer = true;
        serverCommunicationAdapter.connectToServer(serverAddress, serverPort, serverConnectionCallback);
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
        if (keycode == Keys.ENTER) {
            textEntered();
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
    public void gameMap(String mapId) {
        this.mapId = mapId;
        doLoadMap = true;
    }

    @Override
    public void gameReady() {
        doStartGame = true;
    }
}
