package org.ah.piwars.virtualrover.screens.greetingscreen;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import org.ah.piwars.virtualrover.game.rovers.Rover;
import org.ah.piwars.virtualrover.game.rovers.RoverType;
import org.ah.piwars.virtualrover.rovers.AbstractRoverModel;
import org.ah.piwars.virtualrover.rovers.CBiSRoverModel;
import org.ah.piwars.virtualrover.rovers.GCCRoverModelM16;
import org.ah.piwars.virtualrover.rovers.GCCRoverModelM18;
import org.ah.piwars.virtualrover.screens.RenderingContext;

public class RoverDisplay extends Local3DDisplay {

    private Rover roverGameObject;

    private static RoverType[] ROVER_TYPE_VALUES = RoverType.values();

    private AssetManager assetManager;

    private int currentlySelectedRoverIndex;
    private RoverType currentlySelectedRoverType;
    private AbstractRoverModel currentlySelectedRoverModel;

    public RoverDisplay(
            Texture cornerTexture,
            Stage stage, Skin skin,
            ModelBatch modelBatch, Environment environment,
            AssetManager assetManager,
            int width, int height) {
        super(cornerTexture, stage, skin, modelBatch, environment, width, height);

        this.assetManager = assetManager;

        this.modelWidth = 210;
        this.modelHeight = 290;

        roverGameObject = new Rover(null, 0, RoverType.GCCM16) { };
    }

    public void updateCurrentlySelectedRover() {
        if (currentlySelectedRoverIndex < 0) {
            currentlySelectedRoverIndex = ROVER_TYPE_VALUES.length - 1;
        } else if (currentlySelectedRoverIndex >= ROVER_TYPE_VALUES.length) {
            currentlySelectedRoverIndex = 0;
        }
        currentlySelectedRoverType = ROVER_TYPE_VALUES[currentlySelectedRoverIndex];
        if (currentlySelectedRoverModel != null) {
            currentlySelectedRoverModel.dispose();
        }
        if (currentlySelectedRoverType == RoverType.GCCM16) {
            currentlySelectedRoverModel = new GCCRoverModelM16(assetManager);
        } else if (currentlySelectedRoverType == RoverType.GCCM18) {
            currentlySelectedRoverModel = new GCCRoverModelM18(assetManager);
        } else if (currentlySelectedRoverType == RoverType.CBIS) {
            currentlySelectedRoverModel = new CBiSRoverModel(assetManager);
        }
        currentlySelectedRoverModel.update(roverGameObject);
    }

    public RoverType getCurrentlySelectedRoverType() {
        return currentlySelectedRoverType;
    }

    @Override
    public void drawModel(RenderingContext renderingContext) {
        currentlySelectedRoverModel.render(renderingContext.modelBatch, renderingContext.environment);
    }

    @Override
    public void previous() {
        currentlySelectedRoverIndex = currentlySelectedRoverIndex - 1;
        updateCurrentlySelectedRover();
    }

    @Override
    public void next() {
        currentlySelectedRoverIndex = currentlySelectedRoverIndex + 1;
        updateCurrentlySelectedRover();
    }

    @Override
    public void reset() {
    }
};
