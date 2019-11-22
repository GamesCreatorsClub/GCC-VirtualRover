package org.ah.gcc.virtualrover.message;

import org.ah.themvsus.engine.common.message.Message;
import org.ah.themvsus.engine.common.message.MessageCode;
import org.ah.themvsus.engine.common.message.MessageFactory;

public abstract class GCCMessageCode extends MessageCode {
    static {
        PlayerInput = new GCCMessageCode() {
            { setOrdinal(PlayerInput.ordinal()); }

            @Override public Message newObject(MessageFactory factory) { return new GCCPlayerInputMessage(factory); }
            @Override public String toString() { return "ThemVsUsPlayerInput"; }
        };
    }

    public static MessageCode[] values() {
        return new MessageCode[] {};
    }
}
