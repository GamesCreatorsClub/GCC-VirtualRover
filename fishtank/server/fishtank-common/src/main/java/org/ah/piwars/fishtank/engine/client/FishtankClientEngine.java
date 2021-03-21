package org.ah.piwars.fishtank.engine.client;

import org.ah.piwars.fishtank.game.FishtankGame;
import org.ah.piwars.fishtank.message.FishtankPlayerInputMessage;
import org.ah.themvsus.engine.client.ClientEngine;
import org.ah.themvsus.engine.client.logging.ClientLogging;
import org.ah.themvsus.engine.common.Sender;

public class FishtankClientEngine extends ClientEngine<FishtankGame> {

    public FishtankClientEngine(FishtankGame game, Sender sender, FishtankPlayerInputMessage fishtankPlayerInputMessage, ClientLogging logging) {
        super(game, sender, fishtankPlayerInputMessage, logging, 0);
    }
}
