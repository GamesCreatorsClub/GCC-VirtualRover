package org.ah.piwars.virtualrover.game.challenge;

import org.ah.themvsus.engine.common.game.Game;
import org.ah.themvsus.engine.common.game.GameObject;

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

    @Override
    public void beforeGameObjectAdded(GameObject gameObject) {
    }

    @Override
    public void gameObjectRemoved(GameObject gameObject) {
    }
}
