package org.ah.gcc.virtualrover.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import org.ah.gcc.virtualrover.GCCVirtualRover;

public class DesktopLauncherLWJGL {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		new LwjglApplication(new GCCVirtualRover(), config);
	}
}
