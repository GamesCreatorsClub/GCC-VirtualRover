package org.ah.gcc.virtualrover.challenges;

import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.math.Polygon;

public interface Challenge {

    void dispose();

    boolean collides(Polygon poligon);

    void render(ModelBatch batch, Environment environment);

}
