package org.ah.themvsus.engine.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

import org.ah.themvsus.engine.common.message.Message;
import org.ah.themvsus.engine.common.transfer.BufferSerializer;
import org.ah.themvsus.engine.common.transfer.BufferSerializerFactory;

import com.badlogic.gdx.Gdx;

public class DesktopServerCommunication extends AbstractServerCommunication {

    private DatagramChannel datagramChannel;
    private SocketAddress serverSocketAddress;
    private BufferSerializerFactory bufferSerializerFactory = new BufferSerializerFactory();

    @Override
    public void connect(String serverAddress, int serverPort, final ServerConnectionCallback callback) throws IOException {
        serverSocketAddress = new InetSocketAddress(serverAddress, serverPort);

        datagramChannel = DatagramChannel.open();
        // datagramChannel.socket().bind(serverSocketAddress);

        Thread thread = new Thread(() -> {
            setConnected(true);
            callback.successful();

            while (true) {
                try {
                    BufferSerializer serializer = bufferSerializerFactory.obtain();
                    ByteBuffer buffer = serializer.getBuffer();
                    datagramChannel.receive(buffer);
                    buffer.flip();
                    receiver.onMessage(serializer);
                } catch (IOException e) {
                    Gdx.app.error("DesktopServerCommunication", "Error receiving datagram", e);
                }
            }
        });
        thread.setName("Desktop receiver thread");
        thread.setDaemon(true);
        thread.start();
    }

    @Override
    public void send(Message message) throws IOException {
        BufferSerializer serializer = bufferSerializerFactory.obtain();
        try {
            message.serialize(serializer);
            ByteBuffer buffer = serializer.getBuffer();
            buffer.flip();
            datagramChannel.send(buffer, serverSocketAddress);
        } finally {
            serializer.free();
        }
    }
}
