package org.ah.gcc.virtualrover.game.rovers;

import org.ah.gcc.virtualrover.engine.utils.PolygonUtils;

import static java.util.Arrays.asList;

public class CBISRoverDefinition extends AbstractRoverDefinition {

    public CBISRoverDefinition() {
        this.roverControls = new TankRoverControls();
        this.polygons = asList(PolygonUtils.polygonFromBox(-75f,  -100f, 75f,  100f));
    }
}
