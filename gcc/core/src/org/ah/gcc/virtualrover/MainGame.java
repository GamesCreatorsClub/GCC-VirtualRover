package org.ah.gcc.virtualrover;

import com.badlogic.gdx.*;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import org.ah.gcc.virtualrover.backgrounds.Background;
import org.ah.gcc.virtualrover.backgrounds.PerlinNoiseBackground;
import org.ah.gcc.virtualrover.challenges.Challenge;
import org.ah.gcc.virtualrover.challenges.PiNoonArena;
import org.ah.gcc.virtualrover.message.GCCMessageFactory;
import org.ah.gcc.virtualrover.rovers.AbstractRover;
import org.ah.gcc.virtualrover.rovers.CBiSRover;
import org.ah.gcc.virtualrover.rovers.GCCRover;
import org.ah.gcc.virtualrover.rovers.Rover;
import org.ah.gcc.virtualrover.rovers.attachments.PiNoonAttachment;
import org.ah.gcc.virtualrover.utils.SoundManager;
import org.ah.gcc.virtualrover.view.ChatColor;
import org.ah.gcc.virtualrover.view.ChatListener;
import org.ah.gcc.virtualrover.view.Console;
import org.ah.themvsus.engine.client.ServerCommunication;
import org.ah.themvsus.engine.common.game.Player;

import java.util.List;

import static org.ah.gcc.virtualrover.utils.MeshUtils.polygonsOverlap;

public class MainGame extends ApplicationAdapter implements InputProcessor, ChatListener {

    public static final long BREAK = 300;
    public static final boolean SHOW_MARKER = true;
    public static final float SCALE = 0.0015f;

    private PlatformSpecific platformSpecific;

    private ServerCommunication serverCommunication;
    private ServerCommunicationAdapter serverCommunicationAdapter;
    private GCCMessageFactory messageFactory;

    private AssetManager assetManager;
    private boolean loadingAssets = true;
    private SoundManager soundManager;
    private ModelFactory modelFactory;

    private ModelBatch batch;
    private Environment environment;

    private OrthographicCamera hudCamera;
    private SpriteBatch spriteBatch;
    private BitmapFont font;

    private Console console;

    private PerspectiveCamera camera;
    private CameraInputController camController;
    private InputMultiplexer cameraInputMultiplexer;
    private int cameratype = 3;
    private Vector3 pos1 = new Vector3();
    private Vector3 pos2 = new Vector3();
    private Vector3 midpoint = new Vector3();
    private float distance;

    private int mouseX = 0;
    private int mouseY = 0;
    private float rotSpeed = 0.1f;

    private boolean mouse = false;

    private long breakTime = 0;
    private int a = 0;

    private enum GameState {
        MENU, SELECTION, GAME, BREAK, END
    }
    private GameState currentState = GameState.MENU;
    private boolean readySoundPlayed = false;
    private boolean fightSoundPlayed = false;

    private RoverType playerSelection1 = RoverType.GCC;
    private RoverType playerSelection2 = RoverType.CBIS;
    private Rover rover1;
    private Rover rover2;
    private Inputs rover1Inputs;
    private Inputs rover2Inputs;
    private int player1score = 0;
    private int player2score = 0;

    private String winner = "NONE";
    private Texture gccLogo;

    private Challenge challenge;
    private Background background;

    private boolean renderBackground = false;

    public MainGame(PlatformSpecific platformSpecific) {
        this.platformSpecific = platformSpecific;
    }

