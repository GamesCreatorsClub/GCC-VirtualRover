package org.ah.gcc.virtualrover.desktop;

import com.badlogic.gdx.backends.jogamp.JoglNewtApplication;
import com.badlogic.gdx.backends.jogamp.JoglNewtApplicationConfiguration;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import org.ah.gcc.virtualrover.MainGame;
import org.ah.gcc.virtualrover.PlatformSpecific;

public class GCCRoverDesktopLauncher {

    public static void main(String[] args) {
        //
        // $ sudo modprobe snd_pcm_oss
        //
        // To get /dev/dsp
        //
        //

        Parameters parameters = new Parameters();
        parameters.parseArgs(args);

        DesktopPlatformSpecific desktopSpecific = new DesktopPlatformSpecific();
        desktopSpecific.setHasSound(parameters.hasSound());
        desktopSpecific.setIsSimulation(parameters.isSimulation());
        if (parameters.getServerAddress() != null) {
            desktopSpecific.setPreferredServerDetails(parameters.getServerAddress().getHostName(), parameters.getServerAddress().getPort());
        }

        System.out.println("Setting up display as " + parameters.getWidth() + "x" + parameters.getHeight() + " @ " + parameters.getX() + ", " + parameters.getY());
        System.out.println(parameters.hasSound() ? "Set sound on" : "No sound will be loaded or played");

        if (parameters.isJOGL()) {
            runJOGL(parameters, desktopSpecific);
        } else {
            runLWJGL(parameters, desktopSpecific);
        }
    }

    public static void runLWJGL(Parameters parameters, PlatformSpecific desktopSpecific) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.x = parameters.getX();
        config.y = parameters.getY();
        config.width = parameters.getWidth();
        config.height = parameters.getHeight();
        config.fullscreen = parameters.isFullScreen();

        new LwjglApplication(new MainGame(desktopSpecific), config);
    }

    public static void runJOGL(Parameters parameters, PlatformSpecific desktopSpecific) {
        JoglNewtApplicationConfiguration config = new JoglNewtApplicationConfiguration();
        config.useGL30 = true;
        config.x = parameters.getX();
        config.y = parameters.getY();
        config.width = parameters.getWidth();
        config.height = parameters.getHeight();
        config.undecorated = parameters.isUndecorated();
        config.fullscreen = parameters.isFullScreen();

        new JoglNewtApplication(new MainGame(desktopSpecific), config);
    }
}
