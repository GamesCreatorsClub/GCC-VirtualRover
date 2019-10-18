package org.ah.gcc.virtualrover.rovers;

import java.util.NoSuchElementException;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import org.ah.gcc.virtualrover.ModelFactory;
import org.ah.gcc.virtualrover.rovers.attachments.Attachment;
import org.ah.gcc.virtualrover.rovers.attachments.PiNoonAttachment;

public abstract class AbstractRover implements Rover {

    private int id = 0;

    private String name;
    private Color colour;

    protected Matrix4 transform = new Matrix4();
    protected Matrix4 previousTransform = new Matrix4();

    protected Attachment attachment;

    protected AbstractRover(String name, ModelFactory modelFactory, Color colour) throws NoSuchElementException {
        this.name = name;
        this.colour = colour;

        Color balloonTransparentColour = new Color(colour);
        balloonTransparentColour.a = 0.7f;
    }

    public String getName() {
        return name;
    }

    public Color getColour() {
        return colour;
    }

    @Override
    public Matrix4 getTransform() {
        return transform;
    }

    @Override
    public Matrix4 getPreviousTransform() {
        return previousTransform;
    }

    public int getId() {
        return id;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    public Attachment getAttachemnt() {
        return attachment;
    }

    public void setAttachment(Attachment attachment) {
        this.attachment = attachment;
        attachment.update(transform);
    }

    protected void renderBalloons(ModelBatch batch, Environment environment) {
        if (attachment != null) {
            attachment.render(batch, environment);
        }
    }

    public void update() {
        if (attachment != null) {
            attachment.update(transform);
        }
    }

    protected static float calcSpeedMillimetresInFrame(float speedMPS) {
        return speedMPS * Gdx.graphics.getDeltaTime() * 1000; // in millimetres
    }
}
