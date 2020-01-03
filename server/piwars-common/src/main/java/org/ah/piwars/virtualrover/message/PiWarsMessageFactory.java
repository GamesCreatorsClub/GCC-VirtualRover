package org.ah.piwars.virtualrover.message;

import org.ah.themvsus.engine.common.message.MessageCode;
import org.ah.themvsus.engine.common.message.MessageFactory;

public class PiWarsMessageFactory extends MessageFactory {

    public PiWarsMessageFactory() {
        super();
        PiWarsMessageCode.values(); // Need to trigger substitution of PlayerInput
    }

    @Override
    protected void collectTypes() {
        super.collectTypes();
        for (MessageCode messageCode : PiWarsMessageCode.values()) {
            allCodes.add(messageCode);
        }
    }

    public PiWarsPlayerInputMessage createPlayerInputCommand() {
        PiWarsPlayerInputMessage playerInputCommand = newMessage(MessageCode.PlayerInput);
        return playerInputCommand;
    }

    public ClientScreenshotMessage createClientScreenshotMessage(int packetNo, int totalPackets, byte[] pixels, int off, int len) {
        ClientScreenshotMessage clientRegisterMessage = newMessage(PiWarsMessageCode.ClientScreenshot);
        clientRegisterMessage.setPacketNo(packetNo);
        clientRegisterMessage.setTotalPackets(totalPackets);
        clientRegisterMessage.setBuffer(pixels, off, len);
        return clientRegisterMessage;
    }
}
