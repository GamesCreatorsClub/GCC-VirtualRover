package org.ah.gcc.virtualrover.message;

import org.ah.themvsus.engine.common.message.Message;
import org.ah.themvsus.engine.common.message.MessageCode;
import org.ah.themvsus.engine.common.message.MessageFactory;
import org.ah.themvsus.engine.common.transfer.Serializer;

import static org.ah.gcc.virtualrover.message.GCCMessageCode.ServerRequestScreenshot;

public class ServerRequestScreenshotMessage extends Message {

    public ServerRequestScreenshotMessage(MessageFactory messageFactory) {
        super(messageFactory);
    }

    @Override
    public MessageCode getType() { return ServerRequestScreenshot; }

    @Override
    public int size() {
        return super.size();
    }

    @Override
    protected void deserializeImpl(Serializer deserializer) {
        super.deserializeImpl(deserializer);
    }

    @Override
    protected void serializeImpl(Serializer serializer) {
    }
}
