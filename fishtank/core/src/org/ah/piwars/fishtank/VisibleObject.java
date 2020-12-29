package org.ah.piwars.fishtank;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;

import org.ah.themvsus.engine.common.game.GameObject;

public interface VisibleObject {

    <T extends GameObject> T getGameObject();

    Color getColour();

    void render(float delta, ModelBatch batch, Environment environment);

    void dispose();
}
