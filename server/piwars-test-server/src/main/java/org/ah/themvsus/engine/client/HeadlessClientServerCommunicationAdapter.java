package org.ah.themvsus.engine.client;

import org.ah.piwars.virtualrover.game.PiWarsGame;
import org.ah.piwars.virtualrover.input.PiWarsPlayerInput;
import org.ah.piwars.virtualrover.message.PiWarsMessageFactory;
import org.ah.piwars.virtualrover.message.PiWarsPlayerInputMessage;
import org.ah.themvsus.engine.common.message.ChatMessage;

public class HeadlessClientServerCommunicationAdapter extends CommonServerCommunicationAdapter<PiWarsGame> {

    protected PiWarsPlayerInputMessage playerInputMessage;

    public HeadlessClientServerCommunicationAdapter(
            ServerCommunication serverCommunication) {
        super(new LocalClientLogging(), serverCommunication);

        PiWarsMessageFactory messageFactory = new PiWarsMessageFactory();
        messageFactory.init();
        setMessageFactory(messageFactory);

        playerInputMessage = messageFactory.createPlayerInputCommand();
    }

    @Override
    protected void processChatMessage(ChatMessage chatMessage) {
    }

    public void startEngine(String mapId) {
        PiWarsGame game = new PiWarsGame(mapId);
        game.init();

        engine = new ClientEngine<PiWarsGame>(game, serverCommunication, playerInputMessage, logger, sessionId);

        sendClientReady();

        fireGameReady();
    }

    public void setPlayerInput(PiWarsPlayerInput playerInput) {
        getEngine().updateInput(playerInput);

        if (serverCommunication.isConnected()) {
            engine.sendPlayerInput();
        }
    }
}
