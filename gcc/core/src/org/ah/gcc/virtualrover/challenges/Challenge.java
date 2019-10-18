package org.ah.gcc.virtualrover.challenges;

import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.math.Polygon;

import java.util.List;

public interface Challenge {

    void dispose();

    boolean collides(List<Polygon> polygons);

    void render(ModelBatch batch, Environment environment);

}
