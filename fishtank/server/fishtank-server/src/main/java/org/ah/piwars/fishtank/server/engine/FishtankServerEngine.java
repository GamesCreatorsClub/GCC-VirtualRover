package org.ah.piwars.fishtank.server.engine;

import org.ah.piwars.fishtank.game.FishtankGame;
import org.ah.piwars.fishtank.game.FishtankGameTypeObject;
import org.ah.piwars.fishtank.game.fish.Fish;
import org.ah.piwars.fishtank.input.FishtankPlayerInputs;
import org.ah.piwars.fishtank.message.FishtankMessageFactory;
import org.ah.themvsus.engine.common.game.GameObject;
import org.ah.themvsus.engine.common.input.PlayerInputs;
import org.ah.themvsus.engine.common.message.ClientAuthenticateMessage;
import org.ah.themvsus.engine.common.message.MessageFactory;
import org.ah.themvsus.server.authentication.AuthenticationAndAuthorisation;
import org.ah.themvsus.server.engine.ClientSession;
import org.ah.themvsus.server.engine.GameLink;
import org.ah.themvsus.server.engine.ServerEngine;

import java.util.Properties;

import static org.ah.themvsus.server.log.LogHelper.ACCESS_LOGGER;
import static org.ah.themvsus.server.log.LogHelper.GAME_LOGGER;

public class FishtankServerEngine extends ServerEngine<FishtankGame> {

    public FishtankServerEngine(FishtankGame game, AuthenticationAndAuthorisation themVsUsAuthentication, Properties properties) {
        super(game, themVsUsAuthentication, properties);
    }

    @Override
    protected MessageFactory createMessageFactory() {
        FishtankMessageFactory piwarsMessageFactory = new FishtankMessageFactory();
        piwarsMessageFactory.init();
        return piwarsMessageFactory;
    }

    @Override
    public ClientSession<FishtankGame> createNewSession() {
        return super.createNewSession();
    }

    @Override
    protected PlayerInputs createPlayerInputs() {
        return new FishtankPlayerInputs();
    }

    @Override
    protected void authenticationCompleted(ClientSession<?> clientSession) {
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void joinGame(String id, ClientSession<?> clientSession) {
        GameLink<FishtankGame> gameLink = gameLinks.get("default");
        if (gameLink != null) {
            FishtankGame game = gameLink.game;
            ((ClientSession<FishtankGame>)clientSession).setGame(game);
//            int playerId = game.newId();
//
//            WaitingPlayer player = game.getGameObjectFactory().newGameObjectWithId(GameObjectType.WaitingPlayerObject, playerId);
//            player.updateAlias(clientSession.getAlias());

            clientSession.queueToSend(messageFactory.createServerGameDetails(gameLink.getId(), gameLink.getName(), "fishtank", "", clientSession.getSessionId()));
        }
    }

    @Override
    protected void authenticationFailed(ClientSession<?> clientSession) {
    }

    @Override
    protected void clientReadyAction(ClientSession<FishtankGame> clientSession) {
        FishtankGame game = clientSession.getGame();
        if (game != null && !game.containsObject(clientSession.getSessionId())) {
            int playerId = clientSession.getPlayerId();
            GameObject waitingPlayer = game.getCurrentGameState().get(playerId);
            if (waitingPlayer != null) {
                game.removeGameObject(playerId);
            }

            Fish rover = game.spawnFish(clientSession.getSessionId(), FishtankGameTypeObject.Spadefish);
            GAME_LOGGER.info(clientSession.clientString() + ": Created new player at " + rover.getPosition().x + ", " + rover.getPosition().y + "; id=" + rover.getId());
        }
        sendWorld(clientSession);
    }

    @Override
    protected void authenticateSession(ClientSession<FishtankGame> clientSession, ClientAuthenticateMessage clientAuthenticateMessage) {
        ACCESS_LOGGER.info(clientSession.clientString() + ": Authenticated...");
        int id = newSessionId();

        idToClientSession.put(id, clientSession);

        // clientSession.setUser(user);
        clientSession.setSessionId(id);

        clientSession.queueToSend(messageFactory.createServerClientAuthenticatedMessage(clientSession.getSessionId()));

        authenticationCompleted(clientSession);
    }
}
