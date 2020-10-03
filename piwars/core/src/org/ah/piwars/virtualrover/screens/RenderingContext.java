package org.ah.piwars.virtualrover.screens;

import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;

public class RenderingContext {
    public boolean showRovers = true;
    public boolean showShadows = true;
    public boolean showPlan = false;
    public boolean renderBackground = false;

    public Environment environment;
    public ModelBatch modelBatch;
    public FrameBuffer frameBuffer;

    public RenderingContext(ModelBatch modelBatch, Environment environment, FrameBuffer frameBuffer) {
        this.modelBatch = modelBatch;
        this.environment = environment;
        this.frameBuffer = frameBuffer;
    }
}
