package org.ah.gcc.virtualrover.game.rovers;

import org.ah.gcc.virtualrover.game.GCCPlayer;
import org.ah.themvsus.engine.common.input.PlayerInput;

public interface RoverControls {

    void processPlayerInput(GCCPlayer gccPlayer, PlayerInput playerInput);

}
