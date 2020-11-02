package org.ah.piwars.virtualrover.world;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;

import org.ah.piwars.virtualrover.VisibleObject;
import org.ah.piwars.virtualrover.game.PiWarsGame;
import org.ah.piwars.virtualrover.game.rovers.Rover;
import org.ah.piwars.virtualrover.game.rovers.Rover.RoverColour;
import org.ah.piwars.virtualrover.game.rovers.RoverType;
import org.ah.piwars.virtualrover.input.PiWarsPlayerInput;
import org.ah.piwars.virtualrover.rovers.CBiSRoverModel;
import org.ah.piwars.virtualrover.rovers.GCCRoverModelM16;
import org.ah.piwars.virtualrover.rovers.GCCRoverModelM18;
import org.ah.piwars.virtualrover.rovers.MacFeegleModel;
import org.ah.piwars.virtualrover.rovers.RoverModel;
import org.ah.themvsus.engine.common.game.GameObject;

public class PlayerModelLink implements VisibleObject {
    private int id;
    private String name;
    private Color colour;
    private RoverType playerSelection = RoverType.GCCM16;
    private RoverModel roverModel;
    private PiWarsGame game;
    private RoverColour roverColour = RoverColour.WHITE;

    public PiWarsPlayerInput roverInput = (PiWarsPlayerInput)PiWarsPlayerInput.INPUTS_FACTORY.obtain(); // TODO - is that OK? Why not set of inputs?

    public PlayerModelLink(PiWarsGame game, RoverType playerSelection, int id, String name) {
        this.game = game;
        this.playerSelection = playerSelection;
        this.id = id;
        this.name = name;
    }

    public void makeRobot(AssetManager assetManager) {
        if (playerSelection == RoverType.CBIS) {
            roverModel = new CBiSRoverModel(name, assetManager, colour);
        } else if (playerSelection == RoverType.MacFeegle) {
            roverModel = new MacFeegleModel(name, assetManager, colour);
        } else if (playerSelection == RoverType.GCCM18) {
            roverModel = new GCCRoverModelM18(name, assetManager, colour);
        } else {
            roverModel = new GCCRoverModelM16(name, assetManager, colour);
        }
        roverModel.setId(id);
    }

    @Override
    public void render(ModelBatch batch, Environment environment) {
        if (roverModel != null) {
            Rover rover = game.getCurrentGameState().get(id);
            if (rover != null) {

                if (roverColour != null && rover.getRoverColour() != roverColour) {
                    setRoverColour(rover);
                }

                this.roverModel.update(rover);

                this.roverModel.render(batch, environment);
            }
        }
    }

    public void setRoverColour(Rover rover) {
        this.roverColour = rover.getRoverColour();
        if (roverColour == Rover.RoverColour.WHITE) {
            colour = Color.WHITE;
        } else if (roverColour == Rover.RoverColour.GREEN) {
            colour = Color.GREEN;
        } else if (roverColour == Rover.RoverColour.BLUE) {
            colour = Color.BLUE;
        }
        if (roverModel != null) {
            roverModel.setColour(colour);

            if (rover.getAttachemntId() != 0) {
                PiNoonAttachmentModelLink attachment = game.getCurrentGameState().get(rover.getAttachemntId()).getLinkBack();
                attachment.setColour(colour);
            }
        }
    }

    public void setGamePlayerPositionAndOrientation(int x, int y, Quaternion orientation) {
        Rover rover = game.getCurrentGameState().get(id);
        rover.setPosition(x, y);
        rover.setOrientation(orientation);
    }

    @Override
    @SuppressWarnings("unchecked")
    public  <T extends GameObject> T getGameObject() {
        return (T)game.getCurrentGameState().get(id);
    }

    public Matrix4 getRoverTransform() {
        if (roverModel != null) {
            return roverModel.getTransform();
        }

        return null;
    }

    @Override
    public Color getColour() {
        return colour;
    }

    @Override
    public void dispose() {
        playerSelection = RoverType.GCCM16;
        roverModel.dispose();
        roverModel = null;
        game = null;
        roverColour = RoverColour.WHITE;
        roverInput.free();
    }
}
