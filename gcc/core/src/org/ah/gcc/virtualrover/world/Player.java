package org.ah.gcc.virtualrover.world;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;

import org.ah.gcc.virtualrover.ModelFactory;
import org.ah.gcc.virtualrover.VisibleObject;
import org.ah.gcc.virtualrover.input.GCCPlayerInput;
import org.ah.gcc.virtualrover.rovers.CBiSRover;
import org.ah.gcc.virtualrover.rovers.GCCRover;
import org.ah.gcc.virtualrover.rovers.Rover;
import org.ah.gcc.virtualrover.rovers.RoverType;
import org.ah.gcc.virtualrover.rovers.attachments.PiNoonAttachment;
import org.ah.themvsus.engine.common.game.GameObject;

public class Player implements VisibleObject {
    public int id;
    public String name;
    public Color colour;
    public RoverType playerSelection = RoverType.GCC;
    public Rover rover;
    public GCCPlayerInput roverInputs = (GCCPlayerInput)GCCPlayerInput.INPUTS_FACTORY.obtain(); // TODO - is that OK? Why not set of inputs?
    public int playerScore = 0;

    public Player(RoverType gcc, int id, String name, Color colour) {
        this.playerSelection = gcc;
        this.id = id;
        this.name = name;
        this.colour = colour;
    }

    public void makeRobot(ModelFactory modelFactory) {
        if (playerSelection == RoverType.CBIS) {
            rover = new CBiSRover(name, modelFactory, colour);
        } else {
            rover = new GCCRover(name, modelFactory, colour);
        }
        rover.setId(id);

        PiNoonAttachment piNoonAttachment = new PiNoonAttachment(modelFactory, colour);
        rover.setAttachment(piNoonAttachment);
        piNoonAttachment.removeBalloons();
    }

    public PiNoonAttachment getPiNoonAttachment() {
        if (rover != null) {
            return (PiNoonAttachment)rover.getAttachemnt();
        }
        return null;
    }

    @Override
    public void render(ModelBatch batch, Environment environment) {
        if (rover != null) {
            rover.render(batch, environment);
        }
    }

    @Override
    public void update(GameObject gameObject) {
    }
}
