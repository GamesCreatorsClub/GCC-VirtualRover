package org.ah.piwars.virtualrover.camera;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector3;

import org.ah.piwars.virtualrover.ServerCommunicationAdapter;
import org.ah.piwars.virtualrover.world.PlayerModelLink;

import static org.ah.piwars.virtualrover.MainGame.SCALE;

public class CinematicCameraController extends InputAdapter implements ActiveCamera {

    private static Vector3 UP = Vector3.Y;
    private PerspectiveCamera camera;

    private Vector3 pos1 = new Vector3();
    private Vector3 pos2 = new Vector3();
    private Vector3 midpoint = new Vector3();
    private float cameraHeight = 700f;
    private float cameraRadius = 1500f;

    private ServerCommunicationAdapter serverCommunicationAdapter;
    private boolean flipXPosition;

    public CinematicCameraController(PerspectiveCamera camera, ServerCommunicationAdapter serverCommunicationAdapter) {
        this.camera = camera;
        this.serverCommunicationAdapter = serverCommunicationAdapter;
    }

    public float getCameraHeight() {
        return cameraHeight;
    }

    public void setCameraHeight(float cameraHeight) {
        this.cameraHeight = cameraHeight;
    }

    public float getCameraRadius() {
        return cameraRadius;
    }

    public void setCameraRadius(float cameraRadius) {
        this.cameraRadius = cameraRadius;
    }

    public boolean isFlipXPosition() {
        return flipXPosition;
    }

    public void setFlipXPosition(boolean flipXPosition) {
        this.flipXPosition = flipXPosition;
    }

    @Override
    public void update() {
        float distanceBetweeRovers = 0f;
        PlayerModelLink player1 = serverCommunicationAdapter.getPlayerOneVisualObject();
        PlayerModelLink player2 = serverCommunicationAdapter.getPlayerTwoVisualObject();
        if (player1 != null && player2 != null) {
            player1.getRoverTransform().getTranslation(pos1);
            player2.getRoverTransform().getTranslation(pos2);
            distanceBetweeRovers = pos1.dst(pos2);
            midpoint.set(pos1).add(pos2).scl(0.5f);

            pos1.sub(pos2);
            pos1.rotate(UP, 90);
            float a = (float)Math.atan2(pos1.x, pos1.z);

            float d = distanceBetweeRovers * 700f * SCALE;
            if (d < 800f * SCALE) {
                d = 800f * SCALE;
            }
            pos2.set(d * (float)Math.sin(a) + midpoint.x, 700f * SCALE, d * (float)Math.cos(a) + midpoint.z);
            pos2.lerp(camera.position, Interpolation.exp5.apply(0.9f));
        } else {
            if (player1 != null) {
                player1.getRoverTransform().getTranslation(midpoint);
            } else {
                midpoint.set(0f, 0f, 0f);
            }
            pos2.set(cameraRadius * SCALE, cameraHeight * SCALE, (flipXPosition ? -1f : 1f) * cameraRadius * SCALE);
        }
        camera.lookAt(midpoint);
        camera.position.set(pos2);
        camera.up.set(UP);
        camera.fieldOfView = 45;
    }
}
