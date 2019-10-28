package org.ah.gcc.virtualrover.game.rovers;

public class GCCRoverDefinition extends AbstractRoverDefinition {

    public GCCRoverDefinition() {
        this.roverControls = new FourSteeringWheelsRoverControls();
    }
}
