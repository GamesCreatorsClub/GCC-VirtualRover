package org.ah.gcc.virtualrover.server.engine;

import org.ah.gcc.virtualrover.game.GCCGame;
import org.ah.gcc.virtualrover.input.GCCPlayerInputs;
import org.ah.gcc.virtualrover.message.GCCMessageFactory;
import org.ah.themvsus.engine.common.input.PlayerInputs;
import org.ah.themvsus.engine.common.message.MessageFactory;
import org.ah.themvsus.engine.common.message.ServerInternalMessage;
import org.ah.themvsus.server.authentication.ThemVsUsAuthentication;
import org.ah.themvsus.server.engine.ClientSession;
import org.ah.themvsus.server.engine.ServerEngine;

import java.util.Properties;

public class GCCServerEngine extends ServerEngine<GCCGame> {

    public GCCServerEngine(GCCGame game, ThemVsUsAuthentication themVsUsAuthentication, Properties properties) {
        super(game, themVsUsAuthentication, properties);
    }

    @Override
    protected MessageFactory createMessageFactory() {
        GCCMessageFactory gccMessageFactory = new GCCMessageFactory();
        gccMessageFactory.init();
        return gccMessageFactory;
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
        clientSession.queueToSend(messageFactory.createServerInternal(ServerInternalMessage.State.GameMap, getGame().getChallenge().getName()));
    }

    @Override
    protected void authenticationFailed(ClientSession clientSession) {
    }
}
