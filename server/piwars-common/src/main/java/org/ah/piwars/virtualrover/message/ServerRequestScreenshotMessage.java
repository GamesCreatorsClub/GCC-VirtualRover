package org.ah.piwars.virtualrover.message;

import org.ah.themvsus.engine.common.message.Message;
import org.ah.themvsus.engine.common.message.MessageCode;
import org.ah.themvsus.engine.common.message.MessageFactory;
import org.ah.themvsus.engine.common.transfer.Serializer;

import static org.ah.piwars.virtualrover.message.PiWarsMessageCode.ServerRequestScreenshot;

public class ServerRequestScreenshotMessage extends Message {

    private int cameraId;

    public ServerRequestScreenshotMessage(MessageFactory messageFactory) {
        super(messageFactory);
    }

    @Override
    public MessageCode getType() { return ServerRequestScreenshot; }

    @Override
    public int size() {
        return super.size() + 2;
    }

    public void setCameraId(int cameraId) {
        this.cameraId = cameraId;
    }

    public int getCameraId() {
        return cameraId;
    }

    @Override
    protected void deserializeImpl(Serializer deserializer) {
        super.deserializeImpl(deserializer);
        cameraId = deserializer.deserializeUnsignedShort();
    }

    @Override
    protected void serializeImpl(Serializer serializer) {
        serializer.serializeUnsignedShort(cameraId);
    }
}
