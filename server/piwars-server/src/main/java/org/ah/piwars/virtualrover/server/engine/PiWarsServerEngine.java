package org.ah.piwars.virtualrover.server.engine;

import org.ah.piwars.virtualrover.game.PiWarsGame;
import org.ah.piwars.virtualrover.game.rovers.Rover;
import org.ah.piwars.virtualrover.game.rovers.RoverType;
import org.ah.piwars.virtualrover.input.PiWarsPlayerInputs;
import org.ah.piwars.virtualrover.message.PiWarsMessageFactory;
import org.ah.themvsus.engine.common.game.GameObject;
import org.ah.themvsus.engine.common.game.GameObjectType;
import org.ah.themvsus.engine.common.game.WaitingPlayer;
import org.ah.themvsus.engine.common.input.PlayerInputs;
import org.ah.themvsus.engine.common.message.MessageFactory;
import org.ah.themvsus.server.authentication.ThemVsUsAuthentication;
import org.ah.themvsus.server.engine.ClientSession;
import org.ah.themvsus.server.engine.ServerEngine;

import java.util.Properties;

import static org.ah.themvsus.server.log.LogHelper.GAME_LOGGER;

public class PiWarsServerEngine extends ServerEngine<PiWarsGame> {

    public PiWarsServerEngine(PiWarsGame game, ThemVsUsAuthentication themVsUsAuthentication, Properties properties) {
        super(game, themVsUsAuthentication, properties);
    }

    @Override
    protected MessageFactory createMessageFactory() {
        PiWarsMessageFactory piwarsMessageFactory = new PiWarsMessageFactory();
        piwarsMessageFactory.init();
        return piwarsMessageFactory;
    }

    @Override
    public ClientSession<?> createNewSession() {
        return super.createNewSession();
    }

    @Override
    protected PlayerInputs createPlayerInputs() {
        return new PiWarsPlayerInputs();
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void authenticationCompleted(ClientSession<?> clientSession) {
        ((ClientSession<PiWarsGame>)clientSession).setGame(game);
        int playerId = game.newId();

        WaitingPlayer player = game.getGameObjectFactory().newGameObjectWithId(GameObjectType.WaitingPlayerObject, playerId);
        player.updateAlias(clientSession.getAlias());

        clientSession.queueToSend(messageFactory.createServerGameDetails(game.getChallenge().getName(), "", playerId));
    }

    @Override
    protected void authenticationFailed(ClientSession<?> clientSession) {
    }

    @Override
    protected void clientReadyAction(ClientSession<?> clientSession) {
        if (!game.containsObject(clientSession.getSessionId())) {
            int playerId = clientSession.getPlayerId();
            GameObject waitingPlayer = game.getCurrentGameState().get(playerId);
            if (waitingPlayer != null) {
                game.removeGameObject(playerId);
            }

            Rover rover = game.spawnRover(clientSession.getSessionId(), clientSession.getAlias(), RoverType.GCCM16);
            GAME_LOGGER.info(clientSession.clientString() + ": Created new player at " + rover.getPosition().x + ", " + rover.getPosition().y + "; id=" + rover.getId());
        }
        sendWorld(clientSession);
    }
}
