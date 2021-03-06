package org.ah.piwars.fishtank;

import org.ah.themvsus.engine.client.ServerCommunication;

public interface PlatformSpecific {

    enum TankView {
        FRONT,
        LEFT,
        RIGHT
    }

    static interface RegistrationCallback {
        void success();
        void failure(String message);
    }

    static interface ServerCommunicationAdapterCreatedCallback {
        void created(ServerCommunicationAdapter serverCommunicationAdapter);
    }

    ServerCommunication getServerCommunication();

    void setServerDetails(String socketAddress, int port);

    String getServerAddress();

    int getServerPort();

    boolean hasSound();

    void fireServerCommunicationAdapterCreatedCallback(ServerCommunicationAdapter serverCommunicationAdapter);

    void setServerCommunicationAdapterCreatedCallback(ServerCommunicationAdapterCreatedCallback serverCommunicationAdapterCreatedCallback);

    TankView getTankView();

    boolean isMirrorWalls();

    boolean isHighresFloor();

    boolean isCameraInputAllowed();
}
