package org.ah.piwars.fishtank.message;

import org.ah.themvsus.engine.common.message.Message;
import org.ah.themvsus.engine.common.message.MessageCode;
import org.ah.themvsus.engine.common.message.MessageFactory;

public abstract class FishtankMessageCode extends MessageCode {
    static {
        PlayerInput = new FishtankMessageCode() {
            { setOrdinal(PlayerInput.ordinal()); }

            @Override public Message newObject(MessageFactory factory) { return new FishtankPlayerInputMessage(factory); }
            @Override public String toString() { return "FishtankPlayerInput"; }
        };
    }

    public static MessageCode[] values() {
        return new MessageCode[] {

        };
    }
}
