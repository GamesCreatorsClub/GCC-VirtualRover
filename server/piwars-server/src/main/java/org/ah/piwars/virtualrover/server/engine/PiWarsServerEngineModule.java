package org.ah.piwars.virtualrover.server.engine;

import static org.ah.themvsus.server.log.LogHelper.GAME_LOGGER;
import static org.ah.themvsus.server.log.LogHelper.SERVER_LOGGER;

import java.util.Properties;
import java.util.logging.Level;

import org.ah.piwars.virtualrover.game.PiWarsGame;
import org.ah.themvsus.server.ServerEngineModule;
import org.ah.themvsus.server.authentication.ThemVsUsAuthentication;
import org.ah.themvsus.server.engine.ServerEngine;

import io.vertx.core.Vertx;

public class PiWarsServerEngineModule extends ServerEngineModule {

    public PiWarsServerEngineModule(Vertx vertx, ThemVsUsAuthentication themVsUsAuthentication, Properties config) {
        super(vertx, themVsUsAuthentication, config);
    }

    @Override
    protected ServerEngine<PiWarsGame> createServerEngine() {
        PiWarsServerEngine themVsUsServerEngine = new PiWarsServerEngine(createGame(), themVsUsAuthentication, config);
        themVsUsServerEngine.init();
        return themVsUsServerEngine;
    }

    public PiWarsGame createGame() {
        try {
            GAME_LOGGER.info("Starting new game");
            SERVER_LOGGER.info("Starting new game");

            PiWarsGame game = new PiWarsGame("PiNoon");
            game.init();

            return game;
        } catch (Throwable t) {
            SERVER_LOGGER.log(Level.SEVERE, "Error before main loop!", t);
            throw new RuntimeException(t);
        }

    }
}
