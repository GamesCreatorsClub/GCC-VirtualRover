package org.ah.piwars.virtualrover.game;

import org.ah.themvsus.engine.common.game.GameObjectFactory;
import org.ah.themvsus.engine.common.game.WaitingPlayer;
import org.ah.themvsus.engine.common.input.PlayerInput;

public class PiWarsWaitingPlayer extends WaitingPlayer {

    public PiWarsWaitingPlayer(GameObjectFactory factory, int id) {
        super(factory, id);
    }

    @Override
    public void processPlayerInputs(PlayerInput playerInputs) {
    }
}

