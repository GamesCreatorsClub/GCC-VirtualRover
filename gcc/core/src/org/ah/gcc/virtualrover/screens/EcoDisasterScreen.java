package org.ah.gcc.virtualrover.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;

import org.ah.gcc.virtualrover.MainGame;
import org.ah.gcc.virtualrover.ModelFactory;
import org.ah.gcc.virtualrover.PlatformSpecific;
import org.ah.gcc.virtualrover.ServerCommunicationAdapter;
import org.ah.gcc.virtualrover.backgrounds.PerlinNoiseBackground;
import org.ah.gcc.virtualrover.camera.CameraControllersManager;
import org.ah.gcc.virtualrover.camera.CinematicCameraController;
import org.ah.gcc.virtualrover.challenges.EcoDisasterArena;
import org.ah.gcc.virtualrover.game.GCCGame;
import org.ah.gcc.virtualrover.game.attachments.CameraAttachment;
import org.ah.gcc.virtualrover.game.rovers.Rover;
import org.ah.gcc.virtualrover.game.rovers.RoverType;
import org.ah.gcc.virtualrover.utils.SoundManager;
import org.ah.gcc.virtualrover.view.Console;

import static org.ah.gcc.virtualrover.MainGame.SCALE;

public class EcoDisasterScreen extends AbstractStandardScreen implements ChallengeScreen {

    private static Vector3 UP = Vector3.Y;

    private PerspectiveCamera attachedCamera;
    private FrameBuffer attachedCameraFrameBuffer;
    // private TextureRegion attachedCameraTextureRegion;
    private Texture attachedCameraTexture;
    // private TextureData attachedCameraTextureData;
    private Pixmap attachedCameraPixmap;
    private Vector3 calculatedCameraPosition = new Vector3();
    private Quaternion calculatedCameraOrientation = new Quaternion();

    private PerspectiveCamera camera;
    private Vector3 attachedCameraDirection = new Vector3();
    private CameraControllersManager cameraControllersManager;
    private InputMultiplexer cameraInputMultiplexer;

    private boolean renderBackground = false;
    private boolean makeSnapshot = false;
    private byte[] snapshotData = null;

    public EcoDisasterScreen(MainGame game,
            PlatformSpecific platformSpecific,
            AssetManager assetManager,
            SoundManager soundManager,
            ModelFactory modelFactory,
            ServerCommunicationAdapter serverCommunicationAdapter,
            Console console) {
        super(game, platformSpecific, assetManager, soundManager, modelFactory, serverCommunicationAdapter, console);

        setBackground(new PerlinNoiseBackground());

        EcoDisasterArena challenge = new EcoDisasterArena(modelFactory);
        challenge.init();

        setChallenge(challenge);

        camera = new PerspectiveCamera(45, 800, 480);
        camera.position.set(300f * SCALE, 480f * SCALE, 300f * SCALE);
        camera.lookAt(0f, 0f, 0f);
        camera.near = 0.02f;
        camera.far = 1000f;

        attachedCamera = new PerspectiveCamera(45, 320, 256);
        attachedCamera.position.set(300f * SCALE, 480f * SCALE, 300f * SCALE);
        attachedCamera.lookAt(0f, 0f, 0f);
        attachedCamera.near = 0.02f;
        attachedCamera.far = 1000f;
        attachedCamera.up.set(UP);
        attachedCamera.fieldOfView = 45f;

        attachedCameraFrameBuffer = new FrameBuffer(Format.RGBA8888, 320, 256, true);
        attachedCameraPixmap = new Pixmap(320,  256,  Format.RGBA8888);
        // attachedCameraTextureData = new PixmapTextureData(attachedCameraPixmap, Format.RGBA8888, false, false);
        //attachedCameraTextureRegion = new TextureRegion(attachedCameraFrameBuffer.getColorBufferTexture());
        attachedCameraTexture = attachedCameraFrameBuffer.getColorBufferTexture();
        // attachedCameraTextureData = attachedCameraTexture.getTextureData();

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
        attachedCameraFrameBuffer.dispose();

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
        Gdx.gl20.glEnable(GL20.GL_DEPTH_TEST);
//        Gdx.gl20.glDepthFunc(GL20.GL_LEQUAL);
//        Gdx.gl.glEnable(GL20.GL_POLYGON_OFFSET_FILL);
//        Gdx.gl20.glPolygonOffset(1.0f, 1.0f);

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

        CameraAttachment cameraAttachment = serverCommunicationAdapter.getCameraAttachment();
        if (cameraAttachment != null) {
            Rover rover = serverCommunicationAdapter.getEngine().getGame().getCurrentGameState().get(cameraAttachment.getParentId());
            if (rover != null) {
                renderAttachedCamera(rover, cameraAttachment);
            } else {
                // TODO do something
            }
        }

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
            spriteBatch.draw(attachedCameraTexture, 0, 0, 320, 256, 0, 0, 320, 256, false, true);
        }
        spriteBatch.end();

