package org.ah.gcc.virtualrover.client;

import com.github.czyzby.websocket.GwtWebSockets;
import com.github.czyzby.websocket.WebSocket;
import com.github.czyzby.websocket.WebSocketListener;
import com.github.czyzby.websocket.data.WebSocketCloseCode;
import com.github.czyzby.websocket.net.ExtendedNet;
import com.google.gwt.user.client.Window.Location;

import org.ah.gcc.virtualrover.client.transfer.WebSocketSerializer;
import org.ah.gcc.virtualrover.client.transfer.WebSocketSerializerFactory;
import org.ah.themvsus.engine.client.AbstractServerCommunication;
import org.ah.themvsus.engine.common.message.Message;

import java.io.IOException;

public class GCCHtmlServerCommunication extends AbstractServerCommunication<WebSocketSerializer> {

    private WebSocket socket;
    private WebSocketSerializerFactory webSocketSerializerFactory = new WebSocketSerializerFactory();

    public GCCHtmlServerCommunication() {
        GwtWebSockets.initiate();
    }

    @Override
    public void connect(String serverAddress, int serverPort, final ServerConnectionCallback callback) throws IOException {
        String serverPortStr = Location.getPort();
        serverAddress = Location.getHostName();
        String path = Location.getPath();

        if (serverPortStr == null || "".equals(serverPortStr)) {
            serverPort = 80;
        } else {
            try {
                serverPort = Integer.parseInt(serverPortStr);
            } catch (NumberFormatException ignore) { }
        }

        if (!path.endsWith("/")) {
            path = path.substring(0, path.lastIndexOf('/'));
        }

        if (path.startsWith("/")) {
            path = path.substring(1);
        }

        if (path.length() > 0) {
            path = path + "/websocket";
        } else {
            path = "websocket";
        }

        socket = ExtendedNet.getNet().newWebSocket(serverAddress, serverPort, path);
        socket.addListener(new WebSocketListener() {

            @Override
            public boolean onOpen(WebSocket webSocket) {
                setConnected(true);
                callback.successful();
                return true;
            }

            @Override
            public boolean onMessage(WebSocket webSocket, byte[] packet) {
                WebSocketSerializer serializer = webSocketSerializerFactory.obtain(packet);
                if (receivingDelay == 0) {
                    receiver.onMessage(serializer);
                } else {
                    TimeAndBuffer<WebSocketSerializer> timeAndBufferEntry = timeAndBufferFactory.obtain();
                    timeAndBufferEntry.time = System.currentTimeMillis() + receivingDelay;
                    timeAndBufferEntry.buffer = serializer;
                    receivingBuffer.add(timeAndBufferEntry);
                }
                return true;
            }

            @Override
            public boolean onMessage(WebSocket webSocket, String packet) {
                return false;
            }

            @Override
            public boolean onError(WebSocket webSocket, Throwable error) {
                if (!isConnected()) {
                    callback.failed(error.getMessage());
                }
                return false;
            }

            @Override
            public boolean onClose(WebSocket webSocket, WebSocketCloseCode code, String reason) {
                return false;
            }
        });

        socket.connect();
    }

    @Override
    public boolean isConnected() {
        if (socket == null) {
            return false;
        }
        return socket.isOpen();
    }

    @Override
    public void setSendingDelay(int sendingDelay) {
        super.setSendingDelay(sendingDelay);
        if (sendingDelay == 0) {
            super.closeTimerTask();
        } else {
            super.setupTimerTask(0.05f);
        }
    }

    @Override
    public void setReceivingDelay(int receivingDelay) {
        super.setReceivingDelay(receivingDelay);
        if (receivingDelay == 0) {
            super.closeTimerTask();
        } else {
            super.setupTimerTask(0.05f);
        }
    }
    @Override
    public void send(Message message) {
        WebSocketSerializer serializer = webSocketSerializerFactory.obtain();
        try {
            message.serialize(serializer);
            if (sendingDelay == 0) {
                socket.send(serializer.getBuffer());
            } else {
                TimeAndBuffer<WebSocketSerializer> timeAndBufferEntry = timeAndBufferFactory.obtain();
                timeAndBufferEntry.time = System.currentTimeMillis() + sendingDelay;
                timeAndBufferEntry.buffer = serializer;
                sendingBuffer.add(timeAndBufferEntry);
                serializer = null;
            }
        } finally {
            if (serializer != null) {
                serializer.free();
            }
       }
    }

    @Override
    protected void timerTask() {
        processSendingMessages();
    }

    protected void processReceivingMessages() {
        if (receivingBuffer.size() > 0) {
            long now = System.currentTimeMillis();
            TimeAndBuffer<WebSocketSerializer> timeAndBufferEntry = receivingBuffer.peek();
            while (timeAndBufferEntry != null) {
                if (timeAndBufferEntry.time <= now) {
                    receivingBuffer.read();
                    WebSocketSerializer serializer = timeAndBufferEntry.buffer;
                    try {
                        receiver.onMessage(serializer);
                    } finally {
                        serializer.free();
                        timeAndBufferFactory.free(timeAndBufferEntry);
                    }

                    timeAndBufferEntry = receivingBuffer.peek();
                } else {
                    timeAndBufferEntry = null;
                }
            }
        }
    }

    protected void processSendingMessages() {
        if (sendingBuffer.size() > 0) {
            long now = System.currentTimeMillis();
            TimeAndBuffer<WebSocketSerializer> timeAndBufferEntry = sendingBuffer.peek();
            while (timeAndBufferEntry != null) {
                if (timeAndBufferEntry.time <= now) {
                    sendingBuffer.read();
                    WebSocketSerializer serializer = timeAndBufferEntry.buffer;
                    try {
                        socket.send(serializer.getBuffer());
                    } finally {
                        serializer.free();
                        timeAndBufferFactory.free(timeAndBufferEntry);
                    }

                    timeAndBufferEntry = sendingBuffer.peek();
                } else {
                    timeAndBufferEntry = null;
                }
            }
        }
    }
}
