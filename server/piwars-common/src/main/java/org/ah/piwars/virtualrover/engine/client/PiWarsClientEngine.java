package org.ah.piwars.virtualrover.engine.client;

import org.ah.piwars.virtualrover.game.PiWarsGame;
import org.ah.piwars.virtualrover.message.PiWarsPlayerInputMessage;
import org.ah.themvsus.engine.client.ClientEngine;
import org.ah.themvsus.engine.client.logging.ClientLogging;
import org.ah.themvsus.engine.common.Sender;
import org.ah.themvsus.engine.common.input.PlayerInputs;

public class PiWarsClientEngine extends ClientEngine<PiWarsGame> {

    private int playerTwoId = -1;
    private PlayerInputs playerTwoInputs;

    public PiWarsClientEngine(PiWarsGame game, Sender sender, PiWarsPlayerInputMessage playerInputMessage, ClientLogging logging, int playerOneId, int playerTwoId) {
        super(game, sender, playerInputMessage, logging, playerOneId);
        this.playerTwoId = playerTwoId;
    }

    public PiWarsClientEngine(PiWarsGame game, Sender sender, PiWarsPlayerInputMessage playerInputMessage, ClientLogging logging, int sessionId) {
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
