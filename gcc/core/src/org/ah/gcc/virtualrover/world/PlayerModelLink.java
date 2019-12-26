package org.ah.gcc.virtualrover.world;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;

import org.ah.gcc.virtualrover.ModelFactory;
import org.ah.gcc.virtualrover.VisibleObject;
import org.ah.gcc.virtualrover.game.GCCGame;
import org.ah.gcc.virtualrover.game.Rover;
import org.ah.gcc.virtualrover.game.Rover.RoverColour;
import org.ah.gcc.virtualrover.game.rovers.RoverType;
import org.ah.gcc.virtualrover.input.GCCPlayerInput;
import org.ah.gcc.virtualrover.rovers.CBiSRoverModel;
import org.ah.gcc.virtualrover.rovers.GCCRoverModel;
import org.ah.gcc.virtualrover.rovers.RoverModel;

public class PlayerModelLink implements VisibleObject {
    private int id;
    private String name;
    private Color colour;
    private RoverType playerSelection = RoverType.GCC;
    private RoverModel roverModel;
    private GCCGame game;
    private RoverColour roverColour = RoverColour.WHITE;

    public GCCPlayerInput roverInput = (GCCPlayerInput)GCCPlayerInput.INPUTS_FACTORY.obtain(); // TODO - is that OK? Why not set of inputs?

    public PlayerModelLink(GCCGame game, RoverType playerSelection, int id, String name) {
        this.game = game;
        this.playerSelection = playerSelection;
        this.id = id;
        this.name = name;
    }

    public void makeRobot(ModelFactory modelFactory) {
        if (playerSelection == RoverType.CBIS) {
            roverModel = new CBiSRoverModel(name, modelFactory, colour);
        } else {
            roverModel = new GCCRoverModel(name, modelFactory, colour);
        }
        roverModel.setId(id);
    }

    @Override
    public void render(ModelBatch batch, Environment environment) {
        if (roverModel != null) {
            Rover rover = game.getCurrentGameState().get(id);

            if (roverColour != null && rover.getRoverColour() != roverColour) {
                setRoverColour(rover);
            }

            this.roverModel.update(rover);

            this.roverModel.render(batch, environment);
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
        Rover gccPlayer = game.getCurrentGameState().get(id);
        gccPlayer.setPosition(x, y);
        gccPlayer.setOrientation(orientation);
    }

    public Rover getGCCPlayer() {
        return game.getCurrentGameState().get(id);
    }

    public Matrix4 getRoverTransform() {
        if (roverModel != null) {
            return roverModel.getTransform();
        }

        return null;
    }

    public Color getColour() {
        return colour;
    }
}
