package org.ah.gcc.virtualrover.challenges;

import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;

public interface Challenge {

    void dispose();

    void update();

    void render(ModelBatch batch, Environment environment);

}
