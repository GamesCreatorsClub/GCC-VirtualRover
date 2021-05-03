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
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.IntSet;

import org.ah.piwars.fishtank.game.FishtankGame;
import org.ah.piwars.fishtank.input.FishtankPlayerInputs;
import org.ah.piwars.fishtank.view.ChatColor;
import org.ah.piwars.fishtank.view.ChatListener;
import org.ah.piwars.fishtank.view.Console;
import org.ah.piwars.fishtank.world.CameraPositionLink;
import org.ah.piwars.fishtank.world.FishModelLink;
import org.ah.themvsus.engine.client.ClientEngine;
import org.ah.themvsus.engine.common.game.Player;

import static org.ah.piwars.fishtank.game.FishtankGame.HALF_DEPTH;

import static java.lang.String.format;

public class FishtankScreen extends ScreenAdapter implements ChatListener {

    public static final float WORLD_SCALE = 0.1f;
    public static final float WORLD_SCALE2 = 0.01f;

    protected Console console;
    private ServerCommunicationAdapter adapter;

    private int width;
    private int height;

    protected OrthographicCamera hudCamera;
    protected PerspectiveCamera camera;

    protected FishtankPlayerInputs playerInputs = new FishtankPlayerInputs();

    private WiiMoteCameraController wiiMoteCameraController;

    private SpriteBatch spriteBatch;
    private ModelBatch modelBatch;

    private Environment environment;

    private AssetManager assetManager;

    private Model fishtankLeftSideModel;
    private ModelInstance fishtankLeftSideInstance;
    private Model fishtankRightSideModel;
    private ModelInstance fishtankRightSideInstance;
    private Model fishtankBackSideModel;
    private ModelInstance fishtankBackSideInstance;
    private Model fishtankBottomModel;
    private ModelInstance fishtankBottomInstance;
    private PlatformSpecific platformSpecific;

    protected BitmapFont fontSmallMono;


    public FishtankScreen(PlatformSpecific platformSpecific, AssetManager assetManager, Console console, ServerCommunicationAdapter adapter) {
        this.platformSpecific = platformSpecific;
        this.assetManager = assetManager;
        this.console = console;
        this.adapter = adapter;
    }

    public void create() {
        modelBatch = new ModelBatch();
        spriteBatch = new SpriteBatch();

        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, .4f, .4f, .4f, 1f));
        environment.add(new DirectionalLight().set(new Color(0.8f, 0.8f, 0.8f, 1f), new Vector3(0f, -1f, -1f).nor()));

        hudCamera = new OrthographicCamera(width, height);
        hudCamera.setToOrtho(true);

        float halfHeight = FishtankGame.HALF_HEIGHT * WORLD_SCALE;
        float halfDisplayWidth = 160f * WORLD_SCALE * 0.5f;

        float yOffset;
        yOffset = - (halfHeight - HALF_DEPTH * WORLD_SCALE) / 2f;
