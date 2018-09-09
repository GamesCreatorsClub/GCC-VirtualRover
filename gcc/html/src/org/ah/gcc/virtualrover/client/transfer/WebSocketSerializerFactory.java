package org.ah.gcc.virtualrover.client.transfer;

import org.ah.themvsus.engine.common.factory.AbstractPoolFactory;

public class WebSocketSerializerFactory extends AbstractPoolFactory<WebSocketSerializer> {

    public WebSocketSerializerFactory() {
    }

    @Override
    protected WebSocketSerializer createNew() {
        WebSocketSerializer serializer = new WebSocketSerializer(this);
        return serializer;
    }

    @Override
    protected void setup(WebSocketSerializer serializer) {
        serializer.setupForSend();
    }

    public WebSocketSerializer obtain(byte[] buf) {
        WebSocketSerializer deserializer = obtain();
        deserializer.setupToReceive(buf);
        return deserializer;
    }
}
