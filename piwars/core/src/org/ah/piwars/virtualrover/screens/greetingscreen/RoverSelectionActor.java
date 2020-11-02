package org.ah.piwars.virtualrover.screens.greetingscreen;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import org.ah.piwars.virtualrover.game.rovers.Rover;
import org.ah.piwars.virtualrover.game.rovers.RoverType;
import org.ah.piwars.virtualrover.rovers.AbstractRoverModel;
import org.ah.piwars.virtualrover.rovers.CBiSRoverModel;
import org.ah.piwars.virtualrover.rovers.GCCRoverModelM16;
import org.ah.piwars.virtualrover.rovers.GCCRoverModelM18;
import org.ah.piwars.virtualrover.rovers.MacFeegleModel;

import static org.ah.piwars.virtualrover.screens.GreetingScreen.ARROW_BUTTON_MARGIN;
import static org.ah.piwars.virtualrover.screens.GreetingScreen.ARROW_BUTTON_WIDTH;
import static org.ah.piwars.virtualrover.screens.GreetingScreen.LEFT_ARROW;
import static org.ah.piwars.virtualrover.screens.GreetingScreen.RIGHT_ARROW;

public class RoverSelectionActor extends Group {

    private Rover roverGameObject;

    private static RoverType[] ROVER_TYPE_VALUES = RoverType.values();

    private AssetManager assetManager;

    private int selectedRoverIndex;
    private RoverType selectedRoverType;

    private RoverActor roverActor;
    private Label roverDescriptionLabel;
    private Button leftButton;
    private Button rightButton;

    public RoverSelectionActor(
            Texture cornerTexture,
            ModelBatch modelBatch,
            Environment environment,
            AssetManager assetManager,
            Skin skin,
            int width, int height) {

        this.assetManager = assetManager;

        setSize(width, height);

        roverGameObject = new Rover(null, 0, RoverType.GCCM16) { };

        roverActor = new RoverActor(
                cornerTexture, modelBatch, environment,
                width, height);

        roverActor.setModelDimensions(210, 290);

        addActor(roverActor);

        roverDescriptionLabel = new Label("", skin);

        addActor(roverDescriptionLabel);

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
    }

    public void dispose() {
        roverActor.dispose();
    }

    @Override
    public void setPosition(float x, float y) {
        super.setPosition(x, y);
    }

    public void updateSelectedRover() {
        if (selectedRoverIndex < 0) {
            selectedRoverIndex = ROVER_TYPE_VALUES.length - 1;
        } else if (selectedRoverIndex >= ROVER_TYPE_VALUES.length) {
            selectedRoverIndex = 0;
        }
        selectedRoverType = ROVER_TYPE_VALUES[selectedRoverIndex];

        if (roverActor.getRoverModel() != null) {
            roverActor.getRoverModel().dispose();
        }

        AbstractRoverModel roverModel = null;
        if (selectedRoverType == RoverType.GCCM16) {
            roverModel = new GCCRoverModelM16(assetManager);
        } else if (selectedRoverType == RoverType.GCCM18) {
            roverModel = new GCCRoverModelM18(assetManager);
        } else if (selectedRoverType == RoverType.CBIS) {
            roverModel = new CBiSRoverModel(assetManager);
        } else if (selectedRoverType == RoverType.MacFeegle) {
            // TODO update this
            roverModel = new MacFeegleModel(assetManager);
        }
        roverModel.update(roverGameObject);
        roverDescriptionLabel.setText(selectedRoverType.getName());
        roverDescriptionLabel.setPosition((roverActor.getWidth() - roverDescriptionLabel.getPrefWidth()) / 2, - roverDescriptionLabel.getPrefHeight() * 1.5f);

        roverActor.setRoverModel(roverModel);
    }

    public RoverType getSelectedRoverType() {
        return selectedRoverType;
    }

     public void reset() {
        selectedRoverIndex = 0;

        updateSelectedRover();
    }

    public void previous() {
        selectedRoverIndex = selectedRoverIndex - 1;
        updateSelectedRover();
    }

    public void next() {
        selectedRoverIndex = selectedRoverIndex + 1;
        updateSelectedRover();
    }
}
