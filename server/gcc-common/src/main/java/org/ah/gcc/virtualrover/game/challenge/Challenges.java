package org.ah.gcc.virtualrover.game.challenge;

public class Challenges {

    private Challenges() { }

    public static Challenge createChallenge(String name) {
        if ("PiNoon".equals(name)) {
            return new PiNoonChallenge();
        }

        throw new IllegalArgumentException(name + " not known");
    }
}
