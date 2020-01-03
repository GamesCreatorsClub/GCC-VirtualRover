package org.ah.piwars.virtualrover.game.rovers;

import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import org.ah.piwars.virtualrover.engine.utils.CollisionUtils;
import org.ah.themvsus.engine.common.game.GameObjectFactory;

import static java.util.Arrays.asList;

public class CBISRover extends Rover {

    public CBISRover(GameObjectFactory factory, int id) {
        super(factory, id, RoverType.CBIS);

        this.polygons = asList(CollisionUtils.polygonFromBox(-100f,  -75f, 100f,  75f));
        this.attachmentPosition = new Vector2(100f, 0);
        this.cameraPosition = new Vector3(100f, 0f, 30f);
        this.cameraOrientation = new Quaternion();
        this.cameraAngle = 45f;
    }
}
