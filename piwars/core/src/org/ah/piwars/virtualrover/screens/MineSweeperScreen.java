package org.ah.piwars.virtualrover.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;

import org.ah.piwars.virtualrover.MainGame;
import org.ah.piwars.virtualrover.ModelFactory;
import org.ah.piwars.virtualrover.PlatformSpecific;
import org.ah.piwars.virtualrover.ServerCommunicationAdapter;
import org.ah.piwars.virtualrover.backgrounds.PerlinNoiseBackground;
import org.ah.piwars.virtualrover.game.PiWarsGame;
import org.ah.piwars.virtualrover.utils.SoundManager;
import org.ah.piwars.virtualrover.view.Console;

import static org.ah.piwars.virtualrover.MainGame.SCALE;

public class MineSweeperScreen extends AbstractCameraChallengeScreen implements ChallengeScreen {

    public MineSweeperScreen(MainGame game,
            PlatformSpecific platformSpecific,
            AssetManager assetManager,
            SoundManager soundManager,
            ModelFactory modelFactory,
            ServerCommunicationAdapter serverCommunicationAdapter,
            Console console) {
        super(game, platformSpecific, assetManager, soundManager, modelFactory, serverCommunicationAdapter, console);

        setBackground(new PerlinNoiseBackground());

        cinematicCameraController.setCameraHeight(1300f);
        cinematicCameraController.setCameraRadius(1000f);
    }
//
//    @Override
//    protected void setupRequiredCameraCombination() {
//        cameraCombination = CAMERA_COMBINATIONS[3];
//    }

    @Override
    public void reset() {
        super.reset();
        camera.position.set(300f * SCALE, 780f * SCALE, 300f * SCALE);
        camera.lookAt(0f, 0f, 0f);
        camera.near = 0.02f;
        camera.far = 1000f;

        Gdx.input.setInputProcessor(cameraInputMultiplexer);
        Gdx.input.setCursorCatched(false);
        challenge.init();

        cameraCombination = CAMERA_COMBINATIONS[3];
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

//    @Override
//    public void render(float delta) {
//        Gdx.gl.glClearColor(0.6f, 0.75f, 1f, 1f);
//
//        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
//        Gdx.gl20.glEnable(GL20.GL_DEPTH_TEST);
////        Gdx.gl20.glDepthFunc(GL20.GL_LEQUAL);
////        Gdx.gl.glEnable(GL20.GL_POLYGON_OFFSET_FILL);
////        Gdx.gl20.glPolygonOffset(1.0f, 1.0f);
//
//        if (!isSuspended()) {
//            progressEngine();
//        }
//
//        if (serverCommunicationAdapter.isLocal()) {
//            if (isSuspended()) {
//                setMiddleMessage("Press ESC to leave", true);
//            } else if (serverCommunicationAdapter.hasPlayerOne()) {
//                moveRovers();
//            } else if (serverCommunicationAdapter.isLocal()) {
//                setMiddleMessage("Press space to begin", true);
//            }
//        } else {
//            if (serverCommunicationAdapter.hasPlayerOne()) {
//                moveRovers();
//            }
//        }
//
//        boolean inGame = isInGame();
//
//        cameraControllersManager.update();
//        camera.update();
//
//        CameraAttachment cameraAttachment = processCameraAttachemnt();
//
//        PerspectiveCamera mainCamera = camera;
//        if (cameraAttachment != null && inGame) {
//            mainCamera = attachedCamera;
//        }
//
//        if (renderBackground) {
//            background.render(mainCamera, batch, environment);
//        }
//
//        batch.begin(mainCamera);
//
//        challenge.render(batch, environment, null, serverCommunicationAdapter.getVisibleObjects());
//
//        batch.end();
//
//        spriteBatch.begin();
//
//        if (drawFPS) {
//            drawFPS();
//        }
//
//        if (!inGame && cameraAttachment != null) {
//            showAttachedCamera();
//        }
//        spriteBatch.end();
//
//        drawStandardMessages();
//
//        if (console != null) {
//            console.render();
//        }
//
//        if (serverCommunicationAdapter.isMakeCameraSnapshot()) {
//            serverCommunicationAdapter.makeCameraSnapshot(snapshotData);
//        }
//    }

    @Override
    public boolean keyDown(int keycode) {
        super.keyDown(keycode);
        if (keycode == Input.Keys.SPACE && serverCommunicationAdapter.isLocal() && !serverCommunicationAdapter.hasPlayerOne()) {
            PiWarsGame game = serverCommunicationAdapter.getEngine().getGame();

            // TODO - select rover type properly
            /* Rover player1 = */game.spawnRover(1, "Blue", mainGameApp.getSelectedRover1());
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
