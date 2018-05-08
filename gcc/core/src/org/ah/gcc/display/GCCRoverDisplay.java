package org.ah.gcc.display;

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

public class GCCRoverDisplay extends ApplicationAdapter implements InputProcessor {

    private AssetManager assetManager;
    private boolean loadingAssets = true;

    private ModelBatch batch;
    private PerspectiveCamera camera;
    private Environment environment;

    private int mouseX = 0;
    private int mouseY = 0;
    private float rotSpeed = 0.1f;

    private boolean mouse = false;
    private CameraInputController camController;

    public Array<ModelInstance> instances;
    private InputMultiplexer cameraInputMultiplexer;
    private ModelFactory modelFactory;
    private GCCRover rover;
    private GCCRover rover2;

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

    private int state = 0;
    private String winner = "NONE";
    private Texture gccLogo;

    @Override
    public void create() {
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

//        tpModel = modelFactory.loadModel("model.g3db");
//        try {
//            roverModel = modelFactory.getBaloon();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        wheel = new GCCRoverWheel(modelFactory, 90, Color.GREEN);

//        backgroundShaderProgram = new ShaderProgram(Gdx.files.internal("rog.vs"), Gdx.files.internal("rog.fs"));
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
//            System.exit(-1);
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

//        instances.add(marker1);
//        instances.add(marker2);

        boxes = new ArrayList<BoundingBox>();
        float wallWidth = 0.1f;
        boxes.add(new BoundingBox(new Vector3(-200 * SCALE, 10 * SCALE, -200 * SCALE), new Vector3(200 * SCALE, 10 * SCALE, (-200 + wallWidth) * SCALE)));
        boxes.add(new BoundingBox(new Vector3(-200 * SCALE, 10 * SCALE, 200 * SCALE), new Vector3(200 * SCALE, 10 * SCALE, (200 - wallWidth) * SCALE)));

        boxes.add(new BoundingBox(new Vector3(-200 * SCALE, 10 * SCALE, -200 * SCALE), new Vector3((-200 + wallWidth) * SCALE, 10 * SCALE, 200 * SCALE)));
        boxes.add(new BoundingBox(new Vector3(200 * SCALE, 10 * SCALE, -200 * SCALE), new Vector3((200 - wallWidth) * SCALE, 10 * SCALE, 200 * SCALE)));

        rover = new GCCRover("rover1", modelFactory, Color.GREEN, this);
        rover.getTransform().setTranslation(180 * SCALE, 0, 180 * SCALE);
        rover.getTransform().scale(SCALE, SCALE, SCALE);
        rover.getTransform().rotate(new Vector3(0, 1, 0), -45);
        rover.update();
        rover.setId(1);
        rover.setDoingPiNoon(false);



        rover2 = new GCCRover("rover1", modelFactory, Color.BLUE, this);
        rover2.getTransform().setTranslation(-180 * SCALE, 0, -180 * SCALE);
        rover2.getTransform().scale(SCALE, SCALE, SCALE);
        rover2.getTransform().rotate(new Vector3(0, 1, 0), 180 - 45);
        rover2.update();
        rover.addOtherRover(rover2);
        rover2.addOtherRover(rover);
        rover2.setId(2);
        rover2.setDoingPiNoon(false);

        environment = new Environment();

        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.1f, 0.1f, 0.1f, 1f));
        DirectionalLight light = new DirectionalLight();
        environment.add(light.set(1f, 1f, 1f, new Vector3(0f * SCALE, -20f * SCALE, 0f * SCALE)));

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

            camera.update();

            Gdx.graphics.getGL20().glEnable(GL20.GL_BLEND);
            Gdx.graphics.getGL20().glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

            renderContext.begin();
            shader.begin(camera, renderContext);
            shader.program.setUniformMatrix("u_projViewTrans", camera.combined);
            shader.program.setUniformMatrix("u_worldTrans", renderable.worldTransform);
            shader.program.setUniformf("u_time", a);
            shader.render(renderable);

            shader.end();
            renderContext.end();


            batch.begin(camera);

            batch.render(instances, environment);
            rover.render(batch, environment);
            rover2.render(batch, environment);
            batch.end();

            rover.update();
            Inputs i = Inputs.create();
            i.moveUp(Gdx.input.isKeyPressed(Input.Keys.W));
            i.moveDown(Gdx.input.isKeyPressed(Input.Keys.S));
            i.moveLeft(Gdx.input.isKeyPressed(Input.Keys.A));
            i.moveRight(Gdx.input.isKeyPressed(Input.Keys.D));
            i.rotateLeft(Gdx.input.isKeyPressed(Input.Keys.Q));
            i.rotateRight(Gdx.input.isKeyPressed(Input.Keys.E));

            rover.processInput(i);

            rover2.update();
            Inputs i2 = Inputs.create();
            i2.moveUp(Gdx.input.isKeyPressed(Input.Keys.I));
            i2.moveDown(Gdx.input.isKeyPressed(Input.Keys.K));
            i2.moveLeft(Gdx.input.isKeyPressed(Input.Keys.J));
            i2.moveRight(Gdx.input.isKeyPressed(Input.Keys.L));
            i2.rotateLeft(Gdx.input.isKeyPressed(Input.Keys.U));
            i2.rotateRight(Gdx.input.isKeyPressed(Input.Keys.O));

            rover2.processInput(i2);

            int margin = 64;

            spriteBatch.begin();

            if (state == 0) {
                if (Math.floor(a / 20) % 2 == 0) {
                    font.draw(spriteBatch, "Press space to start", margin, margin);
                }
            } else if (state == 2) {
                font.draw(spriteBatch, winner + " wins!", margin, margin + 40);
                if (Math.floor(a / 20) % 2 == 0) {
                    font.draw(spriteBatch, "Press space to restart!", margin, margin);
                }

            }
            spriteBatch.draw(gccLogo, 0, Gdx.graphics.getHeight() - gccLogo.getHeight());
            spriteBatch.end();

            if (state == 1) {
                if (!rover.hasBallon1() && !rover.hasBallon2() && !rover.hasBallon3()) {
                    state = 2;
                    winner = "Blue";
                } else if (!rover2.hasBallon1() && !rover2.hasBallon2() && !rover2.hasBallon3()) {
                    state = 2;
                    winner = "Green";
                }
            }
        }
    }

    @Override
    public void dispose() {
        batch.dispose();
//        tpModel.dispose();
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

        if (keycode == Input.Keys.SPACE) {
            if (state == 0 || state == 2) {
                state = 1;

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

        if (keycode == Input.Keys.TAB) {
            camera.fieldOfView = 4f;
//        } else {if (keycode == Input.Keys.A) {
//            moveleft = true;
//        } else if (keycode == Input.Keys.D) {
//            moveright = true;
//
//        } else if (keycode == Input.Keys.W) {
//            moveup = true;
//
//        } else if (keycode == Input.Keys.S) {
//            movedown = true;
        }
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        if (keycode == Input.Keys.TAB) {
            camera.fieldOfView = 45f;
//        } else if (keycode == Input.Keys.A) {
//            moveleft = false;
//        } else if (keycode == Input.Keys.D) {
//            moveright = false;
//        } else if (keycode == Input.Keys.W) {
//            moveup = false;
//        } else if (keycode == Input.Keys.S) {
//            movedown = false;
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
        Mesh mesh = new Mesh(true, 4, 6,
                VertexAttribute.Position(),
                VertexAttribute.ColorUnpacked(),
                VertexAttribute.TexCoords(0));

        mesh.setVertices(new float[] {
                -1 * width + x, -1, -1 * height + y, 1, 0, 1, 1, 0, 0,
                 1 * width + x, -1, -1 * height + y, 1, 0, 1, 1, 1, 0,
                 1 * width + x, -1,  1 * height + y, 1, 0, 1, 1, 1, 1,
                -1 * width + x, -1,  1 * height + y, 1, 0, 1, 1, 0, 1
           });

//        mesh.setIndices(new short[] {0, 1, 2, 2, 3, 0});
        mesh.setIndices(new short[] {2, 1, 0, 0, 3, 2});
        return mesh;
    }

}
