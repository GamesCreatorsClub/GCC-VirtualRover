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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
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
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.IntSet;

import org.ah.piwars.fishtank.game.FishtankGame;
import org.ah.piwars.fishtank.view.ChatColor;
import org.ah.piwars.fishtank.view.ChatListener;
import org.ah.piwars.fishtank.view.Console;
import org.ah.piwars.fishtank.world.FishModelLink;
import org.ah.themvsus.engine.client.ClientEngine;
import org.ah.themvsus.engine.common.game.Player;

public class FishtankScreen extends ScreenAdapter implements ChatListener {

    protected Console console;
    private ServerCommunicationAdapter adapter;

    private int width;
    private int height;

    protected OrthographicCamera hudCamera;
    protected PerspectiveCamera camera;

    private CameraInputController inputController;

    private ModelBatch modelBatch;

    private Environment environment;

    private Model fishModel;
    private ModelInstance fishModelInstance;

    private AssetManager assetManager;

    private Model model;
//    private AnimationController animationController;

    private ModelInstance backgroundInstance;


    public FishtankScreen(AssetManager assetManager, Console console, ServerCommunicationAdapter adapter) {
        this.assetManager = assetManager;
        this.console = console;
        this.adapter = adapter;
    }

    public void create() {
        modelBatch = new ModelBatch();

        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, .4f, .4f, .4f, 1f));
        environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, 0f, -10f, 10f));

        hudCamera = new OrthographicCamera(width, height);
        hudCamera.setToOrtho(true);

        camera = new PerspectiveCamera(45, width, height);
        camera.position.set(0f, 48f, -30f);
        camera.lookAt(0f, 0f, 0f);
        camera.near = 0.1f;
        camera.far = 300f;
        camera.update();

        inputController = new CameraInputController(camera);
        Gdx.input.setInputProcessor(inputController);

        unknownObjectIds = new IntSet();

        fishModel = assetManager.get("fish/spadefish/spadefish.g3db", Model.class);
        fishModelInstance = new ModelInstance(fishModel);

        spine1 = fishModelInstance.getNode("spine1");
        spine2 = fishModelInstance.getNode("spine2");
        spine3 = fishModelInstance.getNode("spine3");
        spine4 = fishModelInstance.getNode("spine4");
        spine1.isAnimated = true;
        spine2.isAnimated = true;
        spine3.isAnimated = true;
        spine4.isAnimated = true;



        float GRID_MIN = -10f;
        float GRID_MAX = 10f;
        float GRID_STEP = 1f;

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
        backgroundInstance = new ModelInstance(model, "gridAndAxes");
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

    private IntSet unknownObjectIds;

    @Override
    public void render(float delta) {
        progressEngine();

        inputController.update();

        t = (t + delta * 0.02f) % 1f;
        fishModelInstance.transform.idt();

        fishModelInstance.transform.scale(0.01f, 0.01f, 0.01f);
        fishModelInstance.transform.rotate(0f, 1f, 0f, 90);

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

//        Gdx.gl.glViewport(0, 0, width, height);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        Gdx.gl.glClearColor(0.2f, 1f, 1.0f, 1f);
        Gdx.gl20.glEnable(GL20.GL_DEPTH_TEST);

        modelBatch.begin(camera);
        modelBatch.render(backgroundInstance, environment);
//        modelBatch.render(fishModelInstance, environment);

        for (VisibleObject visibleObject : adapter.getVisibleObjects().values()) {
            if (visibleObject instanceof FishModelLink) {
                FishModelLink fishModel = (FishModelLink) visibleObject;

                fishModel.render(modelBatch, environment);
            }
        }

        modelBatch.end();
    }

    @Override
    public void dispose() {
        modelBatch.dispose();
    }

    @Override
    public void resume() {
    }

    @Override
    public void resize(int width, int height) {
        this.width = width;
        this.height = height;
        if (console != null) {
            console.setConsoleWidth(width);
        }
        if (hudCamera != null) {
            hudCamera.setToOrtho(false, width, height);
            hudCamera.update();
        }
        if (camera != null) {
            camera.viewportWidth = width;
            camera.viewportHeight = height;
        }
    }

    @Override
    public void pause() {
    }

    protected void progressEngine() {
        ClientEngine<FishtankGame> engine = adapter.getEngine();
        if (engine != null) {
            long now = System.currentTimeMillis();
            engine.progressEngine(now, unknownObjectIds);

            if (unknownObjectIds.size > 0) {
                adapter.requestFullUpdate(unknownObjectIds);
            }
        }
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
