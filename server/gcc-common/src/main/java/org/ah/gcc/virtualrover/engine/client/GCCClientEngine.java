package org.ah.gcc.virtualrover.engine.client;

import org.ah.gcc.virtualrover.game.GCCGame;
import org.ah.themvsus.engine.client.ClientEngine;
import org.ah.themvsus.engine.common.input.PlayerInputs;

public class GCCClientEngine extends ClientEngine<GCCGame> {

    private int playerTwoId = -1;
    private PlayerInputs playerTwoInputs;

    public GCCClientEngine(GCCGame game, int playerOneId, int playerTwoId) {
        super(game, playerOneId);
        this.playerTwoId = playerOneId;
    }

    public GCCClientEngine(GCCGame game, int sessionId) {
        this(game, sessionId, -1);
    }

    public void processPlayerTwoInputs() {
        game.processPlayerInputs(playerTwoId, playerTwoInputs);
    }

    public void setPlayerTwoInputs(PlayerInputs inputs) {
        this.playerTwoInputs = inputs;
    }
}
