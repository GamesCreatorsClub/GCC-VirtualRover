package org.ah.piwars.fishtank.message;

import org.ah.themvsus.engine.common.message.MessageCode;
import org.ah.themvsus.engine.common.message.MessageFactory;

public class FishtankMessageFactory extends MessageFactory {

    public FishtankMessageFactory() {
        super();
        FishtankMessageCode.values(); // Need to trigger substitution of PlayerInput
    }

    @Override
    protected void collectTypes() {
        super.collectTypes();
        for (MessageCode messageCode : FishtankMessageCode.values()) {
            allCodes.add(messageCode);
        }
    }

    public FishtankPlayerInputMessage createPlayerInputCommand() {
        FishtankPlayerInputMessage playerInputCommand = newMessage(MessageCode.PlayerInput);
        return playerInputCommand;
    }
}
