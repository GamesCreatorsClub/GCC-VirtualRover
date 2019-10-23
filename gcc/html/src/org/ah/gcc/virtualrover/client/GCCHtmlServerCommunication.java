package org.ah.gcc.virtualrover.client;

import java.io.IOException;

import org.ah.gcc.virtualrover.client.transfer.WebSocketSerializer;
import org.ah.gcc.virtualrover.client.transfer.WebSocketSerializerFactory;
import org.ah.themvsus.engine.client.AbstractServerCommunication;
import org.ah.themvsus.engine.common.message.Message;

import com.github.czyzby.websocket.GwtWebSockets;
import com.github.czyzby.websocket.WebSocket;
import com.github.czyzby.websocket.WebSocketListener;
import com.github.czyzby.websocket.data.WebSocketCloseCode;
import com.github.czyzby.websocket.net.ExtendedNet;
import com.google.gwt.user.client.Window.Location;

public class GCCHtmlServerCommunication extends AbstractServerCommunication {

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
         }

        if (!path.endsWith("/")) {
            path = path.substring(0, path.lastIndexOf('/'));
        }

        if (path.startsWith("/")) {
            path = path.substring(1);
        }

        socket = ExtendedNet.getNet().newWebSocket(serverAddress, serverPort, path + "/websocket");
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
                receiver.onMessage(serializer);
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
    public void send(Message message) {
        WebSocketSerializer serializer = webSocketSerializerFactory.obtain();
        try {
            message.serialize(serializer);
            socket.send(serializer.getBuffer());
        } finally {
            serializer.free();
        }
   }
}
