package org.ah.gcc.virtualrover.desktop;

import org.ah.gcc.virtualrover.MainGame;
import org.ah.gcc.virtualrover.PlatformSpecific;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class GCCRoverDesktopLauncher {

    public static PlatformSpecific platformSpecific;

    public static void main(String[] args) {
        Parameters parameters = new Parameters();
        parameters.parseArgs(args);

        System.out.println("Setting up display as " + parameters.getWidth() + "x" + parameters.getHeight() + " @ " + parameters.getX() + ", " + parameters.getY());
        System.out.println(parameters.hasSound() ? "Set sound on" : "No sound will be loaded or played");

        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.x = parameters.getX();
        config.y = parameters.getY();
        config.width = parameters.getWidth();
        config.height = parameters.getHeight();

        DesktopPlatformSpecific desktopSpecific = new DesktopPlatformSpecific();
        desktopSpecific.setHasSound(parameters.hasSound());

        new LwjglApplication(new MainGame(desktopSpecific), config);
    }
}
