package org.ah.gcc.virtualrover.game.rovers;

import com.badlogic.gdx.math.Vector2;

import org.ah.gcc.virtualrover.engine.utils.PolygonUtils;

import static java.util.Arrays.asList;

public class GCCRoverDefinition extends AbstractRoverDefinition {

    public GCCRoverDefinition() {
        super(RoverType.GCC);
        this.roverControls = new FourSteeringWheelsRoverControls();
        this.polygons = asList(PolygonUtils.polygonFromBox(-80f,  -55f, 80f,  55f));
        this.attachmentPosition = new Vector2(80f, 0);
    }
}