    @Override
    public void create() {

        assetManager = new AssetManager();

        assetManager.load("font/basic.fnt", BitmapFont.class);
        assetManager.load("font/copper18.fnt", BitmapFont.class);
        assetManager.load("GCC_full.png", Texture.class);

        soundManager = new SoundManager(!platformSpecific.hasSound());
        soundManager.requestAssets(assetManager);

        batch = new ModelBatch();
        spriteBatch = new SpriteBatch();
        modelFactory = new ModelFactory();
        modelFactory.load();

        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        DirectionalLight light = new DirectionalLight();
        environment.add(light.set(1f, 1f, 1f, new Vector3(0f * SCALE, -10f * SCALE, 0f * SCALE)));

        hudCamera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        hudCamera.setToOrtho(true);

        messageFactory = new GCCMessageFactory();
        serverCommunication = platformSpecific.getServerCommunication();
        serverCommunicationAdapter = new ServerCommunicationAdapter(serverCommunication, messageFactory, console);


        background = new PerlinNoiseBackground();
        challenge = new PiNoonArena(modelFactory);

        rover1Inputs = Inputs.create();
        rover2Inputs = Inputs.create();


        camera = new PerspectiveCamera(45, 800, 480);
        camera.position.set(300f * SCALE, 480f * SCALE, 300f * SCALE);
        camera.lookAt(0f, 0f, 0f);

        camera.near = 0.02f;
        camera.far = 1000f;

        camController = new CameraInputController(camera);

        cameraInputMultiplexer = new InputMultiplexer();
        cameraInputMultiplexer.addProcessor(this);
        cameraInputMultiplexer.addProcessor(camController);
        Gdx.input.setInputProcessor(cameraInputMultiplexer);
        Gdx.input.setCursorCatched(mouse);
    }

    private void finishLoading() {
        loadingAssets = false;
        font = assetManager.get("font/basic.fnt");
        gccLogo = assetManager.get("GCC_full.png");

        soundManager.fetchSounds(assetManager);

        console = new Console();
        console.raw("Welcome to PiWars Virtual PiNoon v.0.6");
        console.raw("Games Creators Club (GCC) Virtual Rover");
        console.raw("(c) Creative Sphere Limited");
        console.addListener(this);

        console.setCamera(hudCamera);
//        serverCommunicationAdapter.startEngine("arena");
    }

