package org.ah.gcc.display.desktop;

import org.ah.gcc.display.GCCRoverDisplay;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class GCCRoverDesktopLauncher {
    public static void main(String[] arg) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        // config.width = 320;
        // config.height = 256;

        config.width = 1440;
        config.height = 960;
        new LwjglApplication(new GCCRoverDisplay(), config);
    }
}
