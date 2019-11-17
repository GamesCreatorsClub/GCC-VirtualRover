package org.ah.gcc.virtualrover.game.challenge;

import org.ah.gcc.virtualrover.game.GCCGame;

public class Challenges {

    private Challenges() { }

    public static Challenge createChallenge(GCCGame game, String name) {
        if ("PiNoon".equals(name)) {
            return new PiNoonChallenge(game);
        }

        throw new IllegalArgumentException(name + " not known");
    }
}
