package org.ah.gcc.virtualrover;

import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;

public interface Robot {
    public void processInput(Inputs i);
    public void render(ModelBatch batch, Environment environment);
    public void update();
    public Polygon getPolygon();
    public Vector2 sharpPoint();
}
