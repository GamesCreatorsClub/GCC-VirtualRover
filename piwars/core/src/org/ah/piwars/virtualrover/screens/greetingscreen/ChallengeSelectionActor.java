package org.ah.piwars.virtualrover.screens.greetingscreen;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent;

import org.ah.piwars.virtualrover.challenges.ChallengeArena;
import org.ah.piwars.virtualrover.challenges.ChallengeDescription;
import org.ah.piwars.virtualrover.challenges.Challenges;

import static org.ah.piwars.virtualrover.screens.GreetingScreen.ARROW_BUTTON_MARGIN;
import static org.ah.piwars.virtualrover.screens.GreetingScreen.ARROW_BUTTON_WIDTH;
import static org.ah.piwars.virtualrover.screens.GreetingScreen.DESCRIPTION_TEXT_MARGIN;
import static org.ah.piwars.virtualrover.screens.GreetingScreen.DOWN_ARROW;
import static org.ah.piwars.virtualrover.screens.GreetingScreen.LEFT_ARROW;
import static org.ah.piwars.virtualrover.screens.GreetingScreen.RIGHT_ARROW;

public class ChallengeSelectionActor extends Group {

    public static final ChangeChallengeEvent CHANGE_CHALLENGE_EVENT = new ChangeChallengeEvent();
    public static final SelectChallengeEvent SELECT_CHALLENGE_EVENT = new SelectChallengeEvent();

    private int selectedChallengeIndex;
    private ChallengeDescription selectedChallengeDescription;
    private ChallengeArena selectedChallengeArena;
    private Challenges challenges;
    private ChallengeActor challengeActor;
    private Label selectChallengeLabel;
    private Label challengeDescriptionLabel;
    private Label challengeYearLabel;
    private boolean focused = false;
    private Button leftButton;
    private Button rightButton;
    private Button selectChallengeButton;

    private boolean selecting;

