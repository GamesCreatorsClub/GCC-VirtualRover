package org.ah.piwars.virtualrover.game.challenge;

import org.ah.piwars.virtualrover.game.PiWarsGame;

public class Challenges {

    private Challenges() { }

    public static Challenge createChallenge(PiWarsGame game, String name) {
        if ("PiNoon".equals(name)) {
            return new PiNoonChallenge(game, name);
        } else if ("EcoDisaster".equals(name)) {
            return new EcoDisasterChallenge(game, name);
        }

        throw new IllegalArgumentException(name + " not known");
    }
}
