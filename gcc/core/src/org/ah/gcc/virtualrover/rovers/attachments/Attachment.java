package org.ah.gcc.virtualrover.rovers.attachments;

import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.math.Matrix4;

public interface Attachment {

    void update(Matrix4 roverTransform);

    void render(ModelBatch batch, Environment environment);

}
