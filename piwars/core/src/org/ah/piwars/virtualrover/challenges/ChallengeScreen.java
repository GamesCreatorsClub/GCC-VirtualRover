package org.ah.piwars.virtualrover.challenges;

import com.badlogic.gdx.Screen;

public interface ChallengeScreen extends Screen {

    void reset();

    ChallengeArena getChallengeArena();

    void setChallengeArena(ChallengeArena challenge);

}
