package org.ah.gcc.virtualrover.engine.client;

import org.ah.gcc.virtualrover.game.GCCGame;
import org.ah.themvsus.engine.client.ClientEngine;
import org.ah.themvsus.engine.client.logging.ClientLogging;
import org.ah.themvsus.engine.common.input.PlayerInputs;

public class GCCClientEngine extends ClientEngine<GCCGame> {

    private int playerTwoId = -1;
    private PlayerInputs playerTwoInputs;

    public GCCClientEngine(GCCGame game, ClientLogging logging, int playerOneId, int playerTwoId) {
        super(game, logging, playerOneId);
        this.playerTwoId = playerTwoId;
    }

    public GCCClientEngine(GCCGame game, ClientLogging logging, int sessionId) {
        this(game, logging, sessionId, -1);
    }

    public void processPlayerTwoInputs() {
        game.processPlayerInputs(playerTwoId, playerTwoInputs);
    }

    public void setPlayerTwoInputs(PlayerInputs inputs) {
        this.playerTwoInputs = inputs;
    }

    @Override
    public void processPlayerInputs() {
        super.processPlayerInputs();
        game.processPlayerInputs(playerTwoId, playerTwoInputs);
    }

    public void setLocalPlayerIds(int playerOneId, int playerTwoId) {
        setSessionId(playerOneId);
        this.playerTwoId = playerTwoId;
    }
}
