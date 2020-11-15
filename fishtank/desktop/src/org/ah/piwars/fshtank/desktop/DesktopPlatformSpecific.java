package org.ah.piwars.fshtank.desktop;

import org.ah.piwars.fishtank.AbstractPlatformSpecific;
import org.ah.themvsus.engine.client.ServerCommunication;

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
}
