package org.ah.gcc.virtualrover.engine.client;

import org.ah.gcc.virtualrover.game.GCCGame;
import org.ah.gcc.virtualrover.message.GCCPlayerInputMessage;
import org.ah.themvsus.engine.client.ClientEngine;
import org.ah.themvsus.engine.client.logging.ClientLogging;
import org.ah.themvsus.engine.common.Sender;
import org.ah.themvsus.engine.common.input.PlayerInputs;

public class GCCClientEngine extends ClientEngine<GCCGame> {

    private int playerTwoId = -1;
    private PlayerInputs playerTwoInputs;

    public GCCClientEngine(GCCGame game, Sender sender, GCCPlayerInputMessage playerInputMessage, ClientLogging logging, int playerOneId, int playerTwoId) {
        super(game, sender, playerInputMessage, logging, playerOneId);
        this.playerTwoId = playerTwoId;
    }

    public GCCClientEngine(GCCGame game, Sender sender, GCCPlayerInputMessage playerInputMessage, ClientLogging logging, int sessionId) {
        this(game, sender, playerInputMessage, logging, sessionId, -1);
    }

    public void processPlayerTwoInputs() {
        if (playerTwoId > 0) {
            getGame().processPlayerInputs(playerTwoId, playerTwoInputs);
        }
    }

    public void setPlayerTwoInputs(PlayerInputs inputs) {
        this.playerTwoInputs = inputs;
    }

    @Override
    public void processPlayerInputs() {
        super.processPlayerInputs();
        if (playerTwoId > 0) {
            getGame().processPlayerInputs(playerTwoId, playerTwoInputs);
        }
    }

    public void setLocalPlayerIds(int playerOneId, int playerTwoId) {
        setSessionId(playerOneId);
        this.playerTwoId = playerTwoId;
    }
}
