package org.ah.gcc.virtualrover.server.engine;

import java.util.Properties;

import org.ah.gcc.virtualrover.game.GCCGame;
import org.ah.themvsus.engine.common.game.GameObject;
import org.ah.themvsus.engine.common.input.GCCPlayerInputs;
import org.ah.themvsus.engine.common.input.PlayerInputs;
import org.ah.themvsus.engine.common.message.MessageFactory;
import org.ah.themvsus.server.authentication.ThemVsUsAuthentication;
import org.ah.themvsus.server.engine.ClientSession;
import org.ah.themvsus.server.engine.ServerEngine;

public class GCCServerEngine extends ServerEngine<GCCGame> {

    public GCCServerEngine(GCCGame game, ThemVsUsAuthentication themVsUsAuthentication, Properties properties) {
        super(game, themVsUsAuthentication, properties);
    }

    @Override
    protected MessageFactory createMessageFactory() {
        return new MessageFactory();
    }

    @Override
    public ClientSession createNewSession() {
        return super.createNewSession();
    }

    @Override
    protected PlayerInputs createPlayerInputs() {
        return new GCCPlayerInputs();
    }

    @Override
    protected void authenticationCompleted(ClientSession clientSession) {
//        clientSession.queueToSend(messageFactory.createServerInternal(ServerInternalMessage.State.GameMap, getGame().getCurrentMapId()));
    }

    @Override
    protected void authenticationFailed(ClientSession clientSession) {
    }

    @Override
    protected void sendWorld(ClientSession clientSession) {
        // TODO whose responsibility is this?
        for (GameObject gameObject : game.getPreviousGameState().gameObjects().values()) {
            clientSession.queueToSend(gameObject.newlyCreatedObjectMessage(messageFactory));
        }

        clientSession.queueToSend(messageFactory.createChatMessage("Welcome " + clientSession.getAlias(), ""));

        clientSession.setReady(true);
    }
}
