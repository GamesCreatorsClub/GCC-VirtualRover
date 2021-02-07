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

    public WiiMoteCameraController(PerspectiveCamera camera, CameraInputController cameraController, float factor) {
        this.camera = camera;
        this.cameraController = cameraController;
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

        processed = processed | cameraController.keyDown(keycode);
        if (processed) {
            updateCamera();
        }
        return processed;
    }

    @Override
    public boolean keyUp(int keycode) {
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
        this.button = button;
        if (button == Buttons.RIGHT) {
            boolean processed = cameraController.touchDown(screenX, screenY, pointer, button);
            if (processed) { updateCameraAfterController(); }
            return processed;
        }
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (button == Buttons.RIGHT) {
            boolean processed = cameraController.touchUp(screenX, screenY, pointer, button);
            if (processed) { updateCameraAfterController(); }
            return processed;
        }
        this.button = 0;
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        if (this.button == Buttons.RIGHT) {
            boolean processed = cameraController.touchDragged(screenX, screenY, pointer);
            if (processed) { updateCameraAfterController(); }
            return processed;
        }
        return false;
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
    }

    public void updateCameraAfterController() {

        float width = Gdx.graphics.getWidth();
        float height = Gdx.graphics.getHeight();

        float aspectRatio = height / width;

        //        float f = camera.near * factor * 0.01f;
        float f = FishtankScreen.WORLD_SCALE2 * factor;

        inputX = camera.position.x * f;
        inputY = camera.position.y * f / aspectRatio;

//        camera.direction.set(cameraDirection);

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
