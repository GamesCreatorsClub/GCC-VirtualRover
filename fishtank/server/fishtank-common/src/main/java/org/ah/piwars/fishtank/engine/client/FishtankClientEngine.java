package org.ah.piwars.fishtank.engine.client;

import org.ah.piwars.fishtank.game.FishtankGame;
import org.ah.themvsus.engine.client.ClientEngine;
import org.ah.themvsus.engine.client.logging.ClientLogging;
import org.ah.themvsus.engine.common.Sender;

public class FishtankClientEngine extends ClientEngine<FishtankGame> {

    public FishtankClientEngine(FishtankGame game, Sender sender, ClientLogging logging) {
        super(game, sender, null, logging, 0);
    }
}
