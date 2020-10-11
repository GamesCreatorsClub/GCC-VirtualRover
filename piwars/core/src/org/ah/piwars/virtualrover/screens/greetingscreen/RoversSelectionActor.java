package org.ah.piwars.virtualrover.screens.greetingscreen;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import static org.ah.piwars.virtualrover.screens.GreetingScreen.ARROW_BUTTON_MARGIN;
import static org.ah.piwars.virtualrover.screens.GreetingScreen.ARROW_BUTTON_WIDTH;
import static org.ah.piwars.virtualrover.screens.GreetingScreen.DESCRIPTION_TEXT_MARGIN;

public class RoversSelectionActor extends Group {

    private RoverSelectionActor rover1Actor;
    private RoverSelectionActor rover2Actor;
    private Label selectRoverLabel;

    private boolean twoRoversChallenge;

    public RoversSelectionActor(
            Texture cornerTexture,
            ModelBatch modelBatch,
            Environment environment,
            AssetManager assetManager,
            Skin skin,
            int width, int height) {

        setSize(width, height);

        rover1Actor = new RoverSelectionActor(
                cornerTexture, modelBatch, environment,
                assetManager,
                skin,
                width, height);

        rover2Actor = new RoverSelectionActor(
                cornerTexture, modelBatch, environment,
                assetManager,
                skin,
                width, height);

        addActor(rover1Actor);
        addActor(rover2Actor);

        selectRoverLabel = new Label("Select Rover", skin);

        addActor(selectRoverLabel);

        rover1Actor.updateSelectedRover();
        rover2Actor.updateSelectedRover();
    }

    public void dispose() {
        rover1Actor.dispose();
        rover2Actor.dispose();
    }

     public void reset() {
        rover1Actor.reset();
        rover2Actor.reset();
    }

     public RoverSelectionActor getRover1() {
         return rover1Actor;
     }

     public RoverSelectionActor getRover2() {
         return rover2Actor;
     }

    public boolean isTwoRoversChallenge() {
        return twoRoversChallenge;
    }

    public void setTwoRoversChallenge(boolean twoRoversChallenge) {
        this.twoRoversChallenge = twoRoversChallenge;
        int margin = ARROW_BUTTON_WIDTH + ARROW_BUTTON_MARGIN + DESCRIPTION_TEXT_MARGIN;
        if (twoRoversChallenge) {
            rover1Actor.setVisible(true);
            rover2Actor.setVisible(true);
            rover1Actor.setPosition(-getWidth() * 0.7f, 0);
            rover2Actor.setPosition(getWidth() * 0.7f, 0);

            selectRoverLabel.setText("Select Rovers");
            selectRoverLabel.setPosition(rover1Actor.getX() -selectRoverLabel.getPrefWidth() - margin, (getHeight() - selectRoverLabel.getPrefHeight()) / 2);
        } else {
            rover1Actor.setVisible(true);
            rover2Actor.setVisible(false);
            rover1Actor.setPosition(0, 0);
            rover2Actor.setPosition(0, 0);

            rover1Actor.setPosition(0, 0);
            rover2Actor.setPosition(0, 0);

            selectRoverLabel.setText("Select Rover");
            selectRoverLabel.setPosition(-selectRoverLabel.getPrefWidth() - margin, (getHeight() - selectRoverLabel.getPrefHeight()) / 2);
        }
    }
}
