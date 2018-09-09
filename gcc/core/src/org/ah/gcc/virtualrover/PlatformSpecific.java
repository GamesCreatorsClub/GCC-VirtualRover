package org.ah.gcc.virtualrover;

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

    void register(String url, String username, String email, String password, RegistrationCallback callback);

    void setPreferredServerDetails(String socketAddress, int port);

    String getPreferredServerAddress();

    int getPreferredServerPort();

    boolean readServerDetails();

    boolean needOnScreenKeyboard();

    void fireServerCommunicationAdapterCreatedCallback(ServerCommunicationAdapter serverCommunicationAdapter);

    void setServerCommunicationAdapterCreatedCallback(ServerCommunicationAdapterCreatedCallback serverCommunicationAdapterCreatedCallback);
}