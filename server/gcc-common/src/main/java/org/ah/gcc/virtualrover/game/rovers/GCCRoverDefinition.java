package org.ah.gcc.virtualrover.game.rovers;

import org.ah.gcc.virtualrover.engine.utils.PolygonUtils;

import static java.util.Arrays.asList;

public class GCCRoverDefinition extends AbstractRoverDefinition {

    public GCCRoverDefinition() {
        this.roverControls = new FourSteeringWheelsRoverControls();
        this.polygons = asList(PolygonUtils.polygonFromBox(-55f,  -80f, 55f,  80f));
    }
}
