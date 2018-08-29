package org.ah.gcc.virtualrover.game;


import org.ah.themvsus.engine.common.game.Game;
import org.ah.themvsus.engine.common.game.Player;

public class GCCGame extends Game {

    public GCCGame() {
        super();
    }

    @Override
    public Player spawnPlayer(int id, String alias) {
        Player player = super.spawnPlayer(id, alias);

        return player;
    }

}
