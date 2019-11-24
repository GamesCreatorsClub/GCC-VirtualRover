package org.ah.gcc.virtualrover.game.rovers;

import org.ah.gcc.virtualrover.game.Rover;
import org.ah.themvsus.engine.common.input.PlayerInput;

public interface RoverControls {

    void processPlayerInput(Rover gccPlayer, PlayerInput playerInput);

}
