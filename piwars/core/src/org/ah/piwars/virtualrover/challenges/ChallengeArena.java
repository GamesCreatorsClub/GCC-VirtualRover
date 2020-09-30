package org.ah.piwars.virtualrover.challenges;

import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.utils.IntMap;

import org.ah.piwars.virtualrover.VisibleObject;
import org.ah.piwars.virtualrover.game.challenge.Challenge;

public interface ChallengeArena {

    void init();

    void dispose();

    void render(ModelBatch batch, Environment en, FrameBuffer frameBuffer, IntMap<VisibleObject> visibleObjects);

    float getWidth();

    float getLength();

    IntMap<VisibleObject> defaultVisibleObjets();

    <T extends Challenge> T getChallenge();

    void setChallenge(Challenge challenge);
}
