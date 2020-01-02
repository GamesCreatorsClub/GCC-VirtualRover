package org.ah.gcc.virtualrover.message;

import org.ah.themvsus.engine.common.message.MessageCode;
import org.ah.themvsus.engine.common.message.MessageFactory;

public class GCCMessageFactory extends MessageFactory {

    public GCCMessageFactory() {
        super();
        GCCMessageCode.values(); // Need to trigger substitution of PlayerInput
    }

    @Override
    protected void collectTypes() {
        super.collectTypes();
        for (MessageCode messageCode : GCCMessageCode.values()) {
            allCodes.add(messageCode);
        }
    }

    public GCCPlayerInputMessage createPlayerInputCommand() {
        GCCPlayerInputMessage playerInputCommand = newMessage(MessageCode.PlayerInput);
        return playerInputCommand;
    }

    public ClientScreenshotMessage createClientScreenshotMessage(int packetNo, int totalPackets, byte[] pixels, int off, int len) {
        ClientScreenshotMessage clientRegisterMessage = newMessage(GCCMessageCode.ClientScreenshot);
        clientRegisterMessage.setPacketNo(packetNo);
        clientRegisterMessage.setTotalPackets(totalPackets);
        clientRegisterMessage.setBuffer(pixels, off, len);
        return clientRegisterMessage;
    }
}
