package org.ah.gcc.virtualrover.game.rovers;

import com.badlogic.gdx.math.Vector2;

import org.ah.gcc.virtualrover.engine.utils.CollisionUtils;

import static java.util.Arrays.asList;

public class CBISRoverDefinition extends AbstractRoverDefinition {

    public CBISRoverDefinition() {
        super(RoverType.CBIS);
        this.roverControls = new TankRoverControls();
        this.polygons = asList(CollisionUtils.polygonFromBox(-100f,  -75f, 100f,  75f));
        this.attachmentPosition = new Vector2(100f, 0);
    }
}
