package org.ah.piwars.virtualrover.challenges;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;

import org.ah.piwars.virtualrover.MainGame;
import org.ah.piwars.virtualrover.PlatformSpecific;
import org.ah.piwars.virtualrover.ServerCommunicationAdapter;
import org.ah.piwars.virtualrover.backgrounds.PerlinNoiseBackground;
import org.ah.piwars.virtualrover.game.GameMessageObject;
import org.ah.piwars.virtualrover.game.attachments.CameraAttachment;
import org.ah.piwars.virtualrover.game.rovers.Rover;
import org.ah.piwars.virtualrover.screens.RenderingContext;
import org.ah.piwars.virtualrover.utils.SoundManager;
import org.ah.piwars.virtualrover.view.Console;

import static org.ah.piwars.virtualrover.MainGame.SCALE;

public abstract class AbstractCameraChallengeScreen extends AbstractChallengeScreen implements ChallengeScreen {

    protected enum CameraType {
        NONE, FIRST_PERSON, THIRD_PERSON
    }

    protected enum CameraCombination {
        THIRD_PERSON_AND_FIRST_PERSON(CameraType.THIRD_PERSON, CameraType.FIRST_PERSON, true),
        FIRST_PERSON_ONLY(CameraType.FIRST_PERSON, CameraType.NONE, true),
        FIRST_PERSON_AND_THIRD_PERSON(CameraType.FIRST_PERSON, CameraType.THIRD_PERSON, true),
        THIRD_PERSION_ONLY(CameraType.THIRD_PERSON, CameraType.NONE, true),
        THIRD_PERSION_ONLY_NON_CYCLABLE(CameraType.THIRD_PERSON, CameraType.NONE, false),
        SIMULATION(CameraType.THIRD_PERSON, CameraType.FIRST_PERSON, false);

        private CameraType main;
        private CameraType small;
        private boolean cyclaable;

        CameraCombination(CameraType main, CameraType small, boolean cyclaable) {
            this.main = main;
            this.small = small;
            this.cyclaable = cyclaable;
        }

        public CameraType getMain() { return main; }
        public CameraType getSmall() { return small; }
        public boolean isCycleable() { return cyclaable; }
    }

    private static final long CAMERA_CHANGE_DELAY = 2000;

    protected static CameraCombination[] CAMERA_COMBINATIONS = CameraCombination.values();

    protected PerspectiveCamera attachedCamera;
    protected FrameBuffer attachedCameraFrameBuffer;
    protected Texture attachedCameraTexture;
    protected Vector3 calculatedCameraPosition = new Vector3();
    protected Quaternion calculatedCameraOrientation = new Quaternion();
    protected Vector3 attachedCameraDirection = new Vector3();

    protected byte[] snapshotData = null;
    protected CameraAttachment cameraAttachment;

    protected FrameBuffer thirdPersonCameraFrameBuffer;
    protected Texture thirdPersonCameraTexture;

    protected CameraCombination cameraCombination = CameraCombination.THIRD_PERSION_ONLY;
    protected long gameStartedTimestamp = 0;
    protected boolean firstTimeout = false;
    protected boolean inGame;
    protected boolean smallCameraEnabled = true;

    protected AbstractCameraChallengeScreen(MainGame game,
            PlatformSpecific platformSpecific,
            AssetManager assetManager,
            SoundManager soundManager,
            ServerCommunicationAdapter serverCommunicationAdapter,
            Console console) {

        super(game, platformSpecific, assetManager, soundManager,
                serverCommunicationAdapter, console);

        attachedCamera = new PerspectiveCamera(45, 320, 256);
        attachedCamera.position.set(300f * SCALE, 480f * SCALE, 300f * SCALE);
        attachedCamera.lookAt(0f, 0f, 0f);
        attachedCamera.near = 0.02f;
        attachedCamera.far = 1000f;
        attachedCamera.up.set(UP);
        attachedCamera.fieldOfView = 45f;

        attachedCameraFrameBuffer = new FrameBuffer(Format.RGBA8888, 320, 256, true);
        attachedCameraTexture = attachedCameraFrameBuffer.getColorBufferTexture();

        thirdPersonCameraFrameBuffer = new FrameBuffer(Format.RGBA8888, 320, 256, true);
        thirdPersonCameraTexture = thirdPersonCameraFrameBuffer.getColorBufferTexture();

        setBackground(new PerlinNoiseBackground());
    }