    @Override
    public void render() {
        if (loadingAssets && assetManager.update()) {
            finishLoading();
        }

        if (!loadingAssets) {
            a++;
            Gdx.gl.glClearColor(0.6f, 0.75f, 1f, 1f);

            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
            Gdx.gl20.glEnable(GL20.GL_DEPTH_TEST);
            Gdx.gl20.glDepthFunc(GL20.GL_LEQUAL);
            Gdx.gl.glEnable(GL20.GL_POLYGON_OFFSET_FILL);
            Gdx.gl20.glPolygonOffset(1.0f, 1.0f);

            if (cameratype == 0) {
                camController.target.set(pos1);

                camera.lookAt(pos1);
                camera.up.set(new Vector3(0, 1, 0));
                camera.fieldOfView = 45;

            } else if (cameratype == 1) {
                pos1 = rover1.getTransform().getTranslation(pos1);
                pos1.y = 10f;

                camera.position.set(pos1);

                Quaternion q = new Quaternion();
                q = rover1.getTransform().getRotation(q);
                camera.direction.set(new Vector3(0.1f, 0, 0));
                camera.direction.rotate(new Vector3(0, 1, 0), 180 + q.getAngleAround(new Vector3(0, 1, 0)));

                camera.up.set(new Vector3(0, 1, 0));

                camera.fieldOfView = 120f;
            } else if (cameratype == 2) {
                pos1 = rover1.getTransform().getTranslation(pos1);
                camera.lookAt(pos1);
                camera.position.set(1500f * SCALE, 2400f * SCALE, 1500f * SCALE);
                camera.up.set(new Vector3(0, 1, 0));
                camera.fieldOfView = 120f;
            } else if (cameratype == 3) {
                float distanceBetweeRovers = 0f;
                distance = 450f;
                if (rover1 != null && rover2 != null) {
                    pos1 = rover1.getTransform().getTranslation(pos1);
                    pos2 = rover2.getTransform().getTranslation(pos2);
                    distanceBetweeRovers = pos1.dst(pos2);
                    midpoint.set(pos1).add(pos2).scl(0.5f);
                    // distance = (-midpoint.dst(300f * SCALE, 0f, -300f * SCALE)) / SCALE;
                    // distance = (distanceBetweeRovers) / SCALE;
                    distance = 350f + (distanceBetweeRovers * 0.5f - midpoint.dst(1500f * SCALE, 0f, -1500f * SCALE) * 0.6f) / SCALE;
                } else {
                    pos1.set(0f, 0f, 0f);
                }
                camera.lookAt(midpoint);
                camera.position.set(distance * SCALE, (1000f + distanceBetweeRovers * 200f) * SCALE, -distance * SCALE);
                camera.up.set(new Vector3(0, 1, 0));
                camera.fieldOfView = 45;
            }

            rover2Inputs.moveUp(Gdx.input.isKeyPressed(Input.Keys.I));
            rover2Inputs.moveDown(Gdx.input.isKeyPressed(Input.Keys.K));
            rover2Inputs.moveLeft(Gdx.input.isKeyPressed(Input.Keys.J));
            rover2Inputs.moveRight(Gdx.input.isKeyPressed(Input.Keys.L));
            rover2Inputs.rotateLeft(Gdx.input.isKeyPressed(Input.Keys.U));
            rover2Inputs.rotateRight(Gdx.input.isKeyPressed(Input.Keys.O));

            rover1Inputs.moveUp(Gdx.input.isKeyPressed(Input.Keys.W));
            rover1Inputs.moveDown(Gdx.input.isKeyPressed(Input.Keys.S));
            rover1Inputs.moveLeft(Gdx.input.isKeyPressed(Input.Keys.A));
            rover1Inputs.moveRight(Gdx.input.isKeyPressed(Input.Keys.D));
            rover1Inputs.rotateLeft(Gdx.input.isKeyPressed(Input.Keys.Q));
            rover1Inputs.rotateRight(Gdx.input.isKeyPressed(Input.Keys.E));

            camera.update();

            Gdx.graphics.getGL20().glEnable(GL20.GL_BLEND);
            Gdx.graphics.getGL20().glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

            if (renderBackground) {
                background.render(camera, batch, environment);
            }

            batch.begin(camera);

            challenge.render(batch, environment);
            breakTime--;

            if (rover1 != null && rover2 != null && (currentState == GameState.GAME || currentState == GameState.END || currentState == GameState.BREAK)) {
                Matrix4 newRover1Position = rover1.processInput(rover1Inputs);
                Matrix4 newRover2Position = rover2.processInput(rover2Inputs);

                boolean rover1Moves = newRover1Position != null;
                boolean rover2Moves = newRover2Position != null;

                if (rover1Moves || rover2Moves) {
                    Matrix4 rover1Position = rover1.getPreviousTransform();
                    Matrix4 rover2Position = rover2.getPreviousTransform();
                    if (newRover1Position == null) { newRover1Position = rover1Position; }
                    if (newRover2Position == null) { newRover2Position = rover2Position; }

                    if (rover1Moves) {
                        rover1.getTransform().set(newRover1Position);
                        rover1.update();
                    }

                    if (rover2Moves) {
                        rover2.getTransform().set(newRover2Position);
                        rover2.update();
                    }

                    List<Polygon> rover1Poligons = rover1.getPolygons();
                    List<Polygon> rover2Poligons = rover2.getPolygons();

                    boolean roversCollide = polygonsOverlap(rover1Poligons, rover2Poligons);

                    if (roversCollide) {
                        if (rover1Moves) {
                            rover1.getTransform().set(rover1Position);
                            rover1.update();
                        }
                        if (rover2Moves) {
                            rover2.getTransform().set(rover2Position);
                            rover2.update();
                        }
                    } else {
                        if (rover1Moves && challenge.collides(rover1Poligons)) {
                            rover1.getTransform().set(rover1Position);
                            rover1.update();
                        }
                        if (rover2Moves && challenge.collides(rover2Poligons)) {
                            rover2.getTransform().set(rover2Position);
                            rover2.update();
                        }
                    }
                }

                rover1.render(batch, environment, true); // currentState == GameState.GAME);
                rover2.render(batch, environment, true); // currentState == GameState.GAME);

                if (currentState == GameState.BREAK) {
                    if (breakTime < 0) {
                        currentState = GameState.GAME;
                        fightSoundPlayed = false;
                        resetRobots();
                    }
                }
            }

            batch.end();

            int margin = 64;
            spriteBatch.begin();

            spriteBatch.draw(gccLogo, 0, Gdx.graphics.getHeight() - gccLogo.getHeight());

            if (currentState == GameState.BREAK || currentState == GameState.GAME || currentState == GameState.END) {
                font.draw(spriteBatch, player1score + " - " + player2score, Gdx.graphics.getWidth() - 120, Gdx.graphics.getHeight() - 40);
            }

            if (currentState == GameState.SELECTION) {
                font.draw(spriteBatch, playerSelection1.getName(), margin, Gdx.graphics.getHeight() / 2 + margin);
                font.draw(spriteBatch, playerSelection2.getName(), margin + Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2 + margin);
                if (Math.floor(a / 20.0) % 2 == 0) {
                    font.draw(spriteBatch, "Press space to begin", margin, margin);
                }

            } else if (currentState == GameState.MENU) {
                if (Math.floor(a / 20.0) % 2 == 0) {
                    font.draw(spriteBatch, "Press space to start", margin, margin * 2);
                }
            } else if (currentState == GameState.END) {
                font.draw(spriteBatch, winner + " wins! " + player1score + " - " + player2score, margin, margin + 40);
                if (Math.floor(a / 20.0) % 2 == 0) {
                    font.draw(spriteBatch, "Press space to return to menu!", margin, margin * 2);
                }

            } else if (currentState == GameState.BREAK) {
                if (!winner.equals("NONE")) {
                    font.draw(spriteBatch, winner + " won that round!", margin, margin);

                }
                if (breakTime < 60) {
                    font.draw(spriteBatch, "1", Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2 + margin);
                } else if (breakTime < 120) {
                    font.draw(spriteBatch, "2", Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2 + margin);
                } else if (breakTime < 180) {
                    if (platformSpecific.hasSound() && !readySoundPlayed) {
                        soundManager.playReady();
                        readySoundPlayed = true;
                    }
                    font.draw(spriteBatch, "3", Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2 + margin);
                } else if (breakTime < 240) {
                    font.draw(spriteBatch, "round " + (player1score + player2score + 1), Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2 + margin);
                }

            } else if (currentState == GameState.GAME) {

                if (breakTime > -60) {
                    font.draw(spriteBatch, "GO!", Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2 + margin);
                    if (platformSpecific.hasSound() && !fightSoundPlayed) {
                        soundManager.playFight();
                        fightSoundPlayed = true;
                    }
                }

                boolean end = false;
                PiNoonAttachment rover1PiNoonAttachment = (PiNoonAttachment)rover1.getAttachemnt();
                PiNoonAttachment rover2PiNoonAttachment = (PiNoonAttachment)rover2.getAttachemnt();

                if (rover1PiNoonAttachment.checkIfBalloonsPopped(rover2PiNoonAttachment.getSharpPoint()) == 0) {
                    end = true;
                    player1score++;
                    winner = "Green";
                } else if (rover2PiNoonAttachment.checkIfBalloonsPopped(rover1PiNoonAttachment.getSharpPoint()) == 0) {
                    end = true;
                    player2score++;
                    winner = "Blue";
                }

                if (end) {
                    if (player1score + player2score >= 3) {
                        currentState = GameState.END;
                        if (player1score > player2score) {
                            winner = "Green";
                        } else if (player1score < player2score) {
                            winner = "Blue";
                        }
                    } else {

                        currentState = GameState.BREAK;
                        readySoundPlayed = false;
                        breakTime = BREAK;
                    }
                }
            }

            spriteBatch.end();

            console.render();
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

    @Override
    public void dispose() {
        assetManager.dispose();
        soundManager.dispose();
        batch.dispose();
        modelFactory.dispose();
        spriteBatch.dispose();
        font.dispose();
        if (console != null) { console.dispose(); }
        challenge.dispose();
        background.dispose();
    }

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.F1) {
            cameratype++;
            if (cameratype > 3) {
                cameratype = 0;
            }
        } else if (keycode == Input.Keys.F2) {
            cameratype--;
            if (cameratype < 0) {
                cameratype = 3;
            }
        }
        if (keycode == Input.Keys.ESCAPE) {
            mouse = !mouse;
            Gdx.input.setCursorCatched(mouse);
            if (mouse) {
                Gdx.input.setInputProcessor(this);
            } else {
                Gdx.input.setInputProcessor(cameraInputMultiplexer);
            }
        }
        if (currentState == GameState.SELECTION) {

            if (keycode == Input.Keys.D) {
                playerSelection1 = playerSelection1.getNext();
            } else if (keycode == Input.Keys.A) {
                playerSelection1 = playerSelection1.getPrevious();
            }

            if (keycode == Input.Keys.L) {
                playerSelection2 = playerSelection2.getNext();
            } else if (keycode == Input.Keys.J) {
                playerSelection2 = playerSelection2.getPrevious();
            }

            if (keycode == Input.Keys.SPACE) {
                rover1 = makeRobot(playerSelection1, "1", Color.BLUE);
                rover2 = makeRobot(playerSelection2, "2", Color.GREEN);
                rover2.setId(2);
                rover1.setAttachment(new PiNoonAttachment(modelFactory, rover1.getColour()));
                rover2.setAttachment(new PiNoonAttachment(modelFactory, rover2.getColour()));

                resetRobots();

                ((PiNoonAttachment)rover1.getAttachemnt()).removeBalloons();

                ((PiNoonAttachment)rover2.getAttachemnt()).removeBalloons();

                player1score = 0;
                player2score = 0;
                currentState = GameState.BREAK;
                readySoundPlayed = false;
                breakTime = BREAK + 100;
                winner = "NONE";
            }
        }

        if (keycode == Input.Keys.SPACE) {
            if (currentState == GameState.END) {
                currentState = GameState.MENU;
            } else if (currentState == GameState.MENU) {
                currentState = GameState.SELECTION;
            }
        }

        if (keycode == Input.Keys.TAB) {
            camera.fieldOfView = 4f;
        }
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        if (keycode == Input.Keys.TAB) {
            camera.fieldOfView = 45f;
        }
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        if (mouse) {
            int magX = Math.abs(mouseX - screenX);
            int magY = Math.abs(mouseY - screenY);

            if (mouseX > screenX) {
                camera.rotate(Vector3.Y, 1 * magX * rotSpeed);
                camera.update();
            }

            if (mouseX < screenX) {
                camera.rotate(Vector3.Y, -1 * magX * rotSpeed);
                camera.update();
            }

            if (mouseY < screenY) {
                if (camera.direction.y > -1)
                    camera.rotate(camera.direction.cpy().crs(Vector3.Y), -1 * magY * rotSpeed);
                camera.update();
            }

            if (mouseY > screenY) {

                if (camera.direction.y < 1)
                    camera.rotate(camera.direction.cpy().crs(Vector3.Y), 1 * magY * rotSpeed);
                camera.update();
            }

            mouseX = screenX;
            mouseY = screenY;
        }
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }

