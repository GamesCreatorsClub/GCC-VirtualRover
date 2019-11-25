package org.ah.gcc.virtualrover;

import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;

public interface VisibleObject {

    void render(ModelBatch batch, Environment environment);
}
