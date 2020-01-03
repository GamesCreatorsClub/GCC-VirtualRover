package org.ah.piwars.virtualrover.client.transfer;

import org.ah.themvsus.engine.common.transfer.AbstractSerializer;

import com.github.czyzby.websocket.serialization.impl.Deserializer;
import com.github.czyzby.websocket.serialization.impl.Serializer;

public class WebSocketSerializer extends AbstractSerializer {

    private Serializer serializer = new Serializer();
    private Deserializer deserializer = new Deserializer();
    private WebSocketSerializerFactory factory;
    private int totalSent = 0;

    WebSocketSerializer(WebSocketSerializerFactory factory) {
        this.factory = factory;
    }

    void setupForSend() {
        serializer.reset();
    }

    void setupToReceive(byte[] buf) {
        deserializer.setSerializedData(buf);
    }

    public byte[] getBuffer() {
        return serializer.serialize();
    }

    @Override
    public void free() {
        totalSent = 0;
        factory.free(this);
    }

    @Override
    public void serializeByte(byte b) {
        totalSent = 1;
        serializer.serializeByte(b);
    }

    @Override
    public void serializeShort(short s) {
        totalSent = 2;
        serializer.serializeShort(s);
    }

    @Override
    public void serializeInt(int i) {
        totalSent = 4;
        serializer.serializeInt(i);
    }

    @Override
    public void serializeLong(long l) {
        totalSent = 8;
        serializer.serializeLong(l);
    }

    @Override
    public byte deserializeByte() {
        return deserializer.deserializeByte();
    }

    @Override
    public short deserializeShort() {
        return deserializer.deserializeShort();
    }

    @Override
    public int deserializeInt() {
        return deserializer.deserializeInt();
    }

    @Override
    public long deserializeLong() {
        return deserializer.deserializeLong();
    }

    @Override
    public int getTotalSize() {
        return totalSent + 40; // IP(20bytes) + TCP(20bytes) - no options
    }
}
