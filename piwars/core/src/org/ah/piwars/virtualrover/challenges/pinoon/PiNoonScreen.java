package org.ah.piwars.virtualrover.challenges.pinoon;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;

import org.ah.piwars.virtualrover.MainGame;
import org.ah.piwars.virtualrover.PlatformSpecific;
import org.ah.piwars.virtualrover.ServerCommunicationAdapter;
import org.ah.piwars.virtualrover.challenges.AbstractCameraChallengeScreen;
import org.ah.piwars.virtualrover.game.PiWarsGame;
import org.ah.piwars.virtualrover.utils.SoundManager;
import org.ah.piwars.virtualrover.view.Console;

public class PiNoonScreen extends AbstractCameraChallengeScreen {

    public PiNoonScreen(MainGame game,
            PlatformSpecific platformSpecific,
            AssetManager assetManager,
            SoundManager soundManager,
            ServerCommunicationAdapter serverCommunicationAdapter,
            Console console) {
        super(game, platformSpecific, assetManager, soundManager, serverCommunicationAdapter, console);
    }

    @Override
    protected void setupRequiredCameraCombination() {
        cameraCombination = CAMERA_COMBINATIONS[4];
    }

    @Override
    public boolean keyDown(int keycode) {
        super.keyDown(keycode);
        if (keycode == Input.Keys.SPACE && serverCommunicationAdapter.isLocal() && !serverCommunicationAdapter.hasPlayerOne() && !serverCommunicationAdapter.hasPlayerTwo()) {
            PiWarsGame game = serverCommunicationAdapter.getEngine().getGame();

            game.spawnRover(1, "Blue", mainGameApp.getSelectedRover1());
            game.spawnRover(2, "Green", mainGameApp.getSelectedRover2());
            serverCommunicationAdapter.setLocalPlayerIds(1, 2);
        }

        return false;
    }
}
