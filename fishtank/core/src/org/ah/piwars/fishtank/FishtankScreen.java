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
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
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

    public static final float WORLD_SCALE = 0.1f;
    public static final float WORLD_SCALE2 = 0.01f;

    protected Console console;
    private ServerCommunicationAdapter adapter;

    private int width;
    private int height;

    protected OrthographicCamera hudCamera;
    protected PerspectiveCamera camera;

    private CameraInputController inputController;

    private ModelBatch modelBatch;

    private Environment environment;

    private AssetManager assetManager;

    private Model gridAndAxesModel;
    private ModelInstance gridAndAxesInstance;
    private Model fishtankLeftSideModel;
    private ModelInstance fishtankLeftSideInstance;
    private Model fishtankRightSideModel;
    private ModelInstance fishtankRightSideInstance;
    private Model fishtankBackSideModel;
    private ModelInstance fishtankBackSideInstance;


    public FishtankScreen(AssetManager assetManager, Console console, ServerCommunicationAdapter adapter) {
        this.assetManager = assetManager;
        this.console = console;
        this.adapter = adapter;
    }

    public void create() {
        modelBatch = new ModelBatch();

        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, .4f, .4f, .4f, 1f));
        environment.add(new DirectionalLight().set(new Color(0.8f, 0.8f, 0.8f, 1f), new Vector3(0f, -1f, -1f).nor()));

        hudCamera = new OrthographicCamera(width, height);
        hudCamera.setToOrtho(true);

        float h = FishtankGame.HALF_HEIGHT * WORLD_SCALE;

        camera = new PerspectiveCamera(45, width, height);
        float tang = 1f / (float)Math.tan(Math.PI / 8.0);
        camera.position.set(0f, 0f, h * tang);
        camera.lookAt(0f, 0f, h);
        camera.near = 0.1f;
        camera.far = 300f;

        inputController = new CameraInputController(camera);
        Gdx.input.setInputProcessor(inputController);
        camera.update();

        unknownObjectIds = new IntSet();

        gridAndAxesModel = createGrid();
        gridAndAxesInstance = new ModelInstance(gridAndAxesModel, "gridAndAxes");

        createFishtankSides();
    }

    float t = 0;

    private IntSet unknownObjectIds;

    @Override
    public void render(float delta) {
        progressEngine();

        inputController.update();
        camera.update();

        t = (t + delta * 0.02f) % 1f;

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        Gdx.gl.glClearColor(0.2f, 1f, 1.0f, 1f);
        Gdx.gl20.glEnable(GL20.GL_DEPTH_TEST);

        modelBatch.begin(camera);
        modelBatch.render(fishtankLeftSideInstance, environment);
        modelBatch.render(fishtankRightSideInstance, environment);
        modelBatch.render(fishtankBackSideInstance, environment);
        modelBatch.render(gridAndAxesInstance, environment);

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
        gridAndAxesModel.dispose();
        fishtankLeftSideModel.dispose();
        fishtankRightSideModel.dispose();
        fishtankBackSideModel.dispose();
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

    public Model createGrid() {
        float GRID_MIN = -FishtankGame.HALF_WIDTH * WORLD_SCALE;
        float GRID_MAX = FishtankGame.HALF_WIDTH * WORLD_SCALE;
        float GRID_STEP = 1f;
        float GRID_BOTTOM = -FishtankGame.HALF_DEPTH * WORLD_SCALE;

        ModelBuilder modelBuilder = new ModelBuilder();
        modelBuilder.begin();

//        Texture texture = new Texture(Gdx.files.internal("badlogic.jpg"));
//        modelBuilder.manage(texture);
//
//        modelBuilder.node().id = "arrow";
//        modelBuilder.part("arrow", GL20.GL_TRIANGLES, Usage.Position | Usage.Normal | Usage.TextureCoordinates,
//                new Material("diffuse-map", TextureAttribute.createDiffuse(texture))).arrow(0, 0, 0, 2, 0, 0, 0.5f, 0.5f, 10);

        modelBuilder.node().id = "gridAndAxes";
        MeshPartBuilder builder = modelBuilder.part("grid", GL20.GL_LINES, Usage.Position | Usage.ColorUnpacked, new Material());
        builder.setColor(Color.LIGHT_GRAY);
        for (float t = GRID_MIN; t <= GRID_MAX; t += GRID_STEP) {
            builder.line(t, GRID_BOTTOM, GRID_MIN, t, GRID_BOTTOM, GRID_MAX);
            builder.line(GRID_MIN, GRID_BOTTOM, t, GRID_MAX, GRID_BOTTOM, t);
        }

        builder = modelBuilder.part("axes", GL20.GL_LINES, Usage.Position | Usage.ColorUnpacked, new Material());
        builder.setColor(Color.RED);
        builder.line(0, 0, 0, 100, 0, 0);
        builder.setColor(Color.GREEN);
        builder.line(0, 0, 0, 0, 100, 0);
        builder.setColor(Color.BLUE);
        builder.line(0, 0, 0, 0, 0, 100);

        return modelBuilder.end();
    }

    public void createFishtankSides() {
        float w = FishtankGame.HALF_WIDTH * WORLD_SCALE;
        float h = FishtankGame.HALF_HEIGHT * WORLD_SCALE;
        float d = FishtankGame.HALF_DEPTH * WORLD_SCALE;

        ModelBuilder modelBuilder = new ModelBuilder();
        modelBuilder.begin();

        modelBuilder.node().id = "fishtankLeftSide";
        MeshPartBuilder left = modelBuilder.part("leftSide", GL20.GL_TRIANGLES, Usage.Position | Usage.ColorUnpacked, new Material());
        left.setColor(new Color(0f, 0f, 0.7f, 1f));
        left.rect(-w, -d, -h, -w, d, -h, -w, d, h, -w, -d, h, -1f, 0f, 0f);
        fishtankLeftSideModel = modelBuilder.end();
        fishtankLeftSideInstance = new ModelInstance(fishtankLeftSideModel, "fishtankLeftSide");

        modelBuilder = new ModelBuilder();
        modelBuilder.begin();
        modelBuilder.node().id = "fishtankRightSide";
        MeshPartBuilder right = modelBuilder.part("rightSide", GL20.GL_TRIANGLES, Usage.Position | Usage.ColorUnpacked, new Material());
        right.setColor(new Color(0f, 0f, 0.7f, 1f));
        right.rect(w, -d, -h, w, -d, h, w, d, h, w, d, -h, -1f, 0f, 0f);
        fishtankRightSideModel = modelBuilder.end();
        fishtankRightSideInstance = new ModelInstance(fishtankRightSideModel, "fishtankRightSide");

        modelBuilder = new ModelBuilder();
        modelBuilder.begin();
        modelBuilder.node().id = "fishtankBackSide";
        MeshPartBuilder back = modelBuilder.part("backSide", GL20.GL_TRIANGLES, Usage.Position | Usage.ColorUnpacked, new Material());
        back.setColor(new Color(0f, 0f, 0.7f, 1f));
        back.rect(-w, d, -h, -w, -d, -h, w, -d, -h, w, d, -h, 0f, -1f, 0f);
        fishtankBackSideModel = modelBuilder.end();
        fishtankBackSideInstance = new ModelInstance(fishtankBackSideModel, "fishtankBackSide");
    }
}