package org.ah.piwars.virtualrover.game.rovers;

import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import org.ah.themvsus.engine.common.game.GameObjectFactory;

import static org.ah.piwars.virtualrover.engine.utils.CollisionUtils.asShape2DList;
import static org.ah.piwars.virtualrover.engine.utils.CollisionUtils.polygonFromBox;

public class MacFeegleRover extends Rover {

    public MacFeegleRover(GameObjectFactory factory, int id) {
        super(factory, id, RoverType.MacFeegle);

        this.stereoCamera = true;

        this.polygons = asShape2DList(polygonFromBox(-100f,  -75f, 100f,  75f));
        this.attachmentPosition = new Vector2(100f, 0);
        this.cameraPosition = new Vector3(100f, 0f, 320f);
        this.cameraOrientation = new Quaternion();
        this.cameraOrientation.setEulerAngles(45f, 0f, 0f); // positive yaw is down
        this.cameraAngle = 45f;
    }
}
