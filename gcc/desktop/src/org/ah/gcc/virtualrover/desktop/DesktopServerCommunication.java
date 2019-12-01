package org.ah.gcc.virtualrover.desktop;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;

import org.ah.themvsus.engine.client.AbstractServerCommunication;
import org.ah.themvsus.engine.common.message.Message;
import org.ah.themvsus.engine.common.message.PlayerInputMessage;
import org.ah.themvsus.engine.common.transfer.BufferSerializer;
import org.ah.themvsus.engine.common.transfer.BufferSerializerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

public class DesktopServerCommunication extends AbstractServerCommunication {

    private static final int MAGIC = 0xAA;

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
                ByteBuffer buffer = null;
                try {
                    BufferSerializer serializer = bufferSerializerFactory.obtain();
                    buffer = serializer.getBuffer();
                    datagramChannel.receive(buffer);
                    buffer.flip();
                    int headerLength = readHeaderLength(buffer);
                    if (headerLength > 0) {
                        if (buffer.remaining() != headerLength) {
                            if (Gdx.app.getLogLevel() >= Application.LOG_DEBUG) {
                                Gdx.app.error("Network", "Error received bad packet; header size " + headerLength + " while available " + buffer.remaining());
                            }
                            buffer.clear();
                        } else {
                            receiver.onMessage(serializer);
                        }
                    } else {
                        buffer.clear();
                    }
                } catch (IOException e) {
                    if (buffer != null) { buffer.clear(); }
                    Gdx.app.error("Network", "Error receiving datagram", e);
                } catch (RuntimeException e) {
                    if (buffer != null) { buffer.clear(); }
                    Gdx.app.error("Network", "Error receiving datagram", e);
                }
            }
        });
        thread.setName("Desktop receiver thread");
        thread.setDaemon(true);
        thread.start();
    }

    private static String hex(int b1, int b2) {
        String s1 = Integer.toHexString(b1);
        String s2 = Integer.toHexString(b2);
        if (s1.length() == 1) { s1 = "0" + s1; }
        if (s2.length() == 1) { s2 = "0" + s2; }
        return "0x" + s1 + s2;
    }

    private static int readHeaderLength(ByteBuffer buffer) {
        if (buffer.hasRemaining()) {
            int b1 = unsignedByte(buffer.get());
            if (buffer.hasRemaining()) {
                int b2 = unsignedByte(buffer.get());
                if ((b1 & 0xfe) == MAGIC) {
                    return b2 + ((b1 & 0x1) << 8);
                } else {
                    if (Gdx.app.getLogLevel() >= Application.LOG_DEBUG) {
                        Gdx.app.error("Network", "Error received bad packet - header " + hex(b1, b2));
                    }
                    return -1;
                }
            } else {
                if (Gdx.app.getLogLevel() >= Application.LOG_DEBUG) {
                    Gdx.app.error("Network", "Error received only one byte packet 0x" + Integer.toHexString(b1));
                }
            }
        }
        if (Gdx.app.getLogLevel() >= Application.LOG_DEBUG) {
            Gdx.app.error("Network", "Error received empty packet ");
        }
        return -2;
    }

    private static int unsignedByte(int b) {
        if (b < 0) {
            b = b + 256;
        }
        return b;
    }

    @Override
    public void send(Message message) throws IOException {
        if (Gdx.app.getLogLevel() >= Application.LOG_DEBUG) {
            // TODO handle this better
            if (!(message instanceof PlayerInputMessage)) {
                Gdx.app.debug("Network", System.currentTimeMillis() + " Sent    : " + message);
            }
        }

        BufferSerializer serializer = bufferSerializerFactory.obtain();
        try {
            int header = message.size() | (MAGIC << 8);
            serializer.serializeUnsignedByte(header >> 8);
            serializer.serializeUnsignedByte(header & 0xFF);
            message.serialize(serializer);
            ByteBuffer buffer = serializer.getBuffer();
            buffer.flip();
            datagramChannel.send(buffer, serverSocketAddress);
        } finally {
            serializer.free();
        }
    }
}