    @Override
    public void dispose() {
        super.dispose();
        attachedCameraFrameBuffer.dispose();
    }

    protected void processCameraAttachemnt() {
        cameraAttachment = serverCommunicationAdapter.getCameraAttachment();
        if (cameraAttachment != null) {
            Rover rover = serverCommunicationAdapter.getEngine().getGame().getCurrentGameState().get(cameraAttachment.getParentId());
            if (rover != null) {
                updateFirstPersonCamera(rover, cameraAttachment);
            }
        }
    }

    private void updateFirstPersonCamera(Rover rover, CameraAttachment cameraAttachment) {
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
    }

    protected void renderFirstPersonSmallCamera() {
        thirdPersonCameraFrameBuffer.begin();
        Gdx.gl.glClearColor(0.6f, 0.75f, 1f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);

        if (renderingContext.renderBackground) {
            background.render(camera, modelBatch, environment);
        }

        modelBatch.begin(camera);

        renderingContext.frameBuffer = thirdPersonCameraFrameBuffer;

        renderChallenge(renderingContext);

        modelBatch.end();

        if (serverCommunicationAdapter.isMakeCameraSnapshot()) {
            snapshotData = ScreenUtils.getFrameBufferPixels(0, 0, 320, 256, true);
        }
        thirdPersonCameraFrameBuffer.end();
    }

    private void renderAttachedCamera() {
        attachedCameraFrameBuffer.begin();
        Gdx.gl.glClearColor(0.6f, 0.75f, 1f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);

        if (renderingContext.renderBackground) {
            background.render(attachedCamera, modelBatch, environment);
        }

        modelBatch.begin(attachedCamera);

        renderingContext.frameBuffer = attachedCameraFrameBuffer;

        renderChallenge(renderingContext);

        modelBatch.end();

        if (serverCommunicationAdapter.isMakeCameraSnapshot()) {
            snapshotData = ScreenUtils.getFrameBufferPixels(0, 0, 320, 256, true);
        }
        attachedCameraFrameBuffer.end();
    }

    protected void showSmallCamera(Texture texture) {
        spriteBatch.draw(texture, 0, 0, 320, 256, 0, 0, 320, 256, false, true);
    }

    protected boolean isInGame() {
        GameMessageObject gameMessageObject = serverCommunicationAdapter.getGameMessageObject();
        return (gameMessageObject != null && gameMessageObject.isInGame());
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.6f, 0.75f, 1f, 1f);

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        Gdx.gl20.glEnable(GL20.GL_DEPTH_TEST);
//        Gdx.gl20.glDepthFunc(GL20.GL_LEQUAL);
//        Gdx.gl.glEnable(GL20.GL_POLYGON_OFFSET_FILL);
//        Gdx.gl20.glPolygonOffset(1.0f, 1.0f);

        if (!isSuspended()) {
            progressEngine();
        }

        if (serverCommunicationAdapter.isLocal()) {
            if (isSuspended()) {
                setMiddleMessage("Press ESC to leave", true);
            } else if (serverCommunicationAdapter.hasPlayerOne()) {
                moveRovers();
            } else if (serverCommunicationAdapter.isLocal()) {
                setMiddleMessage("Press space to begin", true);
            }
        } else {
            if (serverCommunicationAdapter.hasPlayerOne()) {
                moveRovers();
            }
        }

        boolean inGame = isInGame();
        if (!this.inGame && inGame) {
            gameStartedTimestamp = System.currentTimeMillis() + CAMERA_CHANGE_DELAY;
            firstTimeout = false;
            setupRequiredCameraCombination();
        }
        this.inGame = inGame;

        if (inGame && !platformSpecific.isSimulation()
                && !firstTimeout && System.currentTimeMillis() > gameStartedTimestamp
                && cameraCombination == CAMERA_COMBINATIONS[0]) {

            cameraCombination = CAMERA_COMBINATIONS[1];
            firstTimeout = true;
        }

        cameraControllersManager.update();
        camera.update();

        if (platformSpecific.isSimulation() || cameraCombination.getMain() == CameraType.FIRST_PERSON || cameraCombination.getSmall() == CameraType.FIRST_PERSON) {
            processCameraAttachemnt();
        }

        if (platformSpecific.isSimulation() || cameraCombination.getSmall() == CameraType.FIRST_PERSON) {
            renderAttachedCamera();
        } else if (cameraCombination.getSmall() == CameraType.THIRD_PERSON) {
            renderFirstPersonSmallCamera();
        }

