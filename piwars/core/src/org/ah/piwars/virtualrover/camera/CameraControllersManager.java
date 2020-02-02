package org.ah.piwars.virtualrover.camera;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;

import java.util.ArrayList;
import java.util.List;

public class CameraControllersManager extends InputAdapter {

    private List<InputProcessor> cameraControllers = new ArrayList<InputProcessor>();
    private List<String> cameraControllerNames = new ArrayList<String>();
    private InputProcessor currentCameraController;
    private int currentCameraControllerIndex = 0;
    private boolean leftShift = false;
    private boolean rightShift = false;

    public CameraControllersManager() {

    }

    public void addCameraController(String name, InputProcessor cameraController) {
        cameraControllers.add(cameraController);
        cameraControllerNames.add(name);
        if (cameraControllers.size() == 1) {
            currentCameraController = cameraController;
        }
    }

    public void update() {
        if (currentCameraController != null && currentCameraController instanceof ActiveCamera) {
            ((ActiveCamera)currentCameraController).update();
        }
    }

    public void next() {
        if (cameraControllers.size() > 1) {
            currentCameraControllerIndex++;
            if (currentCameraControllerIndex >= cameraControllers.size()) {
                currentCameraControllerIndex = 0;
            }
        }
        currentCameraController = cameraControllers.get(currentCameraControllerIndex);
    }

    public void previous() {
        if (cameraControllers.size() > 1) {
            currentCameraControllerIndex--;
            if (currentCameraControllerIndex < 0) {
                currentCameraControllerIndex = cameraControllers.size() -1;
            }
        }
        currentCameraController = cameraControllers.get(currentCameraControllerIndex);
    }

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.F3) {
            if (leftShift || rightShift) {
                previous();
            } else {
                next();
            }
        } else if (keycode == Input.Keys.SHIFT_LEFT) {
            leftShift = true;
        } else if (keycode == Input.Keys.SHIFT_RIGHT) {
            rightShift = true;
        }
        if (currentCameraController != null) { return currentCameraController.keyDown(keycode); }
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        if (keycode == Input.Keys.SHIFT_LEFT) {
            leftShift = false;
        } else if (keycode == Input.Keys.SHIFT_RIGHT) {
            rightShift = false;
        }
        if (currentCameraController != null) { return currentCameraController.keyUp(keycode); }
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        if (currentCameraController != null) { return currentCameraController.keyTyped(character); }
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (currentCameraController != null) { return currentCameraController.touchDown(screenX, screenY, pointer, button); }
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (currentCameraController != null) { return currentCameraController.touchUp(screenX, screenY, pointer, button); }
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        if (currentCameraController != null) { return currentCameraController.touchDragged(screenX, screenY, pointer); }
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        if (currentCameraController != null) { return currentCameraController.mouseMoved(screenX, screenY); }
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        if (currentCameraController != null) { return currentCameraController.scrolled(amount); }
        return false;
    }
}
