package org.ah.piwars.virtualrover.game.challenge;

import org.ah.piwars.virtualrover.game.PiWarsGame;

public class Challenges {

    private Challenges() { }

    public static Challenge createChallenge(PiWarsGame game, String name) {
        if ("PiNoon".equals(name)) {
            return new PiNoonChallenge(game, name);
        } else if ("EcoDisaster".equals(name)) {
            return new EcoDisasterChallenge(game, name);
        } else if ("CanyonsOfMars".equals(name)) {
            return new CanyonsOfMarsChallenge(game, name);
        } else if ("StraightLineSpeedTest".equals(name)) {
            return new StraightLineSpeedTestChallenge(game, name);
        } else if ("BlastOff".equals(name)) {
            return new BlastOffChallenge(game, name);
        } else if ("MineSweeper".equals(name)) {
            return new MineSweeperChallenge(game, name);
        }

        throw new IllegalArgumentException(name + " not known");
    }
}
