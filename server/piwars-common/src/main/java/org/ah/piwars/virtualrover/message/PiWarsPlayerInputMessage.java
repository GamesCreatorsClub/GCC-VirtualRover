package org.ah.piwars.virtualrover.message;

import org.ah.piwars.virtualrover.input.PiWarsPlayerInputs;
import org.ah.themvsus.engine.common.input.PlayerInputs;
import org.ah.themvsus.engine.common.message.MessageFactory;
import org.ah.themvsus.engine.common.message.PlayerInputMessage;

public class PiWarsPlayerInputMessage extends PlayerInputMessage {

    PiWarsPlayerInputMessage(MessageFactory messageFactory) {
        super(messageFactory);
    }

    @Override
    protected PlayerInputs createPlayerInputs() {
        return new PiWarsPlayerInputs();
    }
}
