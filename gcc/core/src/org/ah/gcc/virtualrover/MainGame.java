package org.ah.gcc.virtualrover;

import java.util.ArrayList;
import java.util.List;

import org.ah.gcc.virtualrover.message.GCCMessageFactory;
import org.ah.gcc.virtualrover.rovers.AbstractRover;
import org.ah.gcc.virtualrover.rovers.CBiSRover;
import org.ah.gcc.virtualrover.rovers.GCCRover;
import org.ah.gcc.virtualrover.rovers.Rover;
import org.ah.gcc.virtualrover.view.ChatColor;
import org.ah.gcc.virtualrover.view.ChatListener;
import org.ah.gcc.virtualrover.view.Console;
import org.ah.themvsus.engine.client.ServerCommunication;
import org.ah.themvsus.engine.common.game.Player;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.g3d.utils.DefaultTextureBinder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.Array;

public class MainGame extends ApplicationAdapter implements InputProcessor, ChatListener {

    private AssetManager assetManager;
    private boolean loadingAssets = true;

    public static final long BREAK = 300;
    public static final boolean SHOW_MARKER = true;
    public static final float SCALE = 0.01f;

    private ModelBatch batch;
    private PerspectiveCamera camera;
    private Environment environment;

    private int mouseX = 0;
    private int mouseY = 0;
    private float rotSpeed = 0.1f;

    private boolean mouse = false;
    private CameraInputController camController;

    private long breakTime = 0;

    private enum GameState {
        MENU, SELECTION, GAME, BREAK, END
    }

    private enum RoverType {
        GCC(0, "GCC Rover"), CBIS(1, "CBiS-Education");

        private int id;
        private String nom;

