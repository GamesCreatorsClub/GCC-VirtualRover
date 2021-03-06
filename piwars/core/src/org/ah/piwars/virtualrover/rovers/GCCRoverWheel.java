package org.ah.piwars.virtualrover.rovers;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.math.Vector3;

public class GCCRoverWheel extends AbstractWheel {

    private static final Vector3 WHEEL_ORIENTATION_AXIS = new Vector3(0f, 0f, 1f);
    private static final Vector3 WHEEL_ROTATION_AXIS = new Vector3(0f, 1f, 0f);

    private ModelInstance wheel;
    private ModelInstance motor;
    private ModelInstance tyre;

    public GCCRoverWheel(Model motorHolderModel, Model wheelModel, Model tyreModel, Color colour, float positionX, float positionY, float positionZ, float degreeOffset) {
        super(positionX, positionY, positionZ, degreeOffset);

        motor = new ModelInstance(motorHolderModel, 0, 0, 0);
        motor.materials.get(0).set(ColorAttribute.createDiffuse(colour));

        wheel = new ModelInstance(wheelModel, 0, 0, 0);
        wheel.materials.get(0).set(ColorAttribute.createDiffuse(colour));

        tyre = new ModelInstance(tyreModel, 0, 0, 0);
        tyre.materials.get(0).set(ColorAttribute.createDiffuse(Color.BLACK));
    }

    @Override
    protected void internalUpdate() {
        transform.rotate(WHEEL_ROTATION_AXIS, degreeOffset + degrees);

        wheel.transform.set(transform);
        motor.transform.set(transform);

        motor.transform.scale(25f, 25f, 25f);

        wheel.transform.rotate(WHEEL_ORIENTATION_AXIS, -90f);
        wheel.transform.translate(-4f, -4f, -10f);
        wheel.transform.rotate(WHEEL_ROTATION_AXIS, wheelangle);

        tyre.transform.set(wheel.transform);
    }

    @Override
    protected void render(ModelBatch batch, Environment environment) {
        batch.render(wheel, environment);
        batch.render(tyre, environment);
        batch.render(motor, environment);
    }

    @Override
    public void dispose() {
    }
}
