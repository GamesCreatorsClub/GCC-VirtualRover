package org.ah.piwars.virtualrover.challenges;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;

import org.ah.piwars.virtualrover.MainGame;
import org.ah.piwars.virtualrover.PlatformSpecific;
import org.ah.piwars.virtualrover.ServerCommunicationAdapter;
import org.ah.piwars.virtualrover.game.PiWarsGame;
import org.ah.piwars.virtualrover.utils.SoundManager;
import org.ah.piwars.virtualrover.view.Console;

public class SingleRoverCameraChallengeScreen extends AbstractCameraChallengeScreen  {

    protected SingleRoverCameraChallengeScreen(MainGame game,
            PlatformSpecific platformSpecific,
            AssetManager assetManager,
            SoundManager soundManager,
            ServerCommunicationAdapter serverCommunicationAdapter,
            Console console) {

        super(game, platformSpecific, assetManager, soundManager,
                serverCommunicationAdapter, console);
    }

    private void startChallenge() {
        PiWarsGame game = serverCommunicationAdapter.getEngine().getGame();

        game.spawnRover(1, "Player", mainGameApp.getSelectedRover1());
        serverCommunicationAdapter.setLocalPlayerIds(1);
    }

    @Override
    public boolean keyDown(int keycode) {
        boolean res = super.keyDown(keycode);

        if (keycode == Input.Keys.SPACE && serverCommunicationAdapter.isLocal() && !serverCommunicationAdapter.hasPlayerOne()) {
            startChallenge();
        }

        return res;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (button == Input.Buttons.LEFT && serverCommunicationAdapter.isLocal() && !serverCommunicationAdapter.hasPlayerOne()) {
            startChallenge();
        }

        return super.touchUp(screenX, screenY, pointer, button);
    }
}
