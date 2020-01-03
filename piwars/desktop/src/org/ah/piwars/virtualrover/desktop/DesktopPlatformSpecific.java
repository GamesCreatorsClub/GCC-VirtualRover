package org.ah.piwars.virtualrover.desktop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.Net.HttpRequest;
import com.badlogic.gdx.Net.HttpResponse;
import com.badlogic.gdx.Net.HttpResponseListener;

import org.ah.piwars.virtualrover.AbstractPlatformSpecific;
import org.ah.themvsus.engine.client.ServerCommunication;

import java.io.PrintWriter;
import java.io.StringWriter;

public class DesktopPlatformSpecific extends AbstractPlatformSpecific {

    protected ServerCommunication serverCommunication;

    public DesktopPlatformSpecific() {
        this.serverCommunication = new DesktopServerCommunication();
    }

    protected DesktopPlatformSpecific(ServerCommunication serverCommunication) {
        this.serverCommunication = serverCommunication;
    }

    @Override
    public ServerCommunication getServerCommunication() {
        return serverCommunication;
    }

    @Override
    public void register(String registrationServerURL, String username, String email, String password, final RegistrationCallback callback) {
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
                int statusCode = httpResponse.getStatus().getStatusCode();
                if (statusCode == 200 || statusCode == 201) {
                    callback.success();
                } else {
                    callback.failure("Status code: " + statusCode);
                }
            }

            @Override public void failed(Throwable t) {
                StringWriter w = new StringWriter();
                t.printStackTrace(new PrintWriter(w));
                callback.failure(w.toString());
            }

            @Override public void cancelled() { }
        });
    }

    @Override public boolean readServerDetails() { return true; }
    @Override public boolean needOnScreenKeyboard() { return false; }
}
