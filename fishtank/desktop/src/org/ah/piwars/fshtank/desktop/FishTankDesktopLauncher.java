package org.ah.piwars.fshtank.desktop;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

import org.ah.piwars.fishtank.FishtankMain;

import java.util.Properties;

public class FishTankDesktopLauncher {
    public static void main (String[] arg) throws Exception {

        Properties serverConfig = new Properties();
        serverConfig.setProperty("udp.port", "7453");
        FishtankServer.init(serverConfig);

        DesktopPlatformSpecific platformSpecific = new DesktopPlatformSpecific();

        platformSpecific.setServerDetails("127.0.0.1", 7453);

        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setWindowedMode(1024, 768);
        config.setWindowPosition(10, 100);
        new Lwjgl3Application(new FishtankMain(platformSpecific), config);
    }
}
