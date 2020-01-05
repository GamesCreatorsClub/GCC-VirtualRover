package org.ah.piwars.virtualrover.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;

import org.ah.piwars.virtualrover.MainGame;
import org.ah.piwars.virtualrover.ModelFactory;
import org.ah.piwars.virtualrover.PlatformSpecific;
import org.ah.piwars.virtualrover.ServerCommunicationAdapter;
import org.ah.piwars.virtualrover.backgrounds.PerlinNoiseBackground;
import org.ah.piwars.virtualrover.camera.CameraControllersManager;
import org.ah.piwars.virtualrover.camera.CinematicCameraController;
import org.ah.piwars.virtualrover.challenges.CanyonsOfMarsArena;
import org.ah.piwars.virtualrover.game.PiWarsGame;
import org.ah.piwars.virtualrover.game.attachments.CameraAttachment;
import org.ah.piwars.virtualrover.game.rovers.RoverType;
import org.ah.piwars.virtualrover.utils.SoundManager;
import org.ah.piwars.virtualrover.view.Console;

import static org.ah.piwars.virtualrover.MainGame.SCALE;

public class CanyonsOfMarsScreen extends AbstractStandardScreen implements ChallengeScreen {

    private PerspectiveCamera camera;
    private CameraControllersManager cameraControllersManager;
    private InputMultiplexer cameraInputMultiplexer;

    public CanyonsOfMarsScreen(MainGame game,
            PlatformSpecific platformSpecific,
            AssetManager assetManager,
            SoundManager soundManager,
            ModelFactory modelFactory,
            ServerCommunicationAdapter serverCommunicationAdapter,
            Console console) {
        super(game, platformSpecific, assetManager, soundManager, modelFactory, serverCommunicationAdapter, console);

        setBackground(new PerlinNoiseBackground());

        CanyonsOfMarsArena challenge = new CanyonsOfMarsArena(modelFactory, assetManager);
        challenge.init();

        setChallenge(challenge);

        camera = new PerspectiveCamera(45, 800, 480);
        camera.position.set(300f * SCALE, 480f * SCALE, 300f * SCALE);
        camera.lookAt(0f, 0f, 0f);
        camera.near = 0.02f;
        camera.far = 1000f;

        cameraInputMultiplexer = new InputMultiplexer();
        Gdx.input.setInputProcessor(cameraInputMultiplexer);
        Gdx.input.setCursorCatched(false);

        cameraControllersManager = new CameraControllersManager();
        cameraInputMultiplexer.addProcessor(this);
        cameraInputMultiplexer.addProcessor(cameraControllersManager);

        cameraControllersManager.addCameraController("Cinematic", new CinematicCameraController(camera, serverCommunicationAdapter));
        cameraControllersManager.addCameraController("Default", new CameraInputController(camera));
        // cameraControllersManager.addCameraController("Other", new CinematicCameraController2(camera, players));
    }

    @Override
    public void reset() {
        camera.position.set(300f * SCALE, 480f * SCALE, 300f * SCALE);
        camera.lookAt(0f, 0f, 0f);
        camera.near = 0.02f;
        camera.far = 1000f;

        Gdx.input.setInputProcessor(cameraInputMultiplexer);
        Gdx.input.setCursorCatched(false);
        challenge.init();
    }

    @Override
    public void dispose() {
        super.dispose();
    }

    @Override
    public void show() {
        super.show();
    }

    @Override
    public void hide() {
        super.hide();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.6f, 0.75f, 1f, 1f);

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl20.glEnable(GL20.GL_DEPTH_TEST);

        progressEngine();

        if (serverCommunicationAdapter.isLocal()) {
            if (serverCommunicationAdapter.hasPlayerOne()) {
                moveRovers();
            } else if (serverCommunicationAdapter.isLocal()) {
                setMiddleMessage("Press space to begin", true);
            }
        } else {
            if (serverCommunicationAdapter.hasPlayerOne()) {
                moveRovers();
            }
        }

        cameraControllersManager.update();
        camera.update();

        CameraAttachment cameraAttachment = processCameraAttachemnt();

        if (renderBackground) {
            background.render(camera, batch, environment);
        }

        batch.begin(camera);

        challenge.render(batch, environment, null, serverCommunicationAdapter.getVisibleObjects());

        batch.end();

        spriteBatch.begin();

        if (drawFPS) {
            drawFPS();
        }
        if (cameraAttachment != null) {
            showAttachedCamera();
        }
        spriteBatch.end();

        drawStandardMessages();

        if (console != null) {
            console.render();
        }

        if (serverCommunicationAdapter.isMakeCameraSnapshot()) {
            serverCommunicationAdapter.makeCameraSnapshot(snapshotData);
        }
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);

        // TODO add handling of current main camera
    }

    @Override
    public boolean keyDown(int keycode) {
        super.keyDown(keycode);
        if (keycode == Input.Keys.SPACE && serverCommunicationAdapter.isLocal() && !serverCommunicationAdapter.hasPlayerOne()) {
            PiWarsGame game = serverCommunicationAdapter.getEngine().getGame();

            // TODO - select rover type properly
            /* Rover player1 = */game.spawnRover(1, "Blue", RoverType.GCC);
            serverCommunicationAdapter.setLocalPlayerIds(1);
        }

        if (keycode == Input.Keys.TAB) {
            camera.fieldOfView = 4f;
        }

        return false;
    }

    @Override public boolean keyTyped(char character) {
        return super.keyTyped(character);
    }
}
