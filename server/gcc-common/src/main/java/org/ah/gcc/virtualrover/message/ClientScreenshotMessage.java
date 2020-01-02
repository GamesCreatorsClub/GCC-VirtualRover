package org.ah.gcc.virtualrover.message;

import org.ah.themvsus.engine.common.message.Message;
import org.ah.themvsus.engine.common.message.MessageCode;
import org.ah.themvsus.engine.common.message.MessageFactory;
import org.ah.themvsus.engine.common.transfer.Serializer;

import static org.ah.gcc.virtualrover.message.GCCMessageCode.ClientScreenshot;

public class ClientScreenshotMessage extends Message {

    private static final byte[] EMPTY_BUFFER = new byte[0];
    private int frameNo;
    private int packetNo;
    private int totalPackets;
    private byte[] buffer;
    private int off;
    private int len = 0;

    public ClientScreenshotMessage(MessageFactory messageFactory) {
        super(messageFactory);
    }

    public void setBuffer(byte[] buffer, int off, int len) {
        this.buffer = buffer;
        this.off = off;
        this.len = len;
    }

    @Override
    public void free() {
        super.free();
        len = 0;
        off = 0;
        buffer = null;
    }

    @Override
    public MessageCode getType() { return ClientScreenshot; }

    public void setFrameNo(int frameNo) {
        this.frameNo = frameNo;
    }

    public int getFrameNo() {
        return frameNo;
    }

    public void setPacketNo(int packetNo) {
        this.packetNo = packetNo;
    }

    public int getPacketNo() {
        return packetNo;
    }

    public void setTotalPackets(int totalPackets) {
        this.totalPackets = totalPackets;
    }

    public int getTotalPackets() {
        return totalPackets;
    }

    public int noDataSize() {
        return super.size() + 4 + 2 + 2 + 2;
    }

    @Override
    public int size() {
        return noDataSize() + len;
    }

    @Override
    protected void deserializeImpl(Serializer deserializer) {
        super.deserializeImpl(deserializer);
        frameNo = deserializer.deserializeInt();
        packetNo = deserializer.deserializeUnsignedShort();
        totalPackets = deserializer.deserializeUnsignedShort();
        buffer = deserializer.deserializeByteArray();
        off = 0;
        len = buffer.length;
    }

    @Override
    protected void serializeImpl(Serializer serializer) {
        serializer.serializeInt(frameNo);

        if (buffer == null) {
            serializer.serializeUnsignedShort(0);
            serializer.serializeUnsignedShort(0);
            serializer.serializeByteArray(EMPTY_BUFFER, 0, 0);
        } else {
            serializer.serializeUnsignedShort(packetNo);
            serializer.serializeUnsignedShort(totalPackets);
            serializer.serializeByteArray(buffer, off, len);
        }
    }
}
