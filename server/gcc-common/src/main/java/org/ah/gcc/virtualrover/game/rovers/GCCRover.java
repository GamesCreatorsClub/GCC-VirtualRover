package org.ah.gcc.virtualrover.game.rovers;

import com.badlogic.gdx.math.Vector2;

import org.ah.gcc.virtualrover.engine.utils.CollisionUtils;
import org.ah.gcc.virtualrover.game.Rover;
import org.ah.themvsus.engine.common.game.GameObjectFactory;

import static java.util.Arrays.asList;

public class GCCRover extends Rover {

    public GCCRover(GameObjectFactory factory, int id) {
        super(factory, id, RoverType.GCC);

        this.polygons = asList(CollisionUtils.polygonFromBox(-80f,  -55f, 80f,  55f));
        this.attachmentPosition = new Vector2(80f, 0);
    }
}
