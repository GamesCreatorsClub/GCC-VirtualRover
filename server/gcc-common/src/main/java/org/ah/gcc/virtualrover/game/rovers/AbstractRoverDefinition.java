package org.ah.gcc.virtualrover.game.rovers;

public class AbstractRoverDefinition implements RoverDefinition {

    protected RoverControls roverControls;

    public AbstractRoverDefinition() {

    }

    @Override
    public RoverControls getRoverControls() {
        return roverControls;
    }

}
