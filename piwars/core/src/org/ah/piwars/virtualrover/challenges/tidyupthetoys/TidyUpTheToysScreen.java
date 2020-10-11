package org.ah.piwars.virtualrover.challenges.tidyupthetoys;

import com.badlogic.gdx.assets.AssetManager;

import org.ah.piwars.virtualrover.MainGame;
import org.ah.piwars.virtualrover.PlatformSpecific;
import org.ah.piwars.virtualrover.ServerCommunicationAdapter;
import org.ah.piwars.virtualrover.challenges.SingleRoverCameraChallengeScreen;
import org.ah.piwars.virtualrover.utils.SoundManager;
import org.ah.piwars.virtualrover.view.Console;

import static org.ah.piwars.virtualrover.MainGame.SCALE;

public class TidyUpTheToysScreen extends SingleRoverCameraChallengeScreen {

    public TidyUpTheToysScreen(MainGame game,
            PlatformSpecific platformSpecific,
            AssetManager assetManager,
            SoundManager soundManager,
            ServerCommunicationAdapter serverCommunicationAdapter,
            Console console) {
        super(game, platformSpecific, assetManager, soundManager, serverCommunicationAdapter, console);
    }

    @Override
    protected void setupRequiredCameraCombination() {
        cameraCombination = CAMERA_COMBINATIONS[3];
    }

    @Override
    public void resetCameraPosition() {
        super.resetCameraPosition();
        camera.position.set(300f * SCALE, 480f * SCALE, 300f * SCALE);
        camera.lookAt(0f, 0f, 0f);

        cinematicCameraController.setCameraHeight(600);
        cinematicCameraController.setCameraRadius(900);
    }
}
