package org.ah.piwars.virtualrover.screens.greetingscreen;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;

import org.ah.piwars.virtualrover.challenges.ChallengeArena;
import org.ah.piwars.virtualrover.screens.RenderingContext;

public class ChallengeActor extends ModelActor {

    private ChallengeArena challengeArena;

    public ChallengeActor(
            Texture cornerTexture,
            ModelBatch modelBatch,
            Environment environment,
            int width, int height) {
        super(cornerTexture, modelBatch, environment, width, height);
    }

    public void setChallengeArena(ChallengeArena challengeArena) {
        this.challengeArena = challengeArena;
    }

    @Override
    public void drawModel(RenderingContext renderingContext) {
        challengeArena.render(renderingContext, challengeArena.defaultVisibleObjets());
    }
}