    public AbstractRover makeRobot(RoverType t, String name, Color color) {
        AbstractRover r;
        if (t == RoverType.CBIS) {
            r = new CBiSRover(name, modelFactory, color);
        } else {
            r = new GCCRover(name, modelFactory, color);

        }
        return r;
    }

    public void resetRobots() {
        rover1.getTransform().idt();
        rover1.getTransform().setToTranslationAndScaling(700 * SCALE, 0, 700 * SCALE, SCALE, SCALE, SCALE);
        rover1.getTransform().rotate(new Vector3(0, 1, 0), -45);
        rover1.update();

        rover2.getTransform().idt();
        rover2.getTransform().setToTranslationAndScaling(-700 * SCALE, 0, -700 * SCALE, SCALE, SCALE, SCALE);
        rover2.getTransform().rotate(new Vector3(0, 1, 0), 180 - 45);
        rover2.update();

        ((PiNoonAttachment)rover1.getAttachemnt()).resetBalloons();
        ((PiNoonAttachment)rover2.getAttachemnt()).resetBalloons();
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

    @Override
    public void onChat(String playerName, String text) {

    }

    @Override
    public void onText(String text) {

    }

    private enum RoverType {
        GCC(0, "GCC Rover"), CBIS(1, "CBiS-Education");

        private int id;
        private String nom;

        RoverType(int id, String nom) {
            this.id = id;
            this.nom = nom;
        }

        public int getId() {
            return id;
        }

        public static RoverType getById(int id) {
            for (RoverType e : values()) {
                if (e.getId() == id) {
                    return e;
                }
            }
            return RoverType.GCC;
        }

        public RoverType getNext() {
            return getById((getId() + 1) % values().length);
        }

        public RoverType getPrevious() {
            return getById((getId() - 1) % values().length);
        }

        public String getName() {
            return nom;
        }
    }
}
