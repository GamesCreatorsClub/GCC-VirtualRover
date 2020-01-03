package org.ah.piwars.virtualrover;

import org.ah.themvsus.engine.client.ServerCommunication;

public interface PlatformSpecific {

    static interface RegistrationCallback {
        void success();
        void failure(String message);
    }

    static interface ServerCommunicationAdapterCreatedCallback {
        void created(ServerCommunicationAdapter serverCommunicationAdapter);
    }

    ServerCommunication getServerCommunication();

    void setIsSimulation(boolean isSimulation);

    boolean isSimulation();

    void setLocalOnly(boolean localOnly);

    boolean isLocalOnly();

    boolean hasServerDetails();

    void register(String url, String username, String email, String password, RegistrationCallback callback);

    void setPreferredServerDetails(String socketAddress, int port);

    String getPreferredServerAddress();

    int getPreferredServerPort();

    boolean readServerDetails();

    boolean needOnScreenKeyboard();

    boolean hasSound();

    void fireServerCommunicationAdapterCreatedCallback(ServerCommunicationAdapter serverCommunicationAdapter);

    void setServerCommunicationAdapterCreatedCallback(ServerCommunicationAdapterCreatedCallback serverCommunicationAdapterCreatedCallback);

    String getRequestedChallenge();
}
