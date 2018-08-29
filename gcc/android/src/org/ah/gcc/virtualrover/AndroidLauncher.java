package org.ah.gcc.virtualrover;

import android.os.Bundle;

import org.ah.gcc.virtualrover.GCCRoverDisplay;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

public class AndroidLauncher extends AndroidApplication {
	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		initialize(new GCCRoverDisplay(), config);
	}
}
