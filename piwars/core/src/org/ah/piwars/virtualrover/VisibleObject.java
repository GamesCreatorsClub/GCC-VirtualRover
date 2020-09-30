package org.ah.piwars.virtualrover;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;

import org.ah.themvsus.engine.common.game.GameObject;

public interface VisibleObject {

    <T extends GameObject> T getGameObject();

    Color getColour();

    void render(ModelBatch batch, Environment environment);

    void dispose();
}
