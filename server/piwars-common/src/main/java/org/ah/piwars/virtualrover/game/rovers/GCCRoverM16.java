package org.ah.piwars.virtualrover.game.rovers;

import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import org.ah.piwars.virtualrover.engine.utils.CollisionUtils;
import org.ah.themvsus.engine.common.game.GameObjectFactory;

import static java.util.Arrays.asList;

public class GCCRoverM16 extends Rover {

    public GCCRoverM16(GameObjectFactory factory, int id) {
        super(factory, id, RoverType.GCCM16);

        this.polygons = asList(CollisionUtils.polygonFromBox(-80f,  -55f, 80f,  55f));
        this.attachmentPosition = new Vector2(80f, 0);
        this.cameraPosition = new Vector3(75f, 0f, 20f);
        this.cameraOrientation = new Quaternion();
        this.cameraOrientation.setEulerAngles(10f, 0f, 0f); // positive yaw is down
        this.cameraAngle = 45f;
    }
}
