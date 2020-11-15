package org.ah.piwars.fishtank.message;

import org.ah.piwars.fishtank.input.FishtankPlayerInputs;
import org.ah.themvsus.engine.common.input.PlayerInputs;
import org.ah.themvsus.engine.common.message.MessageFactory;
import org.ah.themvsus.engine.common.message.PlayerInputMessage;

public class FishtankPlayerInputMessage extends PlayerInputMessage {

    FishtankPlayerInputMessage(MessageFactory messageFactory) {
        super(messageFactory);
    }

    @Override
    protected PlayerInputs createPlayerInputs() {
        return new FishtankPlayerInputs();
    }
}
