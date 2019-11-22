package org.ah.gcc.virtualrover.game.challenge;

import org.ah.themvsus.engine.common.game.Game;

public abstract class AbstractChallenge implements Challenge {

    protected Game game;
    protected String name;

    protected AbstractChallenge(Game game, String name) {
        this.game = game;
        this.name = name;
    }

    public Game getGame() {
        return game;
    }

    @Override
    public String getName() {
        return name;
    }
}
