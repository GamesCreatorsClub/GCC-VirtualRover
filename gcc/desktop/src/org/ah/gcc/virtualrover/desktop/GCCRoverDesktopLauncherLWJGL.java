package org.ah.gcc.virtualrover.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import org.ah.gcc.virtualrover.MainGame;
import org.ah.gcc.virtualrover.PlatformSpecific;

public class GCCRoverDesktopLauncherLWJGL {

    public static void main(String[] args) {
        Parameters parameters = new Parameters();
        parameters.parseArgs(args);

        DesktopPlatformSpecific desktopSpecific = new DesktopPlatformSpecific();
        desktopSpecific.setHasSound(parameters.hasSound());

        System.out.println("Setting up display as " + parameters.getWidth() + "x" + parameters.getHeight() + " @ " + parameters.getX() + ", " + parameters.getY());
        System.out.println(parameters.hasSound() ? "Set sound on" : "No sound will be loaded or played");

        run(parameters, desktopSpecific);
    }

    public static void run(Parameters parameters, PlatformSpecific desktopSpecific) {

        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.x = parameters.getX();
        config.y = parameters.getY();
        config.width = parameters.getWidth();
        config.height = parameters.getHeight();
        config.fullscreen = parameters.isFullScreen();

        new LwjglApplication(new MainGame(desktopSpecific), config);
    }
}
