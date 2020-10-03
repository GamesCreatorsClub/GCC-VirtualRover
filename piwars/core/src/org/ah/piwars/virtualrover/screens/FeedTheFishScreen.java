package org.ah.piwars.virtualrover.screens;

import com.badlogic.gdx.assets.AssetManager;

import org.ah.piwars.virtualrover.MainGame;
import org.ah.piwars.virtualrover.PlatformSpecific;
import org.ah.piwars.virtualrover.ServerCommunicationAdapter;
import org.ah.piwars.virtualrover.utils.SoundManager;
import org.ah.piwars.virtualrover.view.Console;

public class FeedTheFishScreen extends SingleRoverCameraChallengeScreen {

    public FeedTheFishScreen(MainGame game,
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

        shadowLight.direction.set(0.5f, -1f, -0.5f);
        directionalLight.direction.set(0.5f, -1f, -0.5f);

        cinematicCameraController.setCameraHeight(800);
        cinematicCameraController.setCameraRadius(-800);
        cinematicCameraController.setFlipXPosition(true);
    }
}
