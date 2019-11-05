package org.ah.gcc.virtualrover.camera;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector3;

import org.ah.gcc.virtualrover.world.PlayerModel;

import java.util.List;

import static org.ah.gcc.virtualrover.MainGame.SCALE;

public class CinematicCameraController extends InputAdapter implements ActiveCamera {

    private static Vector3 UP = new Vector3(0, 1, 0);
    private PerspectiveCamera camera;
    private final List<PlayerModel> players;

    private Vector3 pos1 = new Vector3();
    private Vector3 pos2 = new Vector3();
    private Vector3 midpoint = new Vector3();

    public CinematicCameraController(PerspectiveCamera camera, List<PlayerModel> players) {
        this.camera = camera;
        this.players = players;
    }

    @Override
    public void update() {
        float distanceBetweeRovers = 0f;
        if (players.size() > 1 && players.get(0).rover != null && players.get(1).rover != null) {
            pos1 = players.get(0).rover.getTransform().getTranslation(pos1);
            pos2 = players.get(1).rover.getTransform().getTranslation(pos2);
            distanceBetweeRovers = pos1.dst(pos2);
            midpoint.set(pos1).add(pos2).scl(0.5f);

            pos1.sub(pos2);
            pos1.rotate(UP, 90);
            float a = (float)Math.atan2(pos1.x, pos1.z);
            // System.out.println(String.format("(%7.3f, %7.3f, %7.3f) @ %7.3f", pos1.x, pos1.y, pos1.z, a));
            float d = distanceBetweeRovers * 700f * SCALE;
            if (d < 800f * SCALE) {
                d = 800f * SCALE;
            }
            pos2.set(d * (float)Math.sin(a) + midpoint.x, 700f * SCALE, d * (float)Math.cos(a) + midpoint.z);
            pos2.lerp(camera.position, Interpolation.exp5.apply(0.9f));
        } else {
            pos1.set(0f, 0f, 0f);
        }
        camera.lookAt(midpoint);
        camera.position.set(pos2);
        camera.up.set(UP);
        camera.fieldOfView = 45;
    }
}
