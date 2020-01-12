package org.ah.piwars.virtualrover.world;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.math.Quaternion;

import org.ah.piwars.virtualrover.ModelFactory;
import org.ah.piwars.virtualrover.VisibleObject;
import org.ah.piwars.virtualrover.game.PiWarsGame;
import org.ah.piwars.virtualrover.game.attachments.PiNoonAttachment;
import org.ah.piwars.virtualrover.game.rovers.Rover;
import org.ah.piwars.virtualrover.rovers.attachments.PiNoonAttachmentModel;

public class PiNoonAttachmentModelLink implements VisibleObject {
    public int id;
    public PlayerModelLink playerModel;
    public Color colour;
    public PiNoonAttachmentModel attachmentModel;
    public PiWarsGame game;

    public PiNoonAttachmentModelLink(PiWarsGame game, int id) {
        this.game = game;
        this.id = id;
    }

    public PiNoonAttachmentModelLink(PiWarsGame game, Color colour, PiNoonAttachment piNoonAttachment, PlayerModelLink playerModel) {
        this.game = game;
        this.colour = colour;
        this.id = piNoonAttachment.getId();
        this.playerModel = playerModel;
    }

    public void makeModel(ModelFactory modelFactory) {
        attachmentModel = new PiNoonAttachmentModel(modelFactory, playerModel.getColour());
    }

    public Color getColour() {
        return colour;
    }

    public void setColour(Color colour) {
        this.colour = colour;
        if (attachmentModel != null) {
            attachmentModel.setColour(colour);
        }
    }

    @Override
    public void render(ModelBatch batch, Environment environment) {
        PiNoonAttachment attachment = game.getCurrentGameState().get(id);
        if (attachment == null) {
            System.err.println("Cannot find attachemtn id " + id + " in " + game.getCurrentGameState());
        } else {
            attachmentModel.setBalloonBits(attachment.getBalloonBits());
            attachmentModel.update(playerModel.getRoverTransform());
            attachmentModel.render(batch, environment);
        }
    }

    public void setGamePlayerPositionAndOrientation(int x, int y, Quaternion orientation) {
        Rover rover = game.getCurrentGameState().get(id);
        rover.setPosition(x, y);
        rover.setOrientation(orientation);
    }

    public PiNoonAttachment getAttachmentGameObject() {
        return game.getCurrentGameState().get(id);
    }
}
