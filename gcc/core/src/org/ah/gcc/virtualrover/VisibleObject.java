package org.ah.gcc.virtualrover;

import com.badlogic.gdx.graphics.g2d.Batch;
import org.ah.themvsus.engine.common.game.GameObject;

public interface VisibleObject {

    void render(Batch batch);
    void update(GameObject gameObject);
}
