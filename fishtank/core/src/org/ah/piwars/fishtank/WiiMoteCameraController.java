package org.ah.piwars.fishtank;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;

public class WiiMoteCameraController extends InputAdapter {

    private final Vector3 tmp = new Vector3();

    public static final float KEYBOARD_STEP = 0.001f;

    private PerspectiveCamera camera;
    private float inputX;
    private float inputY;

    private float camX;
    private float camY;

    private CameraInputController cameraController;

    private float factor;
    private int button;

    private Vector3 cameraDirection = new Vector3();

    private float startX, startY;
    private final Vector3 tmpV1 = new Vector3();
    private final Vector3 tmpV2 = new Vector3();

    public WiiMoteCameraController(PerspectiveCamera camera, float factor) {
        this.camera = camera;
        this.cameraController = new CameraInputController(camera);
;
        this.factor = factor;
        cameraController.autoUpdate = false;
        cameraDirection.set(camera.direction);
    }

    public void setCamPosition(float x, float y) {
        this.camX = x;
        this.camY = y;
    }

    public float getInputX() { return inputX; }
    public float getInputY() { return inputY; }

    @Override
    public boolean keyDown(int keycode) {
        boolean processed = false;
//        processed = processed | cameraController.keyDown(keycode);
//        if (processed) {
//            updateCamera();
//        }
        return processed;
    }

    @Override
    public boolean keyUp(int keycode) {
        boolean processed = false;
//        processed = processed | cameraController.keyUp(keycode);
//        if (processed) { updateCamera(); }
        return processed;
    }

    @Override
    public boolean keyTyped(char character) {
        boolean processed = false;
//        processed = processed | cameraController.keyTyped(character);
//        if (processed) { updateCamera(); }
        return processed;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        boolean processed = false;
        this.button = button;
        if (button == Buttons.RIGHT) {
            processed = processed | cameraController.touchDown(screenX, screenY, pointer, button);
            if (processed) { updateCameraAfterController(); }
        }
        return processed;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        boolean processed = false;
        if (button == Buttons.RIGHT) {
            processed = processed | cameraController.touchUp(screenX, screenY, pointer, button);
            if (processed) { updateCameraAfterController(); }
        }
        this.button = 0;
        return processed;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        boolean processed = false;
        if (this.button == Buttons.RIGHT) {
            processed = processed | cameraController.touchDragged(screenX, screenY, pointer);
            if (processed) { updateCameraAfterController(); }
        }
        return processed;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        boolean processed = false;
//        processed = processed | cameraController.mouseMoved(screenX, screenY);
//        if (processed) { updateCamera(); }
        return processed;
    }

    @Override
    public boolean scrolled(int amount) {
        boolean processed = false;
//        processed = processed | cameraController.scrolled(amount);
//        if (processed) { updateCamera(); }
        return processed;
    }

    public void update() {
    }

    public void updateCameraAfterController() {

        float width = Gdx.graphics.getWidth();
        float height = Gdx.graphics.getHeight();

        float aspectRatio = height / width;

        float f = FishtankScreen.WORLD_SCALE2 * factor;

        inputX = camera.position.x * f;
        inputY = camera.position.y * f / aspectRatio;

        updateCamera();
    }

    public void updateCamera() {
        boolean updateFrustum = true;

        float width = Gdx.graphics.getWidth();
        float height = Gdx.graphics.getHeight();

        float aspectRatio = height / width;

        float f = camera.near * factor;

        camera.projection.setToProjection(-f - camX, f - camX, -f * aspectRatio - camY, f * aspectRatio - camY, 0.1f, 300f);

        camera.view.setToLookAt(camera.position, tmp.set(camera.position).add(camera.direction), camera.up);
        camera.combined.set(camera.projection);
        Matrix4.mul(camera.combined.val, camera.view.val);

        if (updateFrustum) {
            camera.invProjectionView.set(camera.combined);
            Matrix4.inv(camera.invProjectionView.val);
            camera.frustum.update(camera.invProjectionView);
        }
    }
}
