package org.ah.gcc.virtualrover;

import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;

public interface Robot {
    public void processInput(Inputs i);
    public void render(ModelBatch batch, Environment environment);
    public void update();
    public Polygon getPolygon();
    public Vector2 sharpPoint();
    public Matrix4 getTransform();
    public void setId(int i);
    public void addOtherRover(Robot rover2);
    public void setDoingPiNoon(boolean b);
    public boolean hasBallon1();
    public boolean hasBallon2();
    public boolean hasBallon3();
    public void hasBallon1(boolean b);
    public void hasBallon2(boolean b);
    public void hasBallon3(boolean b);
}
