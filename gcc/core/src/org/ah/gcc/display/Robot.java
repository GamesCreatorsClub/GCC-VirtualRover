package org.ah.gcc.display;

import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;

public interface Robot {
    public void processInput(Inputs i);
    public void render(ModelBatch batch, Environment environment);
    public void update();
}
