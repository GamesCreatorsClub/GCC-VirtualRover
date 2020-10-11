package org.ah.piwars.virtualrover.challenges.straightline;

import com.badlogic.gdx.assets.AssetManager;

import org.ah.piwars.virtualrover.MainGame;
import org.ah.piwars.virtualrover.PlatformSpecific;
import org.ah.piwars.virtualrover.ServerCommunicationAdapter;
import org.ah.piwars.virtualrover.challenges.SingleRoverCameraChallengeScreen;
import org.ah.piwars.virtualrover.utils.SoundManager;
import org.ah.piwars.virtualrover.view.Console;

import static org.ah.piwars.virtualrover.MainGame.SCALE;

public class StraightLineSpeedTestScreen extends SingleRoverCameraChallengeScreen {

    public StraightLineSpeedTestScreen(MainGame game,
            PlatformSpecific platformSpecific,
            AssetManager assetManager,
            SoundManager soundManager,
            ServerCommunicationAdapter serverCommunicationAdapter,
            Console console) {
        super(game, platformSpecific, assetManager, soundManager, serverCommunicationAdapter, console);
    }

    @Override
    public void resetCameraPosition() {
        super.resetCameraPosition();
        camera.position.set(3900f * SCALE, 480f * SCALE, 300f * SCALE);
        camera.lookAt(3450f, 0f, 0f);
    }
}
