package org.ah.gcc.display;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.loader.G3dModelLoader;
import com.badlogic.gdx.graphics.g3d.loader.ObjLoader;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.UBJsonReader;

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

    private boolean mouse = true;
    private Model roverModel;
    private CameraInputController camController;


    public Array<ModelInstance> instances;
    private InputMultiplexer cameraInputMultiplexer;


    @Override
    public void create() {
        Gdx.input.setInputProcessor(this);
        Gdx.input.setCursorCatched(mouse);



        camera = new PerspectiveCamera(45, 800, 480);
        camera.position.set(0f, 0f, 100f);
        camera.lookAt(0f, 0f, 0f);
        camera.near = 0.1f;
        camera.far = 10000f;

        camController = new CameraInputController(camera);

        cameraInputMultiplexer = new InputMultiplexer();
        cameraInputMultiplexer.addProcessor(this);
        cameraInputMultiplexer.addProcessor(camController);

        batch = new ModelBatch();

        instances = new Array<ModelInstance>();


        UBJsonReader jsonreader = new UBJsonReader();
        G3dModelLoader modelloader = new G3dModelLoader(jsonreader);
        ObjLoader objModelloader = new ObjLoader();

        tpModel = modelloader.loadModel(Gdx.files.internal("model.g3db"));
        roverModel = objModelloader.loadModel(Gdx.files.internal("3d/balloon1.obj"));
        roverModel = objModelloader.loadModel(Gdx.files.internal("3d/balloon1.obj"));

//        roverModel = modelloader.loadModel(Gdx.files.internal("3d/balloon1.g3db"));

        ModelInstance rover = new ModelInstance(roverModel, 0, 0, 0);

        ModelInstance tp = new ModelInstance(tpModel, 0, 3, 0);
        ModelInstance tp2 = new ModelInstance(tpModel, 0, -3, 0);

//        tp.transform.scale(0.5f, 0.5f, 0.5f);
        rover.transform.scale(0.1f, 0.1f, 0.1f);

//        roverModel.materials.clear();
//        roverModel.materials.addAll(tp.materials);
//
//        rover.materials.clear();
//        rover.materials.addAll(tp.materials);

        instances.add(tp);
        instances.add(tp2);
        instances.add(rover);

        // modelInstance.transform.rotate(new Vector3(1f, 0f, 0f), 45f);

        environment = new Environment();

        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.65f, 0.65f, 0.65f, 1f));
        DirectionalLight light = new DirectionalLight();
        environment.add(light.set(1f, 1f, 1f, new Vector3(0f, -10f, 0f)));
        camera.rotateAround(new Vector3(0f, 0f, 0f), new Vector3(1f, 0f, 0f), -45f);
    }

    @Override
    public void render() {
        // modelInstance.transform.rotate(new Vector3(0f, 1f, 0f), 1f);

        Gdx.gl.glClearColor(0.5f, 0.5f, 0.5f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        Gdx.gl20.glEnable(GL20.GL_DEPTH_TEST);
        camera.update();
        batch.begin(camera);
        batch.render(instances, environment);

        batch.end();

        if (moveleft) {
            camera.rotateAround(new Vector3(0f, 0f, 0f), new Vector3(0f, 1f, 0f), 1f);
        } else if (moveright) {
            camera.rotateAround(new Vector3(0f, 0f, 0f), new Vector3(0f, 1f, 0f), -1f);
        }
        if (moveup) {
            camera.position.add(camera.direction.nor());
        } else if (movedown) {
            camera.position.sub(camera.direction.nor());
        }
    }

    @Override
    public void dispose() {
        batch.dispose();
        tpModel.dispose();
    }

    @Override
    public boolean keyDown(int keycode) {
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
