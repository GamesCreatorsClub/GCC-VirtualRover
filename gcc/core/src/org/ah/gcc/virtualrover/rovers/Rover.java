package org.ah.gcc.virtualrover.rovers;

import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.collision.BoundingBox;
import org.ah.gcc.virtualrover.Inputs;

import java.util.List;

public interface Rover {

    Matrix4 processInput(Inputs i);

    void render(ModelBatch batch, Environment environment, boolean hasBalloons);

    void update();

    Polygon getPolygon();

    Vector2 sharpPoint();

    Matrix4 getTransform();

    Matrix4 getPreviousTransform();

    void setId(int i);

    void removeBalloons();

    void resetBalloons();

    int checkIfBalloonsPopped(Vector2 robotSharpPoint);
}