        private RoverType(int id, String nom) {
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

    public RoverType playerSelection1 = RoverType.GCC;
    public RoverType playerSelection2 = RoverType.CBIS;
    public int player1score = 0;
    public int player2score = 0;

    public GameState currentState = GameState.MENU;

    public Array<ModelInstance> instances;

    private InputMultiplexer cameraInputMultiplexer;
    private ModelFactory modelFactory;

    private Rover rover1;
    private Rover rover2;
    private Rover[] rovers;

    private int cameratype = 3;


    private List<BoundingBox> boxes;

    private Mesh backgroundMesh;
    private RenderContext renderContext;
    private Renderable renderable;
    private DefaultShader shader;

    private SpriteBatch spriteBatch;
    private BitmapFont font;

    private OrthographicCamera hudCamera;
    private Console console;

    private PlatformSpecific platformSpecific;
    private ServerCommunication serverCommunication;

    private long a = 1;

    private String winner = "NONE";
    private Texture gccLogo;

    private Inputs rover1Inputs;
    private Inputs rover2Inputs;

    private ServerCommunicationAdapter serverCommunicationAdapter;
    private GCCMessageFactory messageFactory;

    private boolean renderBackground = false;

    private Vector3 pos1 = new Vector3();
    private Vector3 pos2 = new Vector3();
    private Vector3 midpoint = new Vector3();
    private float distance;

    public MainGame(PlatformSpecific platformSpecific) {
        this.platformSpecific = platformSpecific;
    }

    @Override
    public void create() {

        rover1Inputs = Inputs.create();
        rover2Inputs = Inputs.create();

        assetManager = new AssetManager();

        assetManager.load("font/basic.fnt", BitmapFont.class);
        assetManager.load("font/copper18.fnt", BitmapFont.class);
        assetManager.load("GCC_full.png", Texture.class);

        spriteBatch = new SpriteBatch();
        modelFactory = new ModelFactory();
        modelFactory.load();

        camera = new PerspectiveCamera(45, 800, 480);
        camera.position.set(300f * SCALE, 480f * SCALE, 300f * SCALE);
        // camera.rotateAround(new Vector3(0,0,0), new Vector3(0,1,0), -45);
//        camera.rotateAround(new Vector3(0f, 0f, 0f), new Vector3(1f, 0f, 0f), 165f);
        // camera.rotateAround(new Vector3(0f, 0f, 0f), new Vector3(0f, 1f, 0f), 85f);
        camera.lookAt(0f, 0f, 0f);

        camera.near = 0.02f;
        camera.far = 1000f;

        camController = new CameraInputController(camera);

        cameraInputMultiplexer = new InputMultiplexer();
        cameraInputMultiplexer.addProcessor(this);
        cameraInputMultiplexer.addProcessor(camController);
        Gdx.input.setInputProcessor(cameraInputMultiplexer);
        Gdx.input.setCursorCatched(mouse);
        batch = new ModelBatch();

        instances = new Array<ModelInstance>();

        // tpModel = modelFactory.loadModel("model.g3db");
        // try {
        // roverModel = modelFactory.getBaloon();
        // } catch (Exception e) {
        // e.printStackTrace();
        // }
        //
        // wheel = new GCCRoverWheel(modelFactory, 90, Color.GREEN);

        // backgroundShaderProgram = new ShaderProgram(Gdx.files.internal("rog.vs"), Gdx.files.internal("rog.fs"));
        backgroundMesh = createRect(0, 0, 120, 120);
        renderable = new Renderable();
        renderable.meshPart.mesh = backgroundMesh;
        renderable.meshPart.primitiveType = GL20.GL_TRIANGLES;
        // renderable.meshPart.size = mesh.getNumVertices() * mesh.getVertexSize();
        renderable.meshPart.size = backgroundMesh.getNumIndices();
        renderable.material = null;
        renderable.worldTransform.idt();

        String vertexProgram = Gdx.files.internal("background.vs").readString();
        String fragmentProgram = Gdx.files.internal("background.fs").readString();

        shader = new DefaultShader(renderable, new DefaultShader.Config(vertexProgram, fragmentProgram));
        shader.init();
        if (!shader.program.isCompiled()) {
            Gdx.app.log("Shader error: ", shader.program.getLog());
//            System.out.println("Shader error" + shader.program.getLog());
            // System.exit(-1);
        }

        renderContext = new RenderContext(new DefaultTextureBinder(DefaultTextureBinder.WEIGHTED, 1));

        Model arenaModel = modelFactory.loadModel("arena.obj");

        ModelInstance arena = new ModelInstance(arenaModel);
        arena.transform.translate(-200 * SCALE, -12 * SCALE, -200 * SCALE);
        arena.transform.scale(0.16f, 0.16f, 0.16f);
        arena.transform.scale(SCALE, SCALE, SCALE);
        arena.materials.get(0).set(ColorAttribute.createDiffuse(new Color(0.5f, 0.1f, 0.1f, 1f)));
        instances.add(arena);

        boxes = new ArrayList<BoundingBox>();
        float wallWidth = 0.1f;
        boxes.add(new BoundingBox(new Vector3(-200 * SCALE, 10 * SCALE, -200 * SCALE), new Vector3(200 * SCALE, 10 * SCALE, (-200 + wallWidth) * SCALE)));
        boxes.add(new BoundingBox(new Vector3(-200 * SCALE, 10 * SCALE, 200 * SCALE), new Vector3(200 * SCALE, 10 * SCALE, (200 - wallWidth) * SCALE)));

        boxes.add(new BoundingBox(new Vector3(-200 * SCALE, 10 * SCALE, -200 * SCALE), new Vector3((-200 + wallWidth) * SCALE, 10 * SCALE, 200 * SCALE)));
        boxes.add(new BoundingBox(new Vector3(200 * SCALE, 10 * SCALE, -200 * SCALE), new Vector3((200 - wallWidth) * SCALE, 10 * SCALE, 200 * SCALE)));

        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        DirectionalLight light = new DirectionalLight();
        environment.add(light.set(1f, 1f, 1f, new Vector3(0f * SCALE, -10f * SCALE, 0f * SCALE)));

        ModelBuilder mb = new ModelBuilder();
        mb.begin();

        hudCamera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        hudCamera.setToOrtho(true);

        messageFactory = new GCCMessageFactory();
        serverCommunication = platformSpecific.getServerCommunication();
        serverCommunicationAdapter = new ServerCommunicationAdapter(serverCommunication, messageFactory, console);
    }

    private void finishLoading() {
        loadingAssets = false;
        font = assetManager.get("font/basic.fnt");
        gccLogo = assetManager.get("GCC_full.png");

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
            // Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 1f);

            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
            Gdx.gl20.glEnable(GL20.GL_DEPTH_TEST);
            Gdx.gl20.glDepthFunc(GL20.GL_LEQUAL);
            Gdx.gl.glEnable(GL20.GL_POLYGON_OFFSET_FILL);
            Gdx.gl20.glPolygonOffset(1.0f, 1.0f);

            if (cameratype == 0) {
                camController.target.set(pos1);

                camera.lookAt(pos1);
                camera.up.set(new Vector3(0, 1, 0));

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
                ;
            } else if (cameratype == 2) {
                pos1 = rover1.getTransform().getTranslation(pos1);
                camera.lookAt(pos1);
                camera.position.set(300f * SCALE, 480f * SCALE, 300f * SCALE);
                camera.up.set(new Vector3(0, 1, 0));
            } else if (cameratype == 3) {
                float distanceBetweeRovers = 0f;
                distance = 450f;
                if (rover1 != null && rover2 != null) {
                    pos1 = rover1.getTransform().getTranslation(pos1);
                    pos2 = rover2.getTransform().getTranslation(pos2);
                    distanceBetweeRovers = pos1.dst(pos2);
                    midpoint.set(pos1).add(pos2).scl(0.5f);
                    distance = (-midpoint.dst(300f * SCALE, 0f, -300f * SCALE)) / SCALE;
                    distance = (distanceBetweeRovers) / SCALE;
                    distance = 350f + (distanceBetweeRovers * 0.5f - midpoint.dst(300f * SCALE, 0f, -300f * SCALE) * 0.6f) / SCALE;
                } else {
                    pos1.set(0f, 0f, 0f);
                }
                camera.lookAt(midpoint);
                camera.position.set(distance * SCALE, (200f + distanceBetweeRovers * 40f) * SCALE, -distance * SCALE);
                camera.up.set(new Vector3(0, 1, 0));
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
                renderContext.begin();
                shader.begin(camera, renderContext);
                shader.program.setUniformMatrix("u_projViewTrans", camera.combined);
                shader.program.setUniformMatrix("u_worldTrans", renderable.worldTransform);
                shader.program.setUniformf("u_time", a);
                shader.render(renderable);

                shader.end();
                renderContext.end();
            }

            batch.begin(camera);

            batch.render(instances, environment);
            breakTime--;

            if (rover1 != null && rover2 != null && (currentState == GameState.GAME || currentState == GameState.END || currentState == GameState.BREAK)) {

                rover1.render(batch, environment, true); // currentState == GameState.GAME);
                rover2.render(batch, environment, true); // currentState == GameState.GAME);

                rover1.update();
                rover1.processInput(rover1Inputs, rovers);
                rover2.update();
                rover2.processInput(rover2Inputs, rovers);

                if (currentState == GameState.BREAK) {
                    if (breakTime < 0) {
                        currentState = GameState.GAME;
                        resetRobots();
                    }
                }
            }

            batch.end();

            int margin = 64;
            spriteBatch.begin();

            spriteBatch.draw(gccLogo, 0, Gdx.graphics.getHeight() - gccLogo.getHeight());

//            font.draw(spriteBatch, Float.toString(distance) + ", " + Float.toString(pos1.x / SCALE) + ", " + Float.toString(pos1.z / SCALE),
//                    Gdx.graphics.getWidth() - 1020, Gdx.graphics.getHeight() - 40);

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
                    font.draw(spriteBatch, "3", Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2 + margin);
                } else if (breakTime < 240) {
                    font.draw(spriteBatch, "round " + (player1score + player2score + 1), Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2 + margin);
                }

            } else if (currentState == GameState.GAME) {

                if (breakTime > -60) {
                    font.draw(spriteBatch, "GO!", Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2 + margin);
                }

                boolean end = false;
                if (rover1.checkIfBalloonsPopped(rover2.sharpPoint()) == 0) {
                    end = true;
                    player1score++;
                    winner = "Green";
                } else if (rover2.checkIfBalloonsPopped(rover1.sharpPoint()) == 0) {
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
        batch.dispose();
        // tpModel.dispose();
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
//                System.out.println("next");
            } else if (keycode == Input.Keys.A) {
                playerSelection1 = playerSelection1.getPrevious();
            }

            if (keycode == Input.Keys.L) {
                playerSelection2 = playerSelection2.getNext();
//                System.out.println("next");
            } else if (keycode == Input.Keys.J) {
                playerSelection2 = playerSelection2.getPrevious();
            }

            if (keycode == Input.Keys.SPACE) {
                rover1 = makeRobot(playerSelection1, "1", Color.BLUE);
                rover1.getTransform().setTranslation(180 * SCALE, 0, 180 * SCALE);
                rover1.getTransform().scale(SCALE, SCALE, SCALE);
                rover1.getTransform().rotate(new Vector3(0, 1, 0), -45);
                rover1.update();
                rover1.setId(1);

                rover2 = makeRobot(playerSelection2, "2", Color.GREEN);
                rover2.getTransform().setTranslation(-180 * SCALE, 0, -180 * SCALE);
                rover2.getTransform().scale(SCALE, SCALE, SCALE);
                rover2.getTransform().rotate(new Vector3(0, 1, 0), 180 - 45);
                rover2.update();
                rover2.setId(2);

                rovers = new Rover[]{ rover1, rover2 };

                resetRobots();

                rover1.removeBalloons();

                rover2.removeBalloons();

                player1score = 0;
                player2score = 0;
                currentState = GameState.BREAK;
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

    public static Mesh createRect(float x, float y, float width, float height) {
        Mesh mesh = new Mesh(true, 4, 6, VertexAttribute.Position(), VertexAttribute.ColorUnpacked(), VertexAttribute.TexCoords(0));

        mesh.setVertices(new float[] { -1 * width + x, -1, -1 * height + y, 1, 0, 1, 1, 0, 0, 1 * width + x, -1, -1 * height + y, 1, 0, 1, 1, 1, 0,
                1 * width + x, -1, 1 * height + y, 1, 0, 1, 1, 1, 1, -1 * width + x, -1, 1 * height + y, 1, 0, 1, 1, 0, 1 });

        // mesh.setIndices(new short[] {0, 1, 2, 2, 3, 0});
        mesh.setIndices(new short[] { 2, 1, 0, 0, 3, 2 });
        return mesh;
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
        rover1.getTransform().setTranslation(160 * SCALE, 0, 160 * SCALE);
        rover1.getTransform().scale(SCALE, SCALE, SCALE);
        rover1.getTransform().rotate(new Vector3(0, 1, 0), -45);
        rover1.update();

        rover2.getTransform().idt();
        rover2.getTransform().setTranslation(-160 * SCALE, 0, -160 * SCALE);
        rover2.getTransform().scale(SCALE, SCALE, SCALE);
        rover2.getTransform().rotate(new Vector3(0, 1, 0), 180 - 45);
        rover2.update();
        rover2.setId(2);

        rover1.resetBalloons();
        rover2.resetBalloons();
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
}
