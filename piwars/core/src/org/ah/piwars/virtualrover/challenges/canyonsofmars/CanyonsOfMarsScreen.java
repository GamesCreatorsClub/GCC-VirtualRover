package org.ah.piwars.virtualrover.challenges.canyonsofmars;

import com.badlogic.gdx.assets.AssetManager;

import org.ah.piwars.virtualrover.MainGame;
import org.ah.piwars.virtualrover.PlatformSpecific;
import org.ah.piwars.virtualrover.ServerCommunicationAdapter;
import org.ah.piwars.virtualrover.challenges.SingleRoverCameraChallengeScreen;
import org.ah.piwars.virtualrover.utils.SoundManager;
import org.ah.piwars.virtualrover.view.Console;

public class CanyonsOfMarsScreen extends SingleRoverCameraChallengeScreen {

    public CanyonsOfMarsScreen(MainGame game,
            PlatformSpecific platformSpecific,
            AssetManager assetManager,
            SoundManager soundManager,
            ServerCommunicationAdapter serverCommunicationAdapter,
            Console console) {
        super(game, platformSpecific, assetManager, soundManager, serverCommunicationAdapter, console);
    }
}