        drawStandardMessages();

        if (console != null) {
            console.render();
        }

        if (serverCommunicationAdapter.isMakeCameraSnapshot()) {
            // snapshot();
            // serverCommunicationAdapter.makeCameraSnapshot(attachedCameraPixmap.getPixels());
            serverCommunicationAdapter.makeCameraSnapshot(snapshotData);
        }
    }

    private void renderAttachedCamera(Rover rover, CameraAttachment cameraAttachment) {
        Vector3 cameraPosition = cameraAttachment.getPosition();
        Vector3 roverPosition = rover.getPosition();
        calculatedCameraPosition.set(cameraPosition);

        calculatedCameraOrientation.set(rover.getOrientation());
        calculatedCameraOrientation.mul(cameraAttachment.getOrientation());

        rover.getOrientation().transform(calculatedCameraPosition);
        calculatedCameraPosition.add(roverPosition);

        attachedCameraDirection.set(Vector3.X);
        calculatedCameraOrientation.transform(attachedCameraDirection);

        attachedCamera.position.x = calculatedCameraPosition.x * SCALE;
        attachedCamera.position.y = cameraPosition.z * SCALE;
        attachedCamera.position.z = -calculatedCameraPosition.y * SCALE;
        attachedCamera.direction.x = attachedCameraDirection.x;
        attachedCamera.direction.y = attachedCameraDirection.z;
        attachedCamera.direction.z = -attachedCameraDirection.y;
        attachedCamera.update();

        attachedCameraFrameBuffer.begin();
        Gdx.gl.glClearColor(0.6f, 0.75f, 1f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        Gdx.gl20.glEnable(GL20.GL_DEPTH_TEST);
//        Gdx.gl20.glDepthFunc(GL20.GL_LEQUAL);
//        Gdx.gl.glEnable(GL20.GL_POLYGON_OFFSET_FILL);
//        Gdx.gl20.glPolygonOffset(1.0f, 1.0f);

        if (renderBackground) {
            background.render(attachedCamera, batch, environment);
        }

        batch.begin(attachedCamera);

        challenge.render(batch, environment, attachedCameraFrameBuffer, serverCommunicationAdapter.getVisibleObjects());

        batch.end();

        if (serverCommunicationAdapter.isMakeCameraSnapshot()) {
            snapshotData = ScreenUtils.getFrameBufferPixels(0, 0, 320, 256, true);
        }
        attachedCameraFrameBuffer.end();

        //        FileHandle fh = new FileHandle(output);
//        Pixmap pixmap = ScreenUtils.getFrameBufferPixmap(0, 0, screenWidth, screenHeight);
//        PixmapIO.writePNG(fh, pixmap);
//        pixmap.dispose();
    }

//    private void snapshot() {
//        if (!attachedCameraTextureData.isPrepared()) {
//            attachedCameraTextureData.prepare();
//        }
//        Pixmap pixmap = attachedCameraTextureData.consumePixmap();
//        try {
//            attachedCameraPixmap.drawPixmap(pixmap, 0, 0);
//        } finally {
//            attachedCameraTextureData.disposePixmap();
//        }
//    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);

        // TODO add handling of current main camera
    }

    @Override
    public boolean keyDown(int keycode) {
        super.keyDown(keycode);
        if (keycode == Input.Keys.SPACE && serverCommunicationAdapter.isLocal() && !serverCommunicationAdapter.hasPlayerOne() && !serverCommunicationAdapter.hasPlayerTwo()) {
            GCCGame game = serverCommunicationAdapter.getEngine().getGame();

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
