package org.ah.gcc.virtualrover;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
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
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.Array;

public class MainGame extends ApplicationAdapter implements InputProcessor {

    private AssetManager assetManager;
    private boolean loadingAssets = true;

    public static final long BREAK = 300;

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

    private Robot rover;
    private Robot rover2;

    private GCCRoverWheel wheel;

    private int cameratype = 0;

    public static final float SCALE = 0.01f;

    private List<BoundingBox> boxes;
    private ModelInstance marker1;
    private ModelInstance marker2;
    private Mesh backgroundMesh;
    private ShaderProgram backgroundShaderProgram;
    private RenderContext renderContext;
    private Renderable renderable;
    private DefaultShader shader;

    private SpriteBatch spriteBatch;
    private BitmapFont font;

    private long a = 1;

    private String winner = "NONE";
    private Texture gccLogo;
    private Inputs rover1Inputs;
    private Inputs rover2Inputs;

    @Override
    public void create() {

        rover1Inputs = Inputs.create();
        rover2Inputs = Inputs.create();

        assetManager = new AssetManager();

        assetManager.load("font/basic.fnt", BitmapFont.class);
        assetManager.load("GCC_full.png", Texture.class);

        spriteBatch = new SpriteBatch();
        modelFactory = new ModelFactory();
        modelFactory.load();

        camera = new PerspectiveCamera(45, 800, 480);
        camera.position.set(300f * SCALE, -480f * SCALE, 300f * SCALE);
        // camera.rotateAround(new Vector3(0,0,0), new Vector3(0,1,0), -45);
        camera.rotateAround(new Vector3(0f, 0f, 0f), new Vector3(1f, 0f, 0f), 165f);
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
            System.out.println("Shader error" + shader.program.getLog());
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

        Model marker = modelFactory.loadModel("model.g3db");
        marker1 = new ModelInstance(marker);
        marker1.transform.scale(0.05f, 0.05f, 0.05f);
        marker1.transform.scale(SCALE, SCALE, SCALE);
        marker1.materials.get(0).set(ColorAttribute.createDiffuse(Color.GREEN));
        marker2 = new ModelInstance(marker);
        marker2.transform.scale(0.05f, 0.05f, 0.05f);
        marker2.transform.scale(SCALE, SCALE, SCALE);
        marker2.materials.get(0).set(ColorAttribute.createDiffuse(Color.BLUE));

        // instances.add(marker1);
        // instances.add(marker2);

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

    }

