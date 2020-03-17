package org.ah.piwars.virtualrover.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;

import org.ah.piwars.virtualrover.MainGame;
import org.ah.piwars.virtualrover.PlatformSpecific;
import org.ah.piwars.virtualrover.ServerCommunicationAdapter;
import org.ah.piwars.virtualrover.backgrounds.PerlinNoiseBackground;
import org.ah.piwars.virtualrover.game.PiWarsGame;
import org.ah.piwars.virtualrover.utils.SoundManager;
import org.ah.piwars.virtualrover.view.Console;

import static org.ah.piwars.virtualrover.MainGame.SCALE;

public class BlastOffScreen extends AbstractCameraChallengeScreen implements ChallengeScreen {

    public BlastOffScreen(MainGame game,
            PlatformSpecific platformSpecific,
            AssetManager assetManager,
            SoundManager soundManager,
            ServerCommunicationAdapter serverCommunicationAdapter,
            Console console) {
        super(game, platformSpecific, assetManager, soundManager, serverCommunicationAdapter, console);

        setBackground(new PerlinNoiseBackground());
    }

    @Override
    public void reset() {
        super.reset();
        camera.position.set(3900f * SCALE, 480f * SCALE, 300f * SCALE);
        camera.lookAt(3450f, 0f, 0f);
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

//    @Override
//    public void render(float delta) {
//        Gdx.gl.glClearColor(0.6f, 0.75f, 1f, 1f);
//
//        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
//        Gdx.gl.glEnable(GL20.GL_BLEND);
//        Gdx.gl20.glEnable(GL20.GL_DEPTH_TEST);
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
//        cameraControllersManager.update();
//        camera.update();
//
//        CameraAttachment cameraAttachment = processCameraAttachemnt();
//
//        if (renderBackground) {
//            background.render(camera, batch, environment);
//        }
//
//        batch.begin(camera);
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
//        if (cameraAttachment != null) {
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
            /* Rover player1 = */game.spawnRover(1, "Player", mainGameApp.getSelectedRover1());
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
