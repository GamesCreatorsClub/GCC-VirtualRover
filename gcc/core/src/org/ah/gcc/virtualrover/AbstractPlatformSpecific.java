package org.ah.gcc.virtualrover;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.Net.HttpRequest;
import com.badlogic.gdx.Net.HttpResponse;
import com.badlogic.gdx.Net.HttpResponseListener;

public abstract class AbstractPlatformSpecific implements PlatformSpecific {

    private String preferredServerAddress;
    private int preferredServerPort;
    private ServerCommunicationAdapterCreatedCallback serverCommunicationAdapterCreatedCallback;
    private boolean hasSound = true;
    protected boolean isSimulation;
    protected boolean isLocalOnly;
    protected String requestedChallenge;

    @Override
    public void setPreferredServerDetails(String socketAddress, int port) {
        preferredServerAddress = socketAddress;
        this.preferredServerPort = port;
    }

    @Override
    public String getPreferredServerAddress() {
        return preferredServerAddress;
    }

    @Override
    public int getPreferredServerPort() {
        return preferredServerPort;
    }

    @Override
    public void setIsSimulation(boolean isSimulation) {
        this.isSimulation = isSimulation;
    }

    @Override
    public boolean isSimulation() {
        return isSimulation;
    }

    @Override
    public void setLocalOnly(boolean localOnly) {
        this.isLocalOnly = localOnly;
    }

    @Override
    public boolean isLocalOnly() {
        return isLocalOnly;
    }


    @Override
    public boolean hasServerDetails() {
        return preferredServerAddress != null;
    }


    @Override
    public void register(String registrationServerURL, final String username, final String email, final String password, final RegistrationCallback callback) {
        String form = "-----------------------------123\n"
                + "Content-Disposition: form-data; name=\"username\"\n\n"
                + username + "\n"
                + "-----------------------------123\n"
                + "Content-Disposition: form-data; name=\"email\"\n\n"
                + email + "\n"
                + "-----------------------------123\n"
                + "Content-Disposition: form-data; name=\"password\"\n\n"
                + password + "\n"
                + "-----------------------------123--";

        HttpRequest httpRequest = new HttpRequest(Net.HttpMethods.POST);
        httpRequest.setUrl(registrationServerURL);
        httpRequest.setHeader("Content-Type", "multipart/form-data; boundary=---------------------------123");
        httpRequest.setContent(form);

        Gdx.net.sendHttpRequest(httpRequest, new HttpResponseListener() {
            @Override public void handleHttpResponse(HttpResponse httpResponse) {
                callback.success();
            }

            @Override public void failed(Throwable t) {
                callback.failure(t.toString());
            }

            @Override public void cancelled() { }
        });

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

    @Override
    public String getRequestedChallenge() {
        return requestedChallenge;
    }

    public void setRequestedChallenge(String requestedChallenge) {
        this.requestedChallenge = requestedChallenge;
    }
}
