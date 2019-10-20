package org.ah.gcc.virtualrover.world;

import com.badlogic.gdx.graphics.Color;
import org.ah.gcc.virtualrover.Inputs;
import org.ah.gcc.virtualrover.ModelFactory;
import org.ah.gcc.virtualrover.rovers.CBiSRover;
import org.ah.gcc.virtualrover.rovers.GCCRover;
import org.ah.gcc.virtualrover.rovers.Rover;
import org.ah.gcc.virtualrover.rovers.RoverType;
import org.ah.gcc.virtualrover.rovers.attachments.PiNoonAttachment;

public class Player {
    public int id;
    public String name;
    public Color colour;
    public RoverType playerSelection = RoverType.GCC;
    public Rover rover;
    public Inputs roverInputs = Inputs.create();
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
}
