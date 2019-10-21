package org.ah.gcc.virtualrover;

import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;

import org.ah.themvsus.engine.common.game.GameObject;

public interface VisibleObject {

    void render(ModelBatch batch, Environment environment);
    void update(GameObject gameObject);
}
