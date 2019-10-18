package org.ah.gcc.virtualrover.rovers;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Polygon;
import org.ah.gcc.virtualrover.Inputs;
import org.ah.gcc.virtualrover.rovers.attachments.Attachment;

import java.util.List;

public interface Rover {

    Matrix4 processInput(Inputs i);

    void render(ModelBatch batch, Environment environment, boolean hasBalloons);

    void update();

    Color getColour();

    Matrix4 getTransform();

    Matrix4 getPreviousTransform(); // TODO this is not quite right. Incorporate somehow in processInput

    void setId(int i);

    List<Polygon> getPolygons();

    Attachment getAttachemnt();

    void setAttachment(Attachment attachment);
}
