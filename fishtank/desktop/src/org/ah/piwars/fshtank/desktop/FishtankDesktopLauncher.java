package org.ah.piwars.fshtank.desktop;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.backends.jogamp.JoglNewtApplication;
import com.badlogic.gdx.backends.jogamp.JoglNewtApplicationConfiguration;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

import org.ah.piwars.fishtank.FishtankMain;

import java.net.InetSocketAddress;
import java.util.Properties;

public class FishtankDesktopLauncher {
    public static void main (String[] args) throws Exception {

        Parameters parameters = new Parameters();
        parameters.parseArgs(args);

        DesktopPlatformSpecific platformSpecific = new DesktopPlatformSpecific();
        if (parameters.getTankView() != null) {
            platformSpecific.setTankView(parameters.getTankView());
        }

        InetSocketAddress serverAddress = parameters.getServerAddress();
        if (parameters.isLocal()) {
            Properties serverConfig = new Properties();
            if (parameters.isTCP()) {
                serverConfig.setProperty("tcp.port", Integer.toString(serverAddress.getPort()));
            } else {
                serverConfig.setProperty("udp.port", Integer.toString(serverAddress.getPort()));
            }
            FishtankServer.init(serverConfig);
            platformSpecific.setServerDetails(serverAddress.getHostName(), serverAddress.getPort());
        } else {
            platformSpecific.setServerDetails(parameters.getServerAddress().getHostName(), parameters.getServerAddress().getPort());
        }
        platformSpecific.updateParameters(parameters);


        @SuppressWarnings("unused")
        Application application;
        if (parameters.isJOGL()) {
            application = runJOGL(parameters, platformSpecific);
        } else {
            application = runLWJGL(parameters, platformSpecific);
        }
    }


    public static Application runLWJGL(Parameters parameters, DesktopPlatformSpecific platformSpecific) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        if (parameters.isFullScreen()) {
            config.setFullscreenMode(Lwjgl3ApplicationConfiguration.getDisplayMode());
        } else {
            config.setWindowedMode(parameters.getWidth(), parameters.getHeight());
            config.setWindowPosition(parameters.getX(), parameters.getY());
        }

        return new Lwjgl3Application(new FishtankMain(platformSpecific), config);
    }

    public static Application runJOGL(Parameters parameters, DesktopPlatformSpecific platformSpecific) {
        JoglNewtApplicationConfiguration config = new JoglNewtApplicationConfiguration();
        config.useGL30 = true;
        config.x = parameters.getX();
        config.y = parameters.getY();
        config.width = parameters.getWidth();
        config.height = parameters.getHeight();
        config.undecorated = parameters.isUndecorated();
        config.fullscreen = parameters.isFullScreen();

        return new JoglNewtApplication(new FishtankMain(platformSpecific), config);
    }
}