//        yOffset = -20f * WORLD_SCALE;
//        yOffset = 10f * WORLD_SCALE;
        yOffset = 0f;

        float cameraAngle;
        cameraAngle = 6.5f;
        float cameraAngleRad = (float)(cameraAngle * Math.PI / 180f);
        float cameraAngleTan = (float)Math.tan(cameraAngleRad);

        float camera_distance;
        camera_distance = halfDisplayWidth / cameraAngleTan;

        camera = new PerspectiveCamera(cameraAngle, width, height);
        camera.near = camera_distance;
        camera.far = camera_distance + halfHeight * 2.5f;

        System.out.println("halfDisplayWidth=" + halfDisplayWidth);
        System.out.println("halfHeight=" + halfHeight);
        System.out.println("cameraAngleTan=" + cameraAngleTan);
        System.out.println("camera_distance=" + camera_distance);

        if (platformSpecific.getTankView() == PlatformSpecific.TankView.FRONT) {
            camera.position.set(0f, yOffset, halfHeight + camera_distance);
            camera.lookAt(0f, 0f, 0f);
        } else if (platformSpecific.getTankView() == PlatformSpecific.TankView.LEFT) {
            camera.position.set(-(halfHeight + camera_distance), yOffset, 0f);
            camera.lookAt(0f, 0f, 0f);
        } else if (platformSpecific.getTankView() == PlatformSpecific.TankView.RIGHT) {
            camera.position.set(halfHeight + camera_distance, yOffset, 0f);
            camera.lookAt(0f, 0f, 0f);
        } else {
            // ERROR but we don't want to do anything about it...
            camera.position.set(0f, 0f, halfHeight * camera_distance);
            camera.lookAt(0f, 0f, 0f);
        }

        wiiMoteCameraController = new WiiMoteCameraController(camera, cameraAngle, platformSpecific.getTankView());
        Gdx.input.setInputProcessor(wiiMoteCameraController);

        unknownObjectIds = new IntSet();

        createFishtankSides();
    }

    float t = 0;

    private IntSet unknownObjectIds;

    @Override
    public void render(float delta) {

        CameraPositionLink cameraPositionModel = adapter.getCameraPositionModel();
        if (platformSpecific.isCameraInputAllowed() && cameraPositionModel != null) {
            cameraPositionModel.updateFrom(wiiMoteCameraController);
        }

        progressEngine();

        if (cameraPositionModel != null) {
            cameraPositionModel.updateTo(wiiMoteCameraController);
        }
        // wiiMoteCameraController.setCamPosition(wiiMoteCameraController.getInput().x, wiiMoteCameraController.getInput().y);
        wiiMoteCameraController.updateCamera();

        t = (t + delta * 0.02f) % 1f;

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        Gdx.gl.glClearColor(0.2f, 1f, 1.0f, 1f);
        // Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl20.glEnable(GL20.GL_DEPTH_TEST);

        modelBatch.begin(camera);
        modelBatch.render(fishtankLeftSideInstance, environment);
        modelBatch.render(fishtankRightSideInstance, environment);
        modelBatch.render(fishtankBackSideInstance, environment);
        modelBatch.render(fishtankBottomInstance, environment);
        // modelBatch.render(gridAndAxesInstance, environment);

        for (VisibleObject visibleObject : adapter.getVisibleObjects().values()) {
            if (visibleObject instanceof FishModelLink) {
                FishModelLink fishModel = (FishModelLink) visibleObject;

                fishModel.render(delta, modelBatch, environment);
            }
        }

        modelBatch.end();
        spriteBatch.setProjectionMatrix(hudCamera.combined);
        spriteBatch.begin();

        drawText(fontSmallMono, format("%.4f, %.4f", wiiMoteCameraController.getInput().x, wiiMoteCameraController.getInput().y), 10, height - 10);
        drawText(fontSmallMono, format("%.4f, %.4f, %.4f", camera.position.x, camera.position.y, camera.position.z), 10, height - 30);
        drawText(fontSmallMono, format("%.4f, %.4f, %.4f", camera.direction.x, camera.direction.y, camera.direction.z), 10, height - 50);

        spriteBatch.end();
    }

    private void drawText(BitmapFont font, String text, float x, float y) {
        fontSmallMono.setColor(Color.BLACK);
        for (int i = -1; i < 2; i += 2) {
            for (int j = -1; j < 2; j += 2) {
                fontSmallMono.draw(spriteBatch, text, x + i, y + j);
            }
        }
        fontSmallMono.setColor(Color.WHITE);
        fontSmallMono.draw(spriteBatch, text, x, y);
    }

    @Override
    public void dispose() {
        // gridAndAxesModel.dispose();
        fishtankLeftSideModel.dispose();
        fishtankRightSideModel.dispose();
        fishtankBackSideModel.dispose();
        fishtankBottomModel.dispose();
        modelBatch.dispose();
        spriteBatch.dispose();
    }

    @Override
    public void show() {
        if (fontSmallMono == null) {
            fontSmallMono = assetManager.get("font/droidsansmono-15.fnt");
        }
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

        if (platformSpecific.getTankView() == PlatformSpecific.TankView.FRONT) {
            wiiMoteCameraController.setCamPosition(
                    wiiMoteCameraController.getInput().x,
                    wiiMoteCameraController.getInput().y,
                    camera.position.z);
        } else {
            wiiMoteCameraController.setCamPosition(
                    camera.position.x,
                    wiiMoteCameraController.getInput().y,
                    wiiMoteCameraController.getInput().x);
        }
//        wiiMoteCameraController.updateCameraAfterController(camera.position.x, camera.position.y);
    }

    @Override
    public void pause() {
    }

    protected void progressEngine() {
        ClientEngine<FishtankGame> engine = adapter.getEngine();
        if (engine != null) {
            long now = System.currentTimeMillis();

            if (platformSpecific.isCameraInputAllowed()) {
                CameraPositionLink cameraPositionModel = adapter.getCameraPositionModel();
                if (cameraPositionModel != null) {
                    adapter.setCameraPositionInput(cameraPositionModel.getPlayerInput());
                }
            }

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
        MeshPartBuilder left = modelBuilder.part("leftSide", GL20.GL_TRIANGLES, Usage.Position | Usage.ColorUnpacked | Usage.Normal, new Material());
        left.setColor(new Color(0f, 0f, 0.7f, 1f));
        left.rect(-w, -d, -h, -w, d, -h, -w, d, h, -w, -d, h, 1f, 0f, 0f);
        fishtankLeftSideModel = modelBuilder.end();
        fishtankLeftSideInstance = new ModelInstance(fishtankLeftSideModel, "fishtankLeftSide");

        modelBuilder = new ModelBuilder();
        modelBuilder.begin();
        modelBuilder.node().id = "fishtankRightSide";
        MeshPartBuilder right = modelBuilder.part("rightSide", GL20.GL_TRIANGLES, Usage.Position | Usage.ColorUnpacked | Usage.Normal, new Material());
        right.setColor(new Color(0f, 0f, 0.7f, 1f));
        right.rect(w, -d, -h, w, -d, h, w, d, h, w, d, -h, -1f, 0f, 0f);
        fishtankRightSideModel = modelBuilder.end();
        fishtankRightSideInstance = new ModelInstance(fishtankRightSideModel, "fishtankRightSide");

        modelBuilder = new ModelBuilder();
        modelBuilder.begin();
        modelBuilder.node().id = "fishtankBackSide";
        MeshPartBuilder back = modelBuilder.part("backSide", GL20.GL_TRIANGLES, Usage.Position | Usage.ColorUnpacked | Usage.Normal, new Material());
        back.setColor(new Color(0f, 0f, 0.7f, 1f));
        back.rect(-w, d, -h, -w, -d, -h, w, -d, -h, w, d, -h, 0f, 0f, -1f);
        fishtankBackSideModel = modelBuilder.end();
        fishtankBackSideInstance = new ModelInstance(fishtankBackSideModel, "fishtankBackSide");

//        Material bottomMaterial = new Material(
//                TextureAttribute.createDiffuse(assetManager.get("Pebbles_025_BaseColor.jpg", Texture.class)),
//                TextureAttribute.createBump(assetManager.get("Pebbles_025_Normal.jpg", Texture.class))
//        );

//        modelBuilder = new ModelBuilder();
//        modelBuilder.begin();
//        modelBuilder.node().id = "fishtankBottom";
//        // long bottomAttributes = Usage.Position | Usage.TextureCoordinates | Usage.ColorUnpacked | Usage.Normal | Usage.BiNormal | Usage.Tangent;
//        long bottomAttributes = Usage.Position | Usage.TextureCoordinates | Usage.Normal;
//        MeshPartBuilder bottom = modelBuilder.part("bottom", GL20.GL_TRIANGLES, bottomAttributes, bottomMaterial);
//        bottom.setColor(new Color(0f, 0f, 0f, 1f));
//        bottom.rect(-w, -d, -h, -w, -d, h, w, -d, h, w, -d, -h, 0f, 1f, 0f);
//        fishtankBottomModel = modelBuilder.end();
//        fishtankBottomInstance = new ModelInstance(fishtankBottomModel, "fishtankBottom");

        fishtankBottomModel = assetManager.get(FishtankMain.TANK_BOTTOM_MODEL);
        fishtankBottomInstance = new ModelInstance(fishtankBottomModel);
        fishtankBottomInstance.transform.translate(0f, -d, 0f);
        fishtankBottomInstance.transform.scl(0.0125f);
    }
}
