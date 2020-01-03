package org.ah.piwars.virtualrover.backgrounds;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;

public interface Background {
    void render(Camera camera, ModelBatch batch, Environment environment);

    void dispose();
}
