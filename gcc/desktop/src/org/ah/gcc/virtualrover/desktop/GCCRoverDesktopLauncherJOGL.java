package org.ah.gcc.virtualrover.desktop;

//import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
//import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import org.ah.gcc.virtualrover.MainGame;
import org.ah.gcc.virtualrover.PlatformSpecific;
import com.badlogic.gdx.backends.jogamp.JoglNewtApplication;
import com.badlogic.gdx.backends.jogamp.JoglNewtApplicationConfiguration;

public class GCCRoverDesktopLauncherJOGL {

    public static PlatformSpecific platformSpecific;

    public static void main(String[] args) {
        //
        // $ sudo modprobe snd_pcm_oss
        //
        // To get /dev/dsp
        //
        //
        //
        //
        //
        //
        //

//        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        // config.width = 320;
        // config.height = 256;

//        config.width = 1440;
//        config.height = 960;
//        new LwjglApplication(new MainGame(new DesktopPlatformSpecific()), config);

        int width = 1440;
        int height = 960;

        if (args.length > 0) {
            int i = args[0].indexOf("x");
            if (i > 0) {
                width = Integer.parseInt(args[0].substring(0, i));
                height = Integer.parseInt(args[0].substring(i + 1));
            }
        }

        System.out.println("Setting up display as " + width + "x" + height);
        System.out.println("Use <width>x<height> as parameter to change it");

        JoglNewtApplicationConfiguration config = new JoglNewtApplicationConfiguration();
        config.useGL30 = true;
        config.width = width;
        config.height = height;
        config.x = 0;
        config.y = 0;
        new JoglNewtApplication(new MainGame(new DesktopPlatformSpecific()), config);

    }
}
