package org.ah.piwars.virtualrover.challenges;

import com.badlogic.gdx.utils.IntMap;

import org.ah.piwars.virtualrover.VisibleObject;
import org.ah.piwars.virtualrover.game.challenge.Challenge;
import org.ah.piwars.virtualrover.screens.RenderingContext;

public interface ChallengeArena {

    void init();

    void dispose();

    void render(RenderingContext renderingContext, IntMap<VisibleObject> visibleObjects);

    float getWidth();

    float getLength();

    IntMap<VisibleObject> defaultVisibleObjets();

    <T extends Challenge> T getChallenge();

    void setChallenge(Challenge challenge);
}
