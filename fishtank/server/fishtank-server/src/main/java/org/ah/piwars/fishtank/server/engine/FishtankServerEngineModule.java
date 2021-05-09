package org.ah.piwars.fishtank.server.engine;

import org.ah.piwars.fishtank.game.CameraPositionObject;
import org.ah.piwars.fishtank.game.FishtankGame;
import org.ah.piwars.fishtank.game.FishtankGameTypeObject;
import org.ah.piwars.fishtank.game.fish.AnchorObject;
import org.ah.piwars.fishtank.game.fish.BenchyObject;
import org.ah.piwars.fishtank.game.fish.Fish;
import org.ah.piwars.fishtank.game.fish.TresureObject;
import org.ah.themvsus.server.ServerEngineModule;
import org.ah.themvsus.server.authentication.AuthenticationAndAuthorisation;
import org.ah.themvsus.server.engine.ServerEngine;

import java.util.Properties;
import java.util.logging.Level;

import static com.badlogic.gdx.math.MathUtils.atan2;

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

            int cameraPositionId = game.newId();
            CameraPositionObject cameraPosition = game.getGameObjectFactory().newGameObjectWithId(FishtankGameTypeObject.CameraPosition, cameraPositionId);
            game.addNewGameObject(cameraPosition);

            Fish fish1 = game.spawnFish(game.newId(), FishtankGameTypeObject.Spadefish);
            fish1.getOrientation().setFromAxisRad(0f, 1f, 0f, atan2(1f, 3f));
            fish1.setPosition(40f, 0f, 2f);

            Fish fish2 = game.spawnFish(game.newId(), FishtankGameTypeObject.Tetrafish);
            fish2.getOrientation().setFromAxisRad(0f, 1f, 0f, atan2(1f, 3f));
            fish2.setPosition(-30f, 0f, 2f);

            Fish fish3 = game.spawnFish(game.newId(), FishtankGameTypeObject.Tetrafish);
            fish3.getOrientation().setFromAxisRad(0f, 1f, 0f, atan2(1f, 3f));
            fish3.setPosition(0f, 0f, 2f);

            Fish fish4 = game.spawnFish(game.newId(), FishtankGameTypeObject.Tetrafish);
            fish4.getOrientation().setFromAxisRad(0f, 1f, 0f, atan2(-1f, 3f));
            fish4.setPosition(0f, 0f, 20f);

            int anchorId = game.newId();
            AnchorObject anchor = game.getGameObjectFactory().newGameObjectWithId(FishtankGameTypeObject.Anchor, anchorId);
            game.addNewGameObject(anchor);
            anchor.setPosition(10f, -60f, 60f);

            int treasureId = game.newId();
            TresureObject treasure = game.getGameObjectFactory().newGameObjectWithId(FishtankGameTypeObject.Tresure, treasureId);
            game.addNewGameObject(treasure);
            treasure.setPosition(-60f, -60f, 50f);

            int benchyId = game.newId();
            BenchyObject benchy = game.getGameObjectFactory().newGameObjectWithId(FishtankGameTypeObject.Benchy, benchyId);
            game.addNewGameObject(benchy);
            benchy.setPosition(20f, -60f, -20f);

            return game;
        } catch (Throwable t) {
            SERVER_LOGGER.log(Level.SEVERE, "Error before main loop!", t);
            throw new RuntimeException(t);
        }

    }
}
