package org.ah.piwars.fishtank;

public abstract class AbstractPlatformSpecific implements PlatformSpecific {

    private String serverAddress;
    private int serverPort;
    private ServerCommunicationAdapterCreatedCallback serverCommunicationAdapterCreatedCallback;
    private boolean hasSound = true;
    protected boolean isSimulation;
    protected boolean isLocalOnly;
    protected String requestedChallenge;

    @Override
    public void setServerDetails(String socketAddress, int port) {
        serverAddress = socketAddress;
        this.serverPort = port;
    }

    @Override
    public String getServerAddress() {
        return serverAddress;
    }

    @Override
    public int getServerPort() {
        return serverPort;
    }

    @Override
    public void fireServerCommunicationAdapterCreatedCallback(ServerCommunicationAdapter serverCommunicationAdapter) {
        if (serverCommunicationAdapterCreatedCallback != null) {
            serverCommunicationAdapterCreatedCallback.created(serverCommunicationAdapter);
        }
    }

    @Override
    public void setServerCommunicationAdapterCreatedCallback(ServerCommunicationAdapterCreatedCallback serverCommunicationAdapterCreatedCallback) {
        this.serverCommunicationAdapterCreatedCallback = serverCommunicationAdapterCreatedCallback;
    }

    public void setHasSound(boolean hasSound) {
        this.hasSound = hasSound;
    }

    @Override
    public boolean hasSound() {
        return this.hasSound;
    }
}
