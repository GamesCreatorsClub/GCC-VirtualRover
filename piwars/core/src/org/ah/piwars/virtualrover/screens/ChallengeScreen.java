package org.ah.piwars.virtualrover.screens;

import com.badlogic.gdx.Screen;

import org.ah.piwars.virtualrover.challenges.ChallengeArena;

public interface ChallengeScreen extends Screen {

    void reset();

    ChallengeArena getChallengeArena();

    void setChallengeArena(ChallengeArena challenge);

}
