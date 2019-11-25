package org.ah.gcc.virtualrover.world;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
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
import org.ah.gcc.virtualrover.rovers.attachments.PiNoonAttachment;
import org.ah.themvsus.engine.common.game.GameObject;

public class PlayerModel implements VisibleObject {
    public int id;
    public String name;
    public Color colour;
    public RoverType playerSelection = RoverType.GCC;
    public RoverModel roverModel;
    public GCCPlayerInput roverInput = (GCCPlayerInput)GCCPlayerInput.INPUTS_FACTORY.obtain(); // TODO - is that OK? Why not set of inputs?
    public int playerScore = 0;
    public GCCGame game;
    private RoverColour roverColour;

    public PlayerModel(GCCGame game, RoverType playerSelection, int id, String name) {
        this.game = game;
        this.playerSelection = playerSelection;
        this.id = id;
        this.name = name;
    }

    public PlayerModel(GCCGame game, RoverType playerSelection, int id, String name, Color colour) {
        this(game, playerSelection, id, name);
        this.colour = colour;
    }

    public void makeRobot(ModelFactory modelFactory) {
        if (playerSelection == RoverType.CBIS) {
            roverModel = new CBiSRoverModel(name, modelFactory, colour);
        } else {
            roverModel = new GCCRoverModel(name, modelFactory, colour);
        }
        roverModel.setId(id);

        PiNoonAttachment piNoonAttachment = new PiNoonAttachment(modelFactory, colour);
        roverModel.setAttachment(piNoonAttachment);
        piNoonAttachment.removeBalloons();
    }

    public PiNoonAttachment getPiNoonAttachment() {
        if (roverModel != null) {
            return (PiNoonAttachment)roverModel.getAttachemnt();
        }
        return null;
    }

    @Override
    public void render(ModelBatch batch, Environment environment) {
        if (roverModel != null) {
            Rover rover = (Rover)game.getCurrentGameState().get(id);

            if (roverColour != null && rover.getRoverColour() != roverColour) {
                setRoverColour(rover.getRoverColour());
            }

            this.roverModel.update(rover);
            if (this.roverModel.getAttachemnt() != null) {
                this.roverModel.getAttachemnt().setAttachmentBits(rover.getChallengeBits());
            }

            this.roverModel.render(batch, environment);
        }
    }

    public void setRoverColour(RoverColour roverColour) {
        this.roverColour = roverColour;
        if (roverColour == Rover.RoverColour.WHITE) {
            colour = Color.WHITE;
        } else if (roverColour == Rover.RoverColour.GREEN) {
            colour = Color.GREEN;
        } else if (roverColour == Rover.RoverColour.BLUE) {
            colour = Color.BLUE;
        }
        if (roverModel != null) {
            roverModel.setColour(colour);
        }
    }

    @Override
    public void update(GameObject gameObject) {
        System.out.println("Got object, id=" + gameObject.getId() + "; " + gameObject);
    }

    public void setGamePlayerPositionAndOrientation(int x, int y, Quaternion orientation) {
        Rover gccPlayer = (Rover)game.getCurrentGameState().get(id);
        gccPlayer.setPosition(x, y);
        gccPlayer.setOrientation(orientation);
    }

    public Rover getGCCPlayer() {
        return (Rover)game.getCurrentGameState().get(id);
    }
}