    private void finishLoading() {
        loadingAssets = false;
        font = assetManager.get("font/basic.fnt");
        gccLogo = assetManager.get("GCC_full.png");
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
                Vector3 pos = new Vector3(0, 0, 0);
                camController.target.set(pos);

                camera.lookAt(pos);
                camera.up.set(new Vector3(0, 1, 0));

            } else if (cameratype == 1) {
                Vector3 pos = new Vector3();
                pos = rover.getTransform().getTranslation(pos);
                pos.y = 10f;

                camera.position.set(pos);

                Quaternion q = new Quaternion();
                q = rover.getTransform().getRotation(q);
                camera.direction.set(new Vector3(0.1f, 0, 0));
                camera.direction.rotate(new Vector3(0, 1, 0), 180 + q.getAngleAround(new Vector3(0, 1, 0)));

                camera.up.set(new Vector3(0, 1, 0));

                camera.fieldOfView = 120f;
                ;
            } else if (cameratype == 2) {
                Vector3 pos = new Vector3();
                pos = rover.getTransform().getTranslation(pos);
                camera.lookAt(pos);
                camera.position.set(240f, 0f, 10f);
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

            // renderContext.begin();
            // shader.begin(camera, renderContext);
            // shader.program.setUniformMatrix("u_projViewTrans", camera.combined);
            // shader.program.setUniformMatrix("u_worldTrans", renderable.worldTransform);
            // shader.program.setUniformf("u_time", a);
            // shader.render(renderable);
            //
            // shader.end();
            // renderContext.end();

            batch.begin(camera);

            batch.render(instances, environment);
            breakTime--;

            if (rover != null && rover2 != null && (currentState == GameState.GAME || currentState == GameState.END || currentState == GameState.BREAK)) {

                rover.render(batch, environment);
                rover2.render(batch, environment);

                rover.update();
                rover.processInput(rover1Inputs);
                rover2.update();
                rover2.processInput(rover2Inputs);

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

            if (currentState == GameState.BREAK || currentState == GameState.GAME || currentState == GameState.END) {
                font.draw(spriteBatch, player1score + " - " + player2score, Gdx.graphics.getWidth() - 120, Gdx.graphics.getHeight() - 40);

            }

            if (currentState == GameState.SELECTION) {
                font.draw(spriteBatch, playerSelection1.getName(), margin, Gdx.graphics.getHeight() / 2 + margin);
                font.draw(spriteBatch, playerSelection2.getName(), margin + Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2 + margin);
                if (Math.floor(a / 20) % 2 == 0) {
                    font.draw(spriteBatch, "Press space to begin", margin, margin);
                }

            } else if (currentState == GameState.MENU) {
                if (Math.floor(a / 20) % 2 == 0) {
                    font.draw(spriteBatch, "Press space to start", margin, margin);
                }
            } else if (currentState == GameState.END) {
                font.draw(spriteBatch, winner + " wins! " + player1score + " - " + player2score, margin, margin + 40);
                if (Math.floor(a / 20) % 2 == 0) {
                    font.draw(spriteBatch, "Press space to return to menu!", margin, margin);
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
                if (!rover.hasBallon1() && !rover.hasBallon2() && !rover.hasBallon3()) {
                    end = true;
                    player1score++;
                    winner = "Green";
                } else if (!rover2.hasBallon1() && !rover2.hasBallon2() && !rover2.hasBallon3()) {
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
            cameratype = 0;
        } else if (keycode == Input.Keys.F2) {
            cameratype = 1;
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
                System.out.println("next");
            } else if (keycode == Input.Keys.A) {
                playerSelection1 = playerSelection1.getPrevious();
            }

            if (keycode == Input.Keys.L) {
                playerSelection2 = playerSelection2.getNext();
                System.out.println("next");
            } else if (keycode == Input.Keys.J) {
                playerSelection2 = playerSelection2.getPrevious();
            }

            if (keycode == Input.Keys.SPACE) {
                rover = makeRobot(playerSelection1, "1", Color.BLUE);
                rover.getTransform().setTranslation(180 * SCALE, 0, 180 * SCALE);
                rover.getTransform().scale(SCALE, SCALE, SCALE);
                rover.getTransform().rotate(new Vector3(0, 1, 0), -45);
                rover.update();
                rover.setId(1);
                rover.setDoingPiNoon(false);

                rover2 = makeRobot(playerSelection2, "2", Color.GREEN);
                rover2.getTransform().setTranslation(-180 * SCALE, 0, -180 * SCALE);
                rover2.getTransform().scale(SCALE, SCALE, SCALE);
                rover2.getTransform().rotate(new Vector3(0, 1, 0), 180 - 45);
                rover2.update();
                rover.addOtherRover(rover2);
                rover2.addOtherRover(rover);
                rover2.setId(2);

                resetRobots();

                rover.hasBallon1(false);
                rover.hasBallon2(false);
                rover.hasBallon3(false);
                rover.setDoingPiNoon(false);

                rover2.hasBallon1(false);
                rover2.hasBallon2(false);
                rover2.hasBallon3(false);
                rover2.setDoingPiNoon(false);

                player1score = 0;
                player2score = 0;
                currentState = GameState.BREAK;
                breakTime = BREAK + 100;
                winner = "NONE";
                System.out.println("game");
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
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        // TODO Auto-generated method stub
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

    public void setMarkerPosition(Vector3 p, int marker, boolean useScale) {

        Vector3 v = new Vector3(p.x * SCALE, p.y * SCALE, p.z * SCALE);
        if (!useScale) {
            v.set(new Vector3(p.x, p.y, p.z));
        }
        if (marker == 1) {
            marker1.transform.setToTranslation(v);
            marker1.transform.scale(SCALE * 1.0f, SCALE * 1.0f, SCALE * 1.0f);
            // marker1.calculateTransforms();

        }
        if (marker == 2) {
            marker2.transform.setToTranslation(v);
            marker2.transform.scale(SCALE * 1.0f, SCALE * 1.0f, SCALE * 1.0f);
            // marker2.calculateTransforms();

        }
    }

    public static Mesh createRect(float x, float y, float width, float height) {
        Mesh mesh = new Mesh(true, 4, 6, VertexAttribute.Position(), VertexAttribute.ColorUnpacked(), VertexAttribute.TexCoords(0));

        mesh.setVertices(new float[] { -1 * width + x, -1, -1 * height + y, 1, 0, 1, 1, 0, 0, 1 * width + x, -1, -1 * height + y, 1, 0, 1, 1, 1, 0, 1 * width + x, -1,
                1 * height + y, 1, 0, 1, 1, 1, 1, -1 * width + x, -1, 1 * height + y, 1, 0, 1, 1, 0, 1 });

        // mesh.setIndices(new short[] {0, 1, 2, 2, 3, 0});
        mesh.setIndices(new short[] { 2, 1, 0, 0, 3, 2 });
        return mesh;
    }

    public Rover makeRobot(RoverType t, String name, Color color) {
        Rover r;
        if (t == RoverType.CBIS) {
            r = new CBiSRover(name, modelFactory, color, this);
        } else {
            r = new GCCRover(name, modelFactory, color, this);

        }
        return r;
    }

    public void resetRobots() {
        rover.getTransform().setTranslation(180 * SCALE, 0, 180 * SCALE);
        rover.update();

        rover2.getTransform().setTranslation(-180 * SCALE, 0, -180 * SCALE);
        rover2.update();
        rover2.setId(2);

        rover.hasBallon1(true);
        rover.hasBallon2(true);
        rover.hasBallon3(true);
        rover.setDoingPiNoon(true);
        rover2.hasBallon1(true);
        rover2.hasBallon2(true);
        rover2.hasBallon3(true);
        rover2.setDoingPiNoon(true);
    }

}