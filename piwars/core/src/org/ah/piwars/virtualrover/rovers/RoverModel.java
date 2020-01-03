package org.ah.piwars.virtualrover.rovers;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.math.Matrix4;

import org.ah.piwars.virtualrover.game.rovers.Rover;

public interface RoverModel {

    void render(ModelBatch batch, Environment environment);

    void update(Rover rover);

    Color getColour();

    Matrix4 getTransform();

    Matrix4 getPreviousTransform(); // TODO this is not quite right. Incorporate somehow in processInput

    void setId(int i);

    void setColour(Color colour);
}
