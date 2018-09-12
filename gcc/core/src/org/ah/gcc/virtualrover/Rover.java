package org.ah.gcc.virtualrover;

import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;

public interface Rover {
    void processInput(Inputs i);

    void render(ModelBatch batch, Environment environment, boolean hasBalloons);

    void update();

    Polygon getPolygon();

    Vector2 sharpPoint();

    Matrix4 getTransform();

    void setId(int i);

    void addOtherRover(Rover rover2);

    boolean hasBallon1();

    boolean hasBallon2();

    boolean hasBallon3();

    void hasBallon1(boolean b);

    void hasBallon2(boolean b);

    void hasBallon3(boolean b);
}