    public ChallengeSelectionActor(
            Texture cornerTexture,
            ModelBatch modelBatch,
            Environment environment,
            Challenges challenges,
            Skin skin,
            int width, int height) {

        this.challenges = challenges;

        setSize(width, height);

        challengeActor = new ChallengeActor(
                cornerTexture, modelBatch, environment,
                width, height);

        addActor(challengeActor);

        selectChallengeLabel = new Label("Select Challenge", skin);
        challengeDescriptionLabel = new Label("", skin);
        challengeYearLabel = new Label("", skin);

        addActor(selectChallengeLabel);
        addActor(challengeDescriptionLabel);
        addActor(challengeYearLabel);

        leftButton = new PolygonButton(LEFT_ARROW.getVertices(), skin, "opaque");
        leftButton.setSize(ARROW_BUTTON_WIDTH, height);
        leftButton.setPosition(-ARROW_BUTTON_WIDTH - ARROW_BUTTON_MARGIN, 0);
        leftButton.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent event, Actor actor) {
                previous();
            }
        });

        rightButton = new PolygonButton(RIGHT_ARROW.getVertices(), skin, "opaque");
        rightButton.setSize(ARROW_BUTTON_WIDTH, height);
        rightButton.setPosition(width + ARROW_BUTTON_MARGIN, 0);
        rightButton.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent event, Actor actor) {
                next();
            }
        });

        addActor(leftButton);
        addActor(rightButton);

        selectChallengeButton = new PolygonButton(DOWN_ARROW.getVertices(), skin, "opaque"); // new TextButton("Select Challenge", skin);
        selectChallengeButton.setSize(selectChallengeButton.getPrefWidth() + ARROW_BUTTON_MARGIN * 4, selectChallengeButton.getPrefHeight() + ARROW_BUTTON_MARGIN * 2);
        selectChallengeButton.setPosition((width - selectChallengeButton.getWidth()) / 2, - selectChallengeButton.getHeight() * 1.5f);
        selectChallengeButton.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent event, Actor actor) {
                fire(SELECT_CHALLENGE_EVENT);
            }});

        addActor(selectChallengeButton);

        updateSelectedChallenge();
    }

    public void dispose() {
        challengeActor.dispose();
    }

    public void setSelecting(boolean selecting) {
        this.selecting = selecting;
        selectChallengeButton.setVisible(selecting);
    }

    public boolean isSelecting() {
        return selecting;
    }

    public boolean isFocused() {
        return focused;
    }

    public void setFocused(boolean focused) {
        this.focused = focused;
        selectChallengeLabel.setVisible(focused);
    }

    @Override
    public void setPosition(float x, float y) {
        super.setPosition(x, y);
        selectChallengeLabel.setPosition(leftButton.getX() -selectChallengeLabel.getPrefWidth() - DESCRIPTION_TEXT_MARGIN, (getHeight() - selectChallengeLabel.getPrefHeight()) / 2);
        String year = selectedChallengeDescription.getYear();
        if (year == null || "".equals(year)) {
            challengeDescriptionLabel.setPosition(rightButton.getX() + rightButton.getWidth() + DESCRIPTION_TEXT_MARGIN, (getHeight()) / 2);
        } else {
            challengeDescriptionLabel.setPosition(rightButton.getX() + rightButton.getWidth() + DESCRIPTION_TEXT_MARGIN, getHeight() / 2 + challengeDescriptionLabel.getPrefHeight() / 2);
        }
        challengeYearLabel.setPosition(rightButton.getX() + rightButton.getWidth() + DESCRIPTION_TEXT_MARGIN, getHeight() / 2 - challengeDescriptionLabel.getPrefHeight() / 2);
    }

    public ChallengeArena getSelectedChallengeArena() {
        return selectedChallengeArena;
    }

    public ChallengeDescription getSelectedChallengeDescription() {
        return selectedChallengeDescription;
    }

    private void updateSelectedChallenge() {
        if (selectedChallengeIndex < 0) {
            selectedChallengeIndex = challenges.getAvailableChallenges().size() - 1;
        } else if (selectedChallengeIndex >= challenges.getAvailableChallenges().size()) {
            selectedChallengeIndex = 0;
        }
        selectedChallengeDescription = challenges.getAvailableChallenges().get(selectedChallengeIndex);
        selectedChallengeArena = challenges.getChallengeArena(selectedChallengeDescription.getName());

        String year = selectedChallengeDescription.getYear();
        String description = selectedChallengeDescription.getDescription();
        if (year == null || "".equals(year)) {
            challengeDescriptionLabel.setText(description);
            challengeYearLabel.setVisible(false);
        } else {
            challengeDescriptionLabel.setText(description);
            challengeDescriptionLabel.setPosition(20, getHeight() / 2);
            challengeYearLabel.setVisible(true);
            challengeYearLabel.setText( "(" + year + ")");
        }

        challengeActor.setChallengeArena(selectedChallengeArena);
        challengeActor.setModelDimensions(selectedChallengeArena.getWidth(), selectedChallengeArena.getLength());
        setPosition(getX(), getY());
    }

    public void reset() {
        selectedChallengeIndex = 0;
        selectedChallengeDescription = challenges.getAvailableChallenges().get(selectedChallengeIndex);
        selectedChallengeArena = challenges.getChallengeArena(selectedChallengeDescription.getName());

        updateSelectedChallenge();
    }

    public void previous() {
        selectedChallengeIndex = selectedChallengeIndex - 1;
        updateSelectedChallenge();
        fire(CHANGE_CHALLENGE_EVENT);
    }

    public void next() {
        selectedChallengeIndex = selectedChallengeIndex + 1;
        updateSelectedChallenge();
        fire(CHANGE_CHALLENGE_EVENT);
    }

    public static class SelectChallengeEvent extends ChangeEvent {
    }

    public static class ChangeChallengeEvent extends ChangeEvent {
    }
}
