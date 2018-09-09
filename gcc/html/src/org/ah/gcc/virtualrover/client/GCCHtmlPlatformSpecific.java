package org.ah.gcc.virtualrover.client;

import org.ah.gcc.virtualrover.AbstractPlatformSpecific;
import org.ah.themvsus.engine.client.ServerCommunication;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.Net.HttpRequest;
import com.badlogic.gdx.Net.HttpResponse;
import com.badlogic.gdx.Net.HttpResponseListener;

public class GCCHtmlPlatformSpecific extends AbstractPlatformSpecific {

    private GCCHtmlServerCommunication htmlServerCommunication;

    public GCCHtmlPlatformSpecific() {
        htmlServerCommunication = new GCCHtmlServerCommunication();
    }

    @Override
    public ServerCommunication getServerCommunication() {
        return htmlServerCommunication;
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
                //
                //                int statusCode = httpResponse.getStatus().getStatusCode();
                //                if (statusCode == 200 || statusCode == 201) {
                //                    callback.success();
                //                } else {
                //                    callback.failure("Status code: " + statusCode);
                //                }
            }

            @Override public void failed(Throwable t) {
                callback.failure(t.toString());
            }

            @Override public void cancelled() { }
        });

    }

    @Override public boolean readServerDetails() { return false; }
    @Override public boolean needOnScreenKeyboard() { return false; }
}
