package org.ah.gcc.virtualrover.challenges;

import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.utils.IntMap;

import org.ah.gcc.virtualrover.VisibleObject;

public interface Challenge {

    void init();

    void dispose();

    void render(ModelBatch batch, Environment en, IntMap<VisibleObject> visibleObjects);
}
