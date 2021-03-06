package org.ah.piwars.virtualrover.rovers;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.math.Vector3;

public class BigWheel extends AbstractWheel {

    protected static final Vector3 WHEEL_ORIENTATION_AXIS = new Vector3(0f, 1f, 0f);
    protected static final Vector3 WHEEL_ROTATION_AXIS = new Vector3(0f, 0f, 1f);

    private ModelInstance wheel;
    private ModelInstance tyre;

    private float scale;

    public BigWheel(Model wheelModel, Model tyreModel, Color colour, float scale, float positionX, float positionY, float positionZ, float degreeOffset) {
        super(positionX, positionY, positionZ, degreeOffset);

        this.scale = scale;

        wheel = new ModelInstance(wheelModel, 0, 0, 0);
        wheel.materials.get(0).set(ColorAttribute.createDiffuse(colour));

        tyre = new ModelInstance(tyreModel, 0, 0, 0);
        tyre.materials.get(0).set(ColorAttribute.createDiffuse(Color.BLACK));
    }

    @Override
    public void internalUpdate() {
        transform.rotate(WHEEL_ORIENTATION_AXIS, degreeOffset + degrees);

        wheel.transform.set(transform);
        wheel.transform.scale(scale, scale, scale);

        wheel.transform.rotate(WHEEL_ORIENTATION_AXIS, 90f);
        wheel.transform.translate(.5f, 0f, 0);
        wheel.transform.rotate(WHEEL_ROTATION_AXIS, wheelangle * -15);

        tyre.transform.set(wheel.transform);
    }

    @Override
    public void render(ModelBatch batch, Environment environment) {
        batch.render(wheel, environment);
        batch.render(tyre, environment);
    }

    @Override
    public void dispose() {
    }
}
