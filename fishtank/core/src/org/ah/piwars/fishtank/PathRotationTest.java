package org.ah.piwars.fishtank;
/*******************************************************************************
 * Copyright 2011 See AUTHORS file.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.CatmullRomSpline;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Path;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

public class PathRotationTest extends ApplicationAdapter {
    private static Vector3 UP = Vector3.Y;

    private PerspectiveCamera cam;
    private CameraInputController inputController;
    private ModelBatch modelBatch;
    private Model model;
    private Model fishModel;
    private Array<ModelInstance> instances;
    private Environment environment;
    private ModelInstance fishModelInstance;
    private Path<Vector3> path;

    private final float GRID_MIN = -10f;
    private final float GRID_MAX = 10f;
    private final float GRID_STEP = 1f;
    private AssetManager assetManager;
//    private AnimationController animationController;
    private boolean ready = false;

    @Override
    public void create() {
        modelBatch = new ModelBatch();

        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, .4f, .4f, .4f, 1f));
        environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));

        cam = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        cam.position.set(4f, 5f, 4f);
        cam.lookAt(0, 1, 0);
        cam.near = 0.1f;
        cam.far = 300f;
        cam.update();

        assetManager = new AssetManager();
        assetManager.load("fish/spadefish/spadefish.g3db", Model.class);

        ModelBuilder modelBuilder = new ModelBuilder();
        modelBuilder.begin();

        Texture texture = new Texture(Gdx.files.internal("badlogic.jpg"));
        modelBuilder.manage(texture);

        modelBuilder.node().id = "arrow";
        modelBuilder.part("arrow", GL20.GL_TRIANGLES, Usage.Position | Usage.Normal | Usage.TextureCoordinates,
                new Material("diffuse-map", TextureAttribute.createDiffuse(texture))).arrow(0, 0, 0, 2, 0, 0, 0.5f, 0.5f, 10);

        modelBuilder.node().id = "gridAndAxes";
        MeshPartBuilder builder = modelBuilder.part("grid", GL20.GL_LINES, Usage.Position | Usage.ColorUnpacked, new Material());
        builder.setColor(Color.LIGHT_GRAY);
        for (float t = GRID_MIN; t <= GRID_MAX; t += GRID_STEP) {
            builder.line(t, 0, GRID_MIN, t, 0, GRID_MAX);
            builder.line(GRID_MIN, 0, t, GRID_MAX, 0, t);
        }

        builder = modelBuilder.part("axes", GL20.GL_LINES, Usage.Position | Usage.ColorUnpacked, new Material());
        builder.setColor(Color.RED);
        builder.line(0, 0, 0, 100, 0, 0);
        builder.setColor(Color.GREEN);
        builder.line(0, 0, 0, 0, 100, 0);
        builder.setColor(Color.BLUE);
        builder.line(0, 0, 0, 0, 0, 100);

        model = modelBuilder.end();

        instances = new Array<ModelInstance>();
        instances.add(new ModelInstance(model, "gridAndAxes"));
//        instances.add(arrow = new ModelInstance(model, "arrow"));

        Gdx.input.setInputProcessor(inputController = new CameraInputController(cam));

        path = new CatmullRomSpline<>(new Vector3[] {
                new Vector3(0, 0, 0),
                new Vector3(5, 1, 0),
                new Vector3(5, 3, -5),
                new Vector3(-5, 3, -5),
                new Vector3(-5, 5, 5),
                new Vector3(0, 2, 5)
            }, true);
    }

    float t = 0;
    Vector3 position = new Vector3();
    Vector3 direction = new Vector3();
    Vector3 right = new Vector3();
    Vector3 up = new Vector3();
    boolean directionOnly = false;
    boolean dontMove = false;

    private Quaternion spine1Orientation = new Quaternion();
    private Quaternion spine2Orientation = new Quaternion();
    private Quaternion spine3Orientation = new Quaternion();
    private Quaternion spine4Orientation = new Quaternion();
    private Node spine1;
    private Node spine2;
    private Node spine3;
    private Node spine4;

    private void updatePosition(float t) {
        path.derivativeAt(direction, t);
        path.valueAt(position, t);
        direction.nor();
        if (directionOnly) {
            fishModelInstance.transform.setToRotation(Vector3.X, direction);
            fishModelInstance.transform.setTranslation(position);
        } else {
            right.set(Vector3.Y).crs(direction).nor();
            up.set(right).crs(direction).nor();
            fishModelInstance.transform.set(direction, up, right, position).rotate(Vector3.X, 180);
        }
        fishModelInstance.transform.scale(0.01f, 0.01f, 0.01f);
        fishModelInstance.transform.rotate(0f, 1f, 0f, -90);
    }

    @Override
    public void render() {
        if (ready) {
            inputController.update();
            final float delta = Math.min(1f / 10f, Gdx.graphics.getDeltaTime());
//            animationController.update(delta);


            t = (t + delta * 0.02f) % 1f;
            if (!dontMove) {
                updatePosition(t);
            }
//            fishModelInstance.transform.rotate(0f, 0f, 1f, -90);

            final float step = MathUtils.PI / 16;

            float yaw1 = 2f * MathUtils.sin(t * 200f) * 15 / MathUtils.PI;
            float yaw2 = 2f * MathUtils.sin(t * 200f + step) * 15 / MathUtils.PI;
            float yaw3 = 2f * MathUtils.sin(t * 200f + step * 2) * 15 / MathUtils.PI;
            float yaw4 = 2f * MathUtils.sin(t * 200f + step * 3) * 15 / MathUtils.PI;

            spine1Orientation.setEulerAngles(0f, 0f, yaw1).mul(spine1.rotation);
            spine2Orientation.setEulerAngles(0f, 0f, yaw2).mul(spine2.rotation);
            spine3Orientation.setEulerAngles(0f, 0f, yaw3).mul(spine3.rotation);
            spine4Orientation.setEulerAngles(0f, 0f, yaw4).mul(spine4.rotation);
            spine1.localTransform.set(spine1.translation, spine1Orientation, spine1.scale);
            spine2.localTransform.set(spine2.translation, spine2Orientation, spine2.scale);
            spine3.localTransform.set(spine3.translation, spine3Orientation, spine3.scale);
            spine4.localTransform.set(spine4.translation, spine4Orientation, spine4.scale);
            fishModelInstance.calculateTransforms();


            cam.lookAt(position);
            cam.up.set(UP);

            cam.update();

            Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

            modelBatch.begin(cam);
            modelBatch.render(instances, environment);
            modelBatch.end();
        } else {
            if (assetManager.update()) {
                fishModel = assetManager.get("fish/spadefish/spadefish.g3db", Model.class);
                fishModelInstance = new ModelInstance(fishModel);
                instances.add(fishModelInstance);

                spine1 = fishModelInstance.getNode("spine1");
                spine2 = fishModelInstance.getNode("spine2");
                spine3 = fishModelInstance.getNode("spine3");
                spine4 = fishModelInstance.getNode("spine4");
                spine1.isAnimated = true;
                spine2.isAnimated = true;
                spine3.isAnimated = true;
                spine4.isAnimated = true;

                ready = true;
//                spine1.localTransform.setToRotationRad(0f, 1f, 0f, - MathUtils.PI / 2);

//                animationController = new AnimationController(fishModelInstance);
//                animationController.animate("spine1", -1, 1f, null, 0f);
                updatePosition(0);
            } else {
                Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
                Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
                Gdx.gl.glClearColor(0f, 1f, 1f, 1f);
            }
        }
    }

    @Override
    public void dispose() {
        modelBatch.dispose();
        model.dispose();
    }

    @Override
    public void resume() {
    }

    @Override
    public void resize(int width, int height) {
        cam.viewportWidth = width;
        cam.viewportHeight = height;
        cam.update();
    }

    @Override
    public void pause() {
    }
}
