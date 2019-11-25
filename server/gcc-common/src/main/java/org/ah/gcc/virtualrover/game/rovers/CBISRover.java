package org.ah.gcc.virtualrover.game.rovers;

import com.badlogic.gdx.math.Vector2;

import org.ah.gcc.virtualrover.engine.utils.CollisionUtils;
import org.ah.gcc.virtualrover.game.Rover;
import org.ah.themvsus.engine.common.game.GameObjectFactory;

import static java.util.Arrays.asList;

public class CBISRover extends Rover {

    public CBISRover(GameObjectFactory factory, int id) {
        super(factory, id, RoverType.CBIS);

        this.polygons = asList(CollisionUtils.polygonFromBox(-100f,  -75f, 100f,  75f));
        this.attachmentPosition = new Vector2(100f, 0);
    }
}