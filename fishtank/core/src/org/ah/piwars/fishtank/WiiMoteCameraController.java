package org.ah.piwars.fishtank;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
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

    private float dx = 0f;
    private float dy = 0f;

    private float factor;

    public WiiMoteCameraController(PerspectiveCamera camera, CameraInputController cameraController, float factor) {
        this.camera = camera;
        this.cameraController = cameraController;
        this.factor = factor;
        cameraController.autoUpdate = false;
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
        if (keycode == Keys.UP) {
            inputY = inputY + KEYBOARD_STEP;
            processed = true;
        } else if (keycode == Keys.DOWN) {
            inputY = inputY - KEYBOARD_STEP;
            processed = true;
        } else if (keycode == Keys.LEFT) {
            inputX = inputX - KEYBOARD_STEP;
            processed = true;
        } else if (keycode == Keys.RIGHT) {
            inputX = inputX + KEYBOARD_STEP;
            processed = true;
        } else if (keycode == Keys.R) {
            inputX = 0f;
            inputY = 0f;
        }
        if (keycode == Keys.A) { dx = -KEYBOARD_STEP / 10f; }
        if (keycode == Keys.D) { dx = KEYBOARD_STEP / 10f; }
        if (keycode == Keys.S) { dy = KEYBOARD_STEP / 10f; }
        if (keycode == Keys.W) { dy = -KEYBOARD_STEP / 10f; }
        processed = processed | cameraController.keyDown(keycode);
        if (processed) {
            updateCamera();
        }
        return processed;
    }

    @Override
    public boolean keyUp(int keycode) {
        if (keycode == Keys.A || keycode == Keys.D) {
            dx = 0f;
        }
        if (keycode == Keys.S || keycode == Keys.W) {
            dy = 0f;
        }
        boolean processed = cameraController.keyUp(keycode);
        if (processed) { updateCamera(); }
        return processed;
    }

    @Override
    public boolean keyTyped(char character) {
        boolean processed = cameraController.keyTyped(character);
        if (processed) { updateCamera(); }
        return processed;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        boolean processed = cameraController.touchDown(screenX, screenY, pointer, button);
        if (processed) { updateCameraAfterController(); }
        return processed;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        boolean processed = cameraController.touchUp(screenX, screenY, pointer, button);
        if (processed) { updateCameraAfterController(); }
        return processed;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        boolean processed = cameraController.touchDragged(screenX, screenY, pointer);
        if (processed) { updateCameraAfterController(); }
        return processed;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        boolean processed = cameraController.mouseMoved(screenX, screenY);
        if (processed) { updateCamera(); }
        return processed;
    }

    @Override
    public boolean scrolled(int amount) {
        boolean processed = cameraController.scrolled(amount);
        if (processed) { updateCamera(); }
        return processed;
    }

    public void update() {
        if (dx != 0 || dy != 0) {
            inputX += dx;
            inputY += dy;
            updateCamera();
        }
    }

    public void updateCameraAfterController() {

        float width = Gdx.graphics.getWidth();
        float height = Gdx.graphics.getHeight();

        float aspectRatio = height / width;

        //        float f = camera.near * factor * 0.01f;
        float f = FishtankScreen.WORLD_SCALE2 * factor;

        inputX = camera.position.x * f;
        inputY = camera.position.y * f / aspectRatio;

//        tmp.set(camera.position);
//        tmp.x = -tmp.x;
//        tmp.z = -tmp.z;
//
//        camera.lookAt(tmp);

        updateCamera();
    }

    public void updateCamera() {
        boolean updateFrustum = true;

        float width = Gdx.graphics.getWidth();
        float height = Gdx.graphics.getHeight();

        float aspectRatio = height / width;

//        float xp = ((x / width) - 0.5f) * 0.2f;
//        float yp = (0.5f - (y / height)) * 0.2f;

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
