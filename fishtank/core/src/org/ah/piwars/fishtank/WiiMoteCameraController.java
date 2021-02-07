package org.ah.piwars.fishtank;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;

public class WiiMoteCameraController extends InputAdapter {

    private final Vector3 tmp = new Vector3();

    public static final float KEYBOARD_STEP = 0.001f;

    private PerspectiveCamera camera;

    private float factor;
    private int button;

    private Vector3 input = new Vector3();

    public float translateUnits = 10f;

    private float startX;
    private float startY;
    private final Vector3 tmpV1 = new Vector3();
    private final Vector3 tmpV2 = new Vector3();

    public WiiMoteCameraController(PerspectiveCamera camera, float factor) {
        this.camera = camera;

        this.factor = factor;

        input.set(camera.position);
    }

    public void setCamPosition(float x, float y) {
        this.camera.position.x = x;
        this.camera.position.y = y;
    }

    public Vector3 getInput() { return input; }

    @Override
    public boolean keyDown(int keycode) {
        boolean processed = false;
        return processed;
    }

    @Override
    public boolean keyUp(int keycode) {
        boolean processed = false;
        return processed;
    }

    @Override
    public boolean keyTyped(char character) {
        boolean processed = false;
        return processed;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        boolean processed = false;
        this.button = button;
        if (button == Buttons.LEFT) {
            startX = screenX;
            startY = screenY;
            this.button = button;
        }
        return processed;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        boolean processed = false;
        if (button == Buttons.LEFT) {
            if (button == this.button) { this.button = -1; }
        }
        this.button = 0;
        return processed;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        boolean processed = false;
        if (this.button == Buttons.LEFT) {

            boolean result = super.touchDragged(screenX, screenY, pointer);
            if (result || this.button < 0) return result;
            final float deltaX = (screenX - startX) / Gdx.graphics.getWidth();
            final float deltaY = (startY - screenY) / Gdx.graphics.getHeight();
            startX = screenX;
            startY = screenY;

            // camera.translate(tmpV1.set(camera.direction).crs(camera.up).nor().scl(-deltaX * translateUnits));
            // camera.translate(tmpV2.set(camera.up).scl(-deltaY * translateUnits));
            input.add(tmpV1.set(camera.direction).crs(camera.up).nor().scl(-deltaX * translateUnits));
            input.add(tmpV2.set(camera.up).scl(-deltaY * translateUnits));
//            if (translateTarget) target.add(tmpV1).add(tmpV2);
//            inputX = input.x;
//            inputY = input.y;
        }
        return processed;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        boolean processed = false;
        return processed;
    }

    @Override
    public boolean scrolled(int amount) {
        boolean processed = false;
        return processed;
    }

    public void updateCamera() {
        boolean updateFrustum = true;

        float width = Gdx.graphics.getWidth();
        float height = Gdx.graphics.getHeight();
        float aspectRatio = height / width;

        float f = FishtankScreen.WORLD_SCALE2 * factor;

        float xOffset = camera.position.x * f;
        float yOffset = camera.position.y * f / aspectRatio;

        f = camera.near * factor;

//        camera.position.x = camX;
//        camera.position.y = camY;

        camera.projection.setToProjection(-f - xOffset, f - xOffset, -f * aspectRatio - yOffset, f * aspectRatio - yOffset, 0.1f, 300f);

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
