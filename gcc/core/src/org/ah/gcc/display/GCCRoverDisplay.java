package org.ah.gcc.display;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

public class GCCRoverDisplay extends ApplicationAdapter implements InputProcessor {
    private ModelBatch batch;
    private PerspectiveCamera camera;
    private Model tpModel;
    private ModelInstance modelInstance;
    private Environment environment;
    private boolean moveleft = false;
    private boolean moveright = false;
    private boolean moveup = false;
    private boolean movedown = false;

    private int mouseX = 0;
    private int mouseY = 0;
    private float rotSpeed = 0.1f;

    private boolean mouse = false;
    private Model roverModel;
    private CameraInputController camController;

    public Array<ModelInstance> instances;
    private InputMultiplexer cameraInputMultiplexer;
    private ModelFactory modelFactory;
    private GCCRover rover;
    private GCCRoverWheel wheel;

    private int cameratype = 0;

    @Override
    public void create() {
        modelFactory = new ModelFactory();
        modelFactory.load();

        camera = new PerspectiveCamera(45, 800, 480);
        camera.position.set(240f, 0f, 240f);
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

        tpModel = modelFactory.loadModel("model.g3db");
        try {
            roverModel = modelFactory.getBaloon();
        } catch (Exception e) {
            e.printStackTrace();
        }

        wheel = new GCCRoverWheel(modelFactory, 90, Color.GREEN);

        Model arenaModel = modelFactory.loadModel("arena.obj");
//        ModelBuilder modelBuilder = new ModelBuilder();
//        Model arenaModel = modelBuilder.createBox(4000f, 5f, 4000f,
//            new Material(ColorAttribute.createDiffuse(Color.RED)),
//            Usage.Position | Usage.Normal);

        ModelInstance arena = new ModelInstance(arenaModel);
        arena.transform.translate(-200, -16, -200);
        arena.transform.scale(0.16f, 0.16f, 0.16f);
//        arena.materials.get(0).set(ColorAttribute.createDiffuse(Color.RED));

        instances.add(arena);

        rover = new GCCRover("rover1", modelFactory, Color.RED);

        environment = new Environment();

        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.1f, 0.1f, 0.1f, 1f));
        DirectionalLight light = new DirectionalLight();
        environment.add(light.set(1f, 1f, 1f, new Vector3(0f, -20f, 0f)));
        camera.rotateAround(new Vector3(0f, 0f, 0f), new Vector3(1f, 0f, 0f), -45f);

        ModelBuilder mb = new ModelBuilder();
        mb.begin();

    }

    @Override
    public void render() {

        // modelInstance.transform.rotate(new Vector3(0f, 1f, 0f), 1f);
        Gdx.gl.glClearColor(0.6f, 0.75f, 1f, 1f);

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        Gdx.gl20.glEnable(GL20.GL_DEPTH_TEST);
        Gdx.gl20.glDepthFunc(GL20.GL_LEQUAL);
        Gdx.gl.glEnable(Gdx.gl20.GL_POLYGON_OFFSET_FILL);
        Gdx.gl20.glPolygonOffset(1.0f, 1.0f);


        if (cameratype == 0) {
            Vector3 pos = new Vector3();
            pos = rover.getTransform().getTranslation(pos);
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

            camera.fieldOfView = 120f;;
        } else if (cameratype == 2) {
            Vector3 pos = new Vector3();
            pos = rover.getTransform().getTranslation(pos);
            camera.lookAt(pos);
            camera.position.set(240f, 0f, 10f);
            camera.up.set(new Vector3(0, 1, 0));

        }

        camera.update();

        batch.begin(camera);
        batch.render(instances, environment);
        rover.render(batch, environment);
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
    }

    @Override
    public void dispose() {
        batch.dispose();
        tpModel.dispose();
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

        if (keycode == Input.Keys.A) {
            moveleft = true;
        } else if (keycode == Input.Keys.D) {
            moveright = true;

        } else if (keycode == Input.Keys.W) {
            moveup = true;

        } else if (keycode == Input.Keys.S) {
            movedown = true;
        } else if (keycode == Input.Keys.TAB) {
            camera.fieldOfView = 4f;
        }
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        if (keycode == Input.Keys.A) {
            moveleft = false;
        } else if (keycode == Input.Keys.D) {
            moveright = false;
        } else if (keycode == Input.Keys.W) {
            moveup = false;
        } else if (keycode == Input.Keys.S) {
            movedown = false;
        } else if (keycode == Input.Keys.TAB) {
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

}
