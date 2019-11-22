package org.ah.themvsus.engine.client;

import org.ah.gcc.virtualrover.game.GCCGame;
import org.ah.gcc.virtualrover.message.GCCMessageFactory;
import org.ah.themvsus.engine.common.message.ChatMessage;

import java.util.logging.Level;
import java.util.logging.Logger;

public class HeadlessClientServerCommunicationAdapter extends CommonServerCommunicationAdapter<GCCGame> {

    private static final Logger logger = Logger.getLogger("TestServer");

    public HeadlessClientServerCommunicationAdapter(
            ServerCommunication serverCommunication) {
        super(new CommonServerCommunicationAdapter.LoggingCallback() {
                @Override public void error(String area, String msg, Throwable e) {
                    logger.log(Level.SEVERE, area + ":" + msg, e);
                }
            },
            serverCommunication);

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

        engine = new ClientEngine<GCCGame>(game, sessionId);

        sendClientReady();

        fireGameReady();
    }
}
