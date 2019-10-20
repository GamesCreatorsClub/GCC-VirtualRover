package org.ah.gcc.virtualrover.camera;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.Vector3;
import org.ah.gcc.virtualrover.world.Player;

import java.util.List;

import static org.ah.gcc.virtualrover.MainGame.SCALE;

public class CinematicCameraController extends InputAdapter implements ActiveCamera {

    private PerspectiveCamera camera;
    private final List<Player> players;

    private Vector3 pos1 = new Vector3();
    private Vector3 pos2 = new Vector3();
    private Vector3 midpoint = new Vector3();

    public CinematicCameraController(PerspectiveCamera camera, List<Player> players) {
        this.camera = camera;
        this.players = players;
    }

    @Override
    public void update() {
        float distanceBetweeRovers = 0f;
        float distance = 450f;
        if (players.size() > 1 && players.get(0).rover != null && players.get(1).rover != null) {
            pos1 = players.get(0).rover.getTransform().getTranslation(pos1);
            pos2 = players.get(1).rover.getTransform().getTranslation(pos2);
            distanceBetweeRovers = pos1.dst(pos2);
            midpoint.set(pos1).add(pos2).scl(0.5f);
            // distance = (-midpoint.dst(300f * SCALE, 0f, -300f * SCALE)) / SCALE;
            // distance = (distanceBetweeRovers) / SCALE;
            // distanceBetweeRovers = distanceBetweeRovers / SCALE;
            distance = 350f + (distanceBetweeRovers * 0.5f - midpoint.dst(1500f * SCALE, 0f, -1500f * SCALE) * 0.6f) / SCALE;
        } else {
            pos1.set(0f, 0f, 0f);
        }
        camera.lookAt(midpoint);
        camera.position.set(distance * SCALE, (1000f + distanceBetweeRovers * 200f) * SCALE, -distance * SCALE);
        camera.up.set(new Vector3(0, 1, 0));
        camera.fieldOfView = 45;
    }
}
