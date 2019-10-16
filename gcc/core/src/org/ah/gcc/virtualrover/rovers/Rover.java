package org.ah.gcc.virtualrover.rovers;

import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import org.ah.gcc.virtualrover.Inputs;

public interface Rover {

    void processInput(Inputs i, Rover[] rovers);

    void render(ModelBatch batch, Environment environment, boolean hasBalloons);

    void update();

    Polygon getPolygon();

    Vector2 sharpPoint();

    Matrix4 getTransform();

    void setId(int i);

    void removeBalloons();

    void resetBalloons();

    int checkIfBalloonsPopped(Vector2 robotSharpPoint);
}
