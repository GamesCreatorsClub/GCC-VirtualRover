package org.ah.gcc.virtualrover.world;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;

import org.ah.gcc.virtualrover.ModelFactory;
import org.ah.gcc.virtualrover.VisibleObject;
import org.ah.gcc.virtualrover.game.GCCGame;
import org.ah.gcc.virtualrover.game.GCCPlayer;
import org.ah.gcc.virtualrover.game.rovers.RoverType;
import org.ah.gcc.virtualrover.input.GCCPlayerInput;
import org.ah.gcc.virtualrover.rovers.CBiSRover;
import org.ah.gcc.virtualrover.rovers.GCCRover;
import org.ah.gcc.virtualrover.rovers.Rover;
import org.ah.gcc.virtualrover.rovers.attachments.PiNoonAttachment;
import org.ah.themvsus.engine.common.game.GameObject;

public class PlayerModel implements VisibleObject {
    public int id;
    public String name;
    public Color colour;
    public RoverType playerSelection = RoverType.GCC;
    public Rover rover;
    public GCCPlayerInput roverInputs = (GCCPlayerInput)GCCPlayerInput.INPUTS_FACTORY.obtain(); // TODO - is that OK? Why not set of inputs?
    public int playerScore = 0;
    public GCCGame game;

    public PlayerModel(GCCGame game, RoverType playerSelection, int id, String name, Color colour) {
        this.game = game;
        this.playerSelection = playerSelection;
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
            GCCPlayer gccPlayer = (GCCPlayer)game.getCurrentGameState().get(id);

            float bearing = gccPlayer.getBearing();
            Vector3 position = gccPlayer.getPosition();

//            Matrix4 transform = rover.getTransform();
//
////            transform.idt();
//            transform.setToTranslationAndScaling(position.x * SCALE, 0, position.y * SCALE, SCALE, SCALE, SCALE);
////            transform.translate(position.x * SCALE, 0, position.y * SCALE);
////            transform.scale(SCALE, SCALE, SCALE);
////            transform.translate(80f, 0, -55);
//            transform.rotate(new Vector3(0, 1, 0), 180 - bearing);
////            transform.translate(-80f, 0, 55f);
            rover.update(position.x, position.y, position.z, bearing);

            rover.render(batch, environment);
        }
    }

    @Override
    public void update(GameObject gameObject) {
    }

    public void setGamePlayerPositionAndOrientation(int x, int y, Quaternion orientation) {
        GCCPlayer gccPlayer = (GCCPlayer)game.getCurrentGameState().get(id);
        gccPlayer.setPosition(x, y);
        gccPlayer.setOrientation(orientation);
    }
}