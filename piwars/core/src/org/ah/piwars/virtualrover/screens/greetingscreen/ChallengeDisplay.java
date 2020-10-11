package org.ah.piwars.virtualrover.screens.greetingscreen;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import org.ah.piwars.virtualrover.challenges.ChallengeArena;
import org.ah.piwars.virtualrover.challenges.ChallengeDescription;
import org.ah.piwars.virtualrover.challenges.Challenges;
import org.ah.piwars.virtualrover.screens.RenderingContext;

public class ChallengeDisplay extends Local3DDisplay {

    private int currentlySelectedChallengeIndex;
    private ChallengeDescription currentlySelectedChallengeDescription;
    private ChallengeArena currentlySelectedChallengeArena;
    private Challenges challenges;

    public ChallengeDisplay(
            Texture cornerTexture,
            Stage stage, Skin skin,
            ModelBatch modelBatch, Environment environment,
            Challenges challenges,
            int width, int height) {
        super(cornerTexture, stage, skin, modelBatch, environment, width, height);
        this.challenges = challenges;

        currentlySelectedChallengeDescription = challenges.getAvailableChallenges().get(currentlySelectedChallengeIndex);
        currentlySelectedChallengeArena = challenges.getChallengeArena(currentlySelectedChallengeDescription.getName());

        modelWidth = currentlySelectedChallengeArena.getWidth();
        modelHeight = currentlySelectedChallengeArena.getLength();
    }

    public ChallengeArena getCurrentlySelectedChallengeArena() {
        return currentlySelectedChallengeArena;
    }

    public ChallengeDescription getCurrentlySelectedChallengeDescription() {
        return currentlySelectedChallengeDescription;
    }

    @Override
    public void reset() {
        currentlySelectedChallengeIndex = 0;
        currentlySelectedChallengeDescription = challenges.getAvailableChallenges().get(currentlySelectedChallengeIndex);
        currentlySelectedChallengeArena = challenges.getChallengeArena(currentlySelectedChallengeDescription.getName());

        updateCurrentlySelectedChallenge();
    }

    public void updateCurrentlySelectedChallenge() {
        if (currentlySelectedChallengeIndex < 0) {
            currentlySelectedChallengeIndex = challenges.getAvailableChallenges().size() - 1;
        } else if (currentlySelectedChallengeIndex >= challenges.getAvailableChallenges().size()) {
            currentlySelectedChallengeIndex = 0;
        }
        currentlySelectedChallengeDescription = challenges.getAvailableChallenges().get(currentlySelectedChallengeIndex);
        currentlySelectedChallengeArena = challenges.getChallengeArena(currentlySelectedChallengeDescription.getName());
        setModelDimensions(currentlySelectedChallengeArena.getWidth(), currentlySelectedChallengeArena.getLength());
    }

    @Override
    public void drawModel(RenderingContext renderingContext) {
        currentlySelectedChallengeArena.render(renderingContext, currentlySelectedChallengeArena.defaultVisibleObjets());
    }

    @Override
    public void previous() {
        currentlySelectedChallengeIndex = currentlySelectedChallengeIndex - 1;
        updateCurrentlySelectedChallenge();
    }

    @Override
    public void next() {
        currentlySelectedChallengeIndex = currentlySelectedChallengeIndex + 1;
        updateCurrentlySelectedChallenge();
    }
};
