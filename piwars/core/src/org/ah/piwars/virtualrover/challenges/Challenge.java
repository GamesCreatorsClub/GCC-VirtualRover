package org.ah.piwars.virtualrover.challenges;

import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.utils.IntMap;

import org.ah.piwars.virtualrover.VisibleObject;

public interface Challenge {

    void init();

    void dispose();

    void render(ModelBatch batch, Environment en, FrameBuffer frameBuffer, IntMap<VisibleObject> visibleObjects);
}
