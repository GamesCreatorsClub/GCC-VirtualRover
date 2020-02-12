package org.ah.piwars.virtualrover.game.challenge;

import org.ah.piwars.virtualrover.game.GameMessageObject;
import org.ah.piwars.virtualrover.game.PiWarsGame;

public abstract class TimedChallenge extends AbstractChallenge {

    protected TimedChallenge(PiWarsGame piwarsGame, String name) {
        super(piwarsGame, name);
    }

    protected void stopTimer() {
        GameMessageObject gameMessageObject = getGameMessage();
        if (gameMessageObject != null) {
            gameMessageObject.setHasTimer(true);
            gameMessageObject.setTimerStopped(true);
        }
    }

    protected void removeTimer() {
        GameMessageObject gameMessageObject = getGameMessage();
        if (gameMessageObject != null) {
            gameMessageObject.setHasTimer(false);
            gameMessageObject.setTimerStopped(true);
        }
    }

    protected void startTimer(int tens) {
        GameMessageObject gameMessageObject = getGameMessage();
        if (gameMessageObject != null) {
            gameMessageObject.setHasTimer(true);
            gameMessageObject.setTimerStopped(false);
            gameMessageObject.setTimerTens(tens, piwarsGame);
        }
    }

}
