package org.ah.gcc.virtualrover.game;

import org.ah.themvsus.engine.common.game.GameObjectFactory;
import org.ah.themvsus.engine.common.game.WaitingPlayer;
import org.ah.themvsus.engine.common.input.PlayerInput;

public class GCCWaitingPlayer extends WaitingPlayer {

    public GCCWaitingPlayer(GameObjectFactory factory, int id) {
        super(factory, id);
    }

    @Override
    public void processPlayerInputs(PlayerInput playerInputs) {
    }
}

