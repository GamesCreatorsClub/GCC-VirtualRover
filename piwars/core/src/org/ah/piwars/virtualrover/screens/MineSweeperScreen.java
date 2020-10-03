package org.ah.piwars.virtualrover.screens;

import com.badlogic.gdx.assets.AssetManager;

import org.ah.piwars.virtualrover.MainGame;
import org.ah.piwars.virtualrover.PlatformSpecific;
import org.ah.piwars.virtualrover.ServerCommunicationAdapter;
import org.ah.piwars.virtualrover.utils.SoundManager;
import org.ah.piwars.virtualrover.view.Console;

public class MineSweeperScreen extends SingleRoverCameraChallengeScreen {

    public MineSweeperScreen(MainGame game,
            PlatformSpecific platformSpecific,
            AssetManager assetManager,
            SoundManager soundManager,
            ServerCommunicationAdapter serverCommunicationAdapter,
            Console console) {
        super(game, platformSpecific, assetManager, soundManager, serverCommunicationAdapter, console);
    }

    @Override
    protected void resetCameraPosition() {
        super.resetCameraPosition();
        cinematicCameraController.setCameraHeight(1300f);
        cinematicCameraController.setCameraRadius(1000f);
    }
}