        PerspectiveCamera mainCamera;
        if (cameraCombination.getMain() == CameraType.THIRD_PERSON) {
            mainCamera = camera;
        } else {
            mainCamera = attachedCamera;
        }

        if (renderingContext.renderBackground) {
            background.render(mainCamera, modelBatch, environment);
        }

        modelBatch.begin(mainCamera);

        renderingContext.frameBuffer = null;

        renderChallenge(renderingContext);

        modelBatch.end();

        spriteBatch.setProjectionMatrix(hudCamera.combined);
        spriteBatch.begin();
        drawScore();

        if (drawFPS) {
            drawFPS();
        }

        if (cameraCombination.getSmall() == CameraType.FIRST_PERSON) {
            showSmallCamera(attachedCameraTexture);
        } else if (cameraCombination.getSmall() == CameraType.THIRD_PERSON) {
            showSmallCamera(thirdPersonCameraTexture);
        }

        drawStandardMessages();
        spriteBatch.end();

        if (console != null) {
            console.render();
        }

        if (serverCommunicationAdapter.isMakeCameraSnapshot()) {
            serverCommunicationAdapter.makeCameraSnapshot(snapshotData);
        }
    }

    private void drawScore() {
        // TODO sort out score
        //        if (serverCommunicationAdapter.hasPlayerOne() && serverCommunicationAdapter.hasPlayerTwo()) {
        //            font.draw(spriteBatch, serverCommunicationAdapter.getPlayerOne().getScore() + " - " + serverCommunicationAdapter.getPlayerTwo().getScore(), Gdx.graphics.getWidth() - 120, Gdx.graphics.getHeight() - 40);
        //        }
    }

    @SuppressWarnings("deprecation")
    private void renderChallenge(RenderingContext renderingContext) {
        if (renderingContext.showShadows) {
            Camera cam = renderingContext.modelBatch.getCamera();
            renderingContext.modelBatch.end();
            if (renderingContext.frameBuffer != null) { renderingContext.frameBuffer.end(); }

            shadowLight.begin(Vector3.Zero, cam.direction);
            shadowBatch.begin(shadowLight.getCamera());

            renderingContext.modelBatch = shadowBatch;
            renderingContext.environment = environment;
            challenge.render(renderingContext, serverCommunicationAdapter.getVisibleObjects());

            shadowBatch.end();
            shadowLight.end();

            if (renderingContext.frameBuffer != null) {
                renderingContext.frameBuffer.begin();
                Gdx.gl20.glEnable(GL20.GL_DEPTH_TEST);
//                Gdx.gl20.glDepthFunc(GL20.GL_LEQUAL);
//                Gdx.gl.glEnable(GL20.GL_POLYGON_OFFSET_FILL);
//                Gdx.gl20.glPolygonOffset(1.0f, 1.0f);
            }
            modelBatch.begin(cam);

            renderingContext.modelBatch = modelBatch;
            renderingContext.environment = shadowEnvironment;
            challenge.render(renderingContext, serverCommunicationAdapter.getVisibleObjects());

            renderingContext.modelBatch.end();
            renderingContext.modelBatch.begin(cam);
        } else {
            renderingContext.modelBatch = modelBatch;
            renderingContext.environment = environment;
            challenge.render(renderingContext, serverCommunicationAdapter.getVisibleObjects());
        }
    }

    protected void setupRequiredCameraCombination() {
        cameraCombination = CAMERA_COMBINATIONS[0];
    }

    @Override
    public boolean keyDown(int keycode) {
        boolean res = super.keyDown(keycode);
        if (!res) {
            if (keycode == Input.Keys.C) {
                if (!platformSpecific.isSimulation()) {
                    int index = 0;
                    while (cameraCombination != CAMERA_COMBINATIONS[index]) {
                        index++;
                    }
                    index++;
                    if (index >= CAMERA_COMBINATIONS.length) {
                        index = 0;
                    }
                    if (!CAMERA_COMBINATIONS[index].isCycleable()) {
                        index++;
                        if (index >= CAMERA_COMBINATIONS.length) {
                            index = 0;
                        }
                    }
                    cameraCombination = CAMERA_COMBINATIONS[index];
                }
            }
        }

        if (keycode == Input.Keys.TAB) {
            camera.fieldOfView = 4f;
        }

        return res;
    }
}
