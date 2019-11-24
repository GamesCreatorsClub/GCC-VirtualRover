package org.ah.gcc.virtualrover.rovers;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;

import org.ah.gcc.virtualrover.rovers.attachments.Attachment;

public interface RoverModel {

    void render(ModelBatch batch, Environment environment);

    void update(Vector3 position, float orientation);

    Color getColour();

    Matrix4 getTransform();

    Matrix4 getPreviousTransform(); // TODO this is not quite right. Incorporate somehow in processInput

    void setId(int i);

    Attachment getAttachemnt();

    void setAttachment(Attachment attachment);

    void setColour(Color colour);
}
