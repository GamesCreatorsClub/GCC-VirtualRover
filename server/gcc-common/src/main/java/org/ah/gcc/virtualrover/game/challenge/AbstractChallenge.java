package org.ah.gcc.virtualrover.game.challenge;

import org.ah.themvsus.engine.common.game.Game;

public abstract class AbstractChallenge implements Challenge {

    protected Game game;

    protected AbstractChallenge(Game game) {
        this.game = game;
    }

    public Game getGame() {
        return game;
    }
}
