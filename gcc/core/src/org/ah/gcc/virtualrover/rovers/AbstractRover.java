package org.ah.gcc.virtualrover.rovers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;

import org.ah.gcc.virtualrover.rovers.attachments.Attachment;

import java.util.NoSuchElementException;

import static org.ah.gcc.virtualrover.MainGame.SCALE;

public abstract class AbstractRover implements Rover {

    protected static float MIN_DISTANCE = 5 * SCALE;
    protected static float MAX_DISTANCE = 50 * SCALE;
    protected static float MIN_DISTANCE_SQUARED = MIN_DISTANCE * MIN_DISTANCE;
    protected static float MAX_DISTANCE_SQUARED = MAX_DISTANCE * MAX_DISTANCE;

    private int id = 0;

    private String name;
    private Color colour;

    protected Matrix4 transform = new Matrix4();
    protected Matrix4 previousTransform = new Matrix4();

    protected Attachment attachment;

    protected Vector3 pos = new Vector3();

    protected AbstractRover(String name, Color colour) throws NoSuchElementException {
        this.name = name;
        this.colour = colour;
    }

    public String getName() {
        return name;
    }

    @Override
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

    @Override
    public Attachment getAttachemnt() {
        return attachment;
    }

    @Override
    public void setAttachment(Attachment attachment) {
        this.attachment = attachment;
        attachment.update(transform);
    }

    protected void renderAttachment(ModelBatch batch, Environment environment) {
        if (attachment != null) {
            attachment.render(batch, environment);
        }
    }

    @Override
    public void update(Vector3 position, float headingDegs) {
        transform.getTranslation(pos);

        float distanceSquared = pos.dst2(position);
        if (distanceSquared < MIN_DISTANCE_SQUARED || distanceSquared > MAX_DISTANCE_SQUARED) {
            pos.set(position);
        } else {
            pos.lerp(position, 0.5f);
        }
        transform.setToTranslationAndScaling(position.x * SCALE, 0, -position.y * SCALE, SCALE, SCALE, SCALE);
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
