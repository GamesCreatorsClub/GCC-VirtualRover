package org.ah.piwars.virtualrover.desktop;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.backends.jogamp.JoglNewtApplication;
import com.badlogic.gdx.backends.jogamp.JoglNewtApplicationConfiguration;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import org.ah.piwars.virtualrover.MainGame;
import org.ah.piwars.virtualrover.PlatformSpecific;
import org.ah.piwars.virtualrover.logging.GdxClientLoggingAdapter;
import org.ah.themvsus.engine.client.desktop.TCPServerCommunication;
import org.ah.themvsus.engine.client.desktop.UDPServerCommunication;

public class PiWarsRoverDesktopLauncher {

    public static void main(String[] args) {
        //
        // $ sudo modprobe snd_pcm_oss
        //
        // To get /dev/dsp
        //
        //

        Parameters parameters = new Parameters();
        parameters.parseArgs(args);


        DesktopPlatformSpecific desktopSpecific;
        if (parameters.isTCP()) {
            desktopSpecific = new DesktopPlatformSpecific(new TCPServerCommunication(GdxClientLoggingAdapter.getInstance()));
        } else {
            desktopSpecific = new DesktopPlatformSpecific(new UDPServerCommunication(GdxClientLoggingAdapter.getInstance()));
        }
        desktopSpecific.setHasSound(parameters.hasSound());
        desktopSpecific.setIsSimulation(parameters.isSimulation());
        desktopSpecific.setLocalOnly(parameters.isLocalOnly());
        if (parameters.getServerAddress() != null) {
            desktopSpecific.setPreferredServerDetails(parameters.getServerAddress().getHostName(), parameters.getServerAddress().getPort());
        }
        desktopSpecific.setRequestedChallenge(parameters.getRequestedChallenge());

        System.out.println("Setting up display as " + parameters.getWidth() + "x" + parameters.getHeight() + " @ " + parameters.getX() + ", " + parameters.getY());
        System.out.println(parameters.hasSound() ? "Set sound on" : "No sound will be loaded or played");

        run(parameters, desktopSpecific);
    }

    public static void run(Parameters parameters, PlatformSpecific desktopSpecific) {
        Application application;
        if (parameters.isJOGL()) {
            application = runJOGL(parameters, desktopSpecific);
        } else {
            application = runLWJGL(parameters, desktopSpecific);
        }

        if (parameters.doDebug()) {
            application.setLogLevel(Application.LOG_DEBUG);
        }
    }

    public static Application runLWJGL(Parameters parameters, PlatformSpecific desktopSpecific) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.x = parameters.getX();
        config.y = parameters.getY();
        config.width = parameters.getWidth();
        config.height = parameters.getHeight();
        config.fullscreen = parameters.isFullScreen();

        return new LwjglApplication(new MainGame(desktopSpecific), config);
    }

    public static Application runJOGL(Parameters parameters, PlatformSpecific desktopSpecific) {
        JoglNewtApplicationConfiguration config = new JoglNewtApplicationConfiguration();
        config.useGL30 = true;
        config.x = parameters.getX();
        config.y = parameters.getY();
        config.width = parameters.getWidth();
        config.height = parameters.getHeight();
        config.undecorated = parameters.isUndecorated();
        config.fullscreen = parameters.isFullScreen();

        return new JoglNewtApplication(new MainGame(desktopSpecific), config);
    }
}
