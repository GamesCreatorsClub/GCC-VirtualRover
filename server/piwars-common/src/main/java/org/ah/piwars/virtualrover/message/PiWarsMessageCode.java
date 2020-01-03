package org.ah.piwars.virtualrover.message;

import org.ah.themvsus.engine.common.message.Message;
import org.ah.themvsus.engine.common.message.MessageCode;
import org.ah.themvsus.engine.common.message.MessageFactory;

public abstract class PiWarsMessageCode extends MessageCode {
    static {
        PlayerInput = new PiWarsMessageCode() {
            { setOrdinal(PlayerInput.ordinal()); }

            @Override public Message newObject(MessageFactory factory) { return new PiWarsPlayerInputMessage(factory); }
            @Override public String toString() { return "PiWarsPlayerInput"; }
        };
    }

    public static MessageCode ServerRequestScreenshot = new MessageCode() {
        @Override public Message newObject(MessageFactory factory) { return new ServerRequestScreenshotMessage(factory); }
        @Override public String toString() { return "ServerRequestScreenshot"; }
    };

    public static MessageCode ClientScreenshot = new MessageCode() {
        @Override public Message newObject(MessageFactory factory) { return new ClientScreenshotMessage(factory); }
        @Override public String toString() { return "ClientScreenshot"; }
    };

    public static MessageCode[] values() {
        return new MessageCode[] {
                ServerRequestScreenshot, ClientScreenshot,
        };
    }
}
