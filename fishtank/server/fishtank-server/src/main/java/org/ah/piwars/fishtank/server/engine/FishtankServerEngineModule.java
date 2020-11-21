package org.ah.piwars.fishtank.server.engine;

import org.ah.piwars.fishtank.game.FishtankGame;
import org.ah.piwars.fishtank.game.FishtankGameTypeObject;
import org.ah.piwars.fishtank.game.fish.Fish;
import org.ah.themvsus.server.ServerEngineModule;
import org.ah.themvsus.server.authentication.AuthenticationAndAuthorisation;
import org.ah.themvsus.server.engine.ServerEngine;

import java.util.Properties;
import java.util.logging.Level;

import static org.ah.themvsus.server.log.LogHelper.GAME_LOGGER;
import static org.ah.themvsus.server.log.LogHelper.SERVER_LOGGER;

import io.vertx.core.Vertx;

public class FishtankServerEngineModule extends ServerEngineModule {

    public FishtankServerEngineModule(Vertx vertx, AuthenticationAndAuthorisation themVsUsAuthentication, Properties config) {
        super(vertx, themVsUsAuthentication, config);
    }

    @Override
    protected ServerEngine<FishtankGame> createServerEngine() {
        FishtankServerEngine themVsUsServerEngine = new FishtankServerEngine(createGame(), themVsUsAuthentication, config);
        themVsUsServerEngine.init();
        return themVsUsServerEngine;
    }

    public FishtankGame createGame() {
        try {
            GAME_LOGGER.info("Starting new game");
            SERVER_LOGGER.info("Starting new game");

            FishtankGame game = new FishtankGame("PiNoon");
            game.init();

            Fish fish1 = game.spawnFish(game.newId(), FishtankGameTypeObject.Spadefish);
            Fish fish2 = game.spawnFish(game.newId(), FishtankGameTypeObject.Spadefish);
            fish2.setPosition(5f, 2f, 2f);

            return game;
        } catch (Throwable t) {
            SERVER_LOGGER.log(Level.SEVERE, "Error before main loop!", t);
            throw new RuntimeException(t);
        }

    }
}
