package org.ah.piwars.virtualrover.camera;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;

import org.ah.piwars.virtualrover.world.PlayerModelLink;

import java.util.List;

import static org.ah.piwars.virtualrover.MainGame.SCALE;

public class CinematicCameraController2 extends InputAdapter implements ActiveCamera {

    private PerspectiveCamera camera;
    private final List<PlayerModelLink> players;

    private Vector3 pos1 = new Vector3();

    public CinematicCameraController2(PerspectiveCamera camera, List<PlayerModelLink> players) {
        this.camera = camera;
        this.players = players;
    }

    @Override
    public void update() {
        if (players.size() > 0 && players.get(0).getRoverTransform() != null) {
            pos1 = players.get(0).getRoverTransform().getTranslation(pos1);

            Quaternion q = new Quaternion();
            q = players.get(0).getRoverTransform().getRotation(q);

            pos1.y = 1000f * SCALE;
            camera.position.set(pos1);

            camera.direction.set(new Vector3(0.1f, 0, 0));
            camera.direction.rotate(new Vector3(0, 1, 0), 180 + q.getAngleAround(new Vector3(0, 1, 0)));
        } else {
            pos1.set(0f, 0f, 0f);
            camera.position.set(pos1);
        }

        camera.up.set(new Vector3(0, 1, 0));

        camera.fieldOfView = 120f;
    }
}
