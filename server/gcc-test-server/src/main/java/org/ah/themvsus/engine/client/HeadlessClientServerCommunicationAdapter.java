package org.ah.themvsus.engine.client;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.ah.gcc.virtualrover.game.GCCGame;
import org.ah.themvsus.engine.common.message.ChatMessage;
import org.ah.themvsus.engine.common.message.MessageFactory;

public class HeadlessClientServerCommunicationAdapter extends CommonServerCommunicationAdapter<GCCGame> {

    private static final Logger logger = Logger.getLogger("TestServer");

    public HeadlessClientServerCommunicationAdapter(
            ServerCommunication serverCommunication,
            MessageFactory messageFactory) {
        super(new CommonServerCommunicationAdapter.LoggingCallback() {
                @Override public void error(String area, String msg, Throwable e) {
                    logger.log(Level.SEVERE, area + ":" + msg, e);
                }
            },
            serverCommunication, messageFactory);
    }

    @Override
    protected void processChatMessage(ChatMessage chatMessage) {
    }

    public void startEngine(String mapId) {
        GCCGame game = new GCCGame();
        game.init();

        engine = new ClientEngine<GCCGame>(game, sessionId);

        sendClientReady();

        fireGameReady();
    }
}
