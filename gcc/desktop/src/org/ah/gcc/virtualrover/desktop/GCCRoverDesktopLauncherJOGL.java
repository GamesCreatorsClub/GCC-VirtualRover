package org.ah.gcc.virtualrover.desktop;

import org.ah.gcc.virtualrover.MainGame;
import org.ah.gcc.virtualrover.PlatformSpecific;
import com.badlogic.gdx.backends.jogamp.JoglNewtApplication;
import com.badlogic.gdx.backends.jogamp.JoglNewtApplicationConfiguration;

public class GCCRoverDesktopLauncherJOGL {

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

        System.out.println("Setting up display as " + parameters.getWidth() + "x" + parameters.getHeight() + " @ " + parameters.getX() + ", " + parameters.getY());
        System.out.println(parameters.hasSound() ? "Set sound on" : "No sound will be loaded or played");

        run(parameters, desktopSpecific);
    }

    public static void run(Parameters parameters, PlatformSpecific desktopSpecific) {
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
