package org.ah.gcc.virtualrover.challenges;

import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.utils.IntMap;

import org.ah.gcc.virtualrover.VisibleObject;

import java.util.List;

public interface Challenge {

    void init();

    void dispose();

    boolean collides(List<Polygon> polygons);

    void render(ModelBatch batch, Environment en, IntMap<VisibleObject> visibleObjects);

}
