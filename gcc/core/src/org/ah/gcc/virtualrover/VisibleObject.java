package org.ah.gcc.virtualrover;

import org.ah.themvsus.engine.common.game.GameObject;

import com.badlogic.gdx.graphics.g2d.Batch;

public interface VisibleObject {

    void render(Batch batch);
    void update(GameObject gameObject);
}
