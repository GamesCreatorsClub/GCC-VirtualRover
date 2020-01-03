package org.ah.piwars.virtualrover.game.rovers;

import org.ah.themvsus.engine.common.input.PlayerInput;

public interface RoverControls {

    void processPlayerInput(Rover rover, PlayerInput playerInput);

}
