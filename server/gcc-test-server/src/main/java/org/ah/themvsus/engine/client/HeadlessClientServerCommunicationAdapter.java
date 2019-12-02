package org.ah.themvsus.engine.client;

import org.ah.gcc.virtualrover.game.GCCGame;
import org.ah.gcc.virtualrover.message.GCCMessageFactory;
import org.ah.gcc.virtualrover.message.GCCPlayerInputMessage;
import org.ah.themvsus.engine.common.message.ChatMessage;

public class HeadlessClientServerCommunicationAdapter extends CommonServerCommunicationAdapter<GCCGame> {

    protected GCCPlayerInputMessage playerInputMessage;

    public HeadlessClientServerCommunicationAdapter(
            ServerCommunication serverCommunication) {
        super(new LocalClientLogging(), serverCommunication);

        GCCMessageFactory messageFactory = new GCCMessageFactory();
        messageFactory.init();
        setMessageFactory(messageFactory);

        playerInputMessage = messageFactory.createPlayerInputCommand();
    }

    @Override
    protected void processChatMessage(ChatMessage chatMessage) {
    }

    public void startEngine(String mapId) {
        GCCGame game = new GCCGame("PiNoon");
        game.init();

        engine = new ClientEngine<GCCGame>(game, playerInputMessage, logger, sessionId);

        sendClientReady();

        fireGameReady();
    }
}
