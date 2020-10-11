package org.ah.piwars.virtualrover.screens.greetingscreen;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;

import org.ah.piwars.virtualrover.rovers.AbstractRoverModel;
import org.ah.piwars.virtualrover.screens.RenderingContext;

public class RoverActor extends ModelActor {

    private AbstractRoverModel roverModel;

    public RoverActor(
            Texture cornerTexture,
            ModelBatch modelBatch,
            Environment environment,
            int width, int height) {
        super(cornerTexture, modelBatch, environment, width, height);

        modelWidth = 210;
        modelHeight = 290;
    }

    @Override
    public void dispose() {
        super.dispose();
        if (roverModel != null) {
            roverModel.dispose();
        }
    }

    public AbstractRoverModel getRoverModel() {
        return roverModel;
    }

    public void setRoverModel(AbstractRoverModel roverModel) {
        this.roverModel = roverModel;
    }

    @Override
    public void drawModel(RenderingContext renderingContext) {
        roverModel.render(renderingContext.modelBatch, renderingContext.environment);
    }
}
