package org.ah.piwars.fshtank.desktop;

import org.ah.piwars.fishtank.AbstractPlatformSpecific;
import org.ah.themvsus.engine.client.ServerCommunication;

import java.util.Properties;

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

    public void updateParameters(Parameters parameters) {
        setTankView(parameters.getTankView());
        Properties props = parameters.getProperties();

        setMirrorWalls(Boolean.valueOf(props.getProperty("gfx.mirrorwalls", "true")));
        setHighresFloor(Boolean.valueOf(props.getProperty("gfx.highresfloor", "true")));
        setCameraInputAllowed(parameters.isCameraInputAllowed());
    }
}
