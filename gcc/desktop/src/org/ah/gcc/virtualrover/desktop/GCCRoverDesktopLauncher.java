package org.ah.gcc.virtualrover.desktop;

import org.ah.gcc.virtualrover.MainGame;
import org.ah.gcc.virtualrover.PlatformSpecific;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class GCCRoverDesktopLauncher {

    public static PlatformSpecific platformSpecific;

    public static void main(String[] arg) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        // config.width = 320;
        // config.height = 256;

        config.width = 1440;
        config.height = 960;
        new LwjglApplication(new MainGame(), config);
    }
}
