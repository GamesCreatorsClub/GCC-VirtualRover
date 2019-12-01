package org.ah.themvsus.engine.client;

import org.ah.gcc.virtualrover.game.GCCGame;
import org.ah.gcc.virtualrover.message.GCCMessageFactory;
import org.ah.themvsus.engine.common.message.ChatMessage;

public class HeadlessClientServerCommunicationAdapter extends CommonServerCommunicationAdapter<GCCGame> {

    public HeadlessClientServerCommunicationAdapter(
            ServerCommunication serverCommunication) {
        super(new LocalClientLogging(), serverCommunication);

        GCCMessageFactory messageFactory = new GCCMessageFactory();
        messageFactory.init();
        setMessageFactory(messageFactory);
    }

    @Override
    protected void processChatMessage(ChatMessage chatMessage) {
    }

    public void startEngine(String mapId) {
        GCCGame game = new GCCGame("PiNoon");
        game.init();

        engine = new ClientEngine<GCCGame>(game, logger, sessionId);

        sendClientReady();

        fireGameReady();
    }
}
