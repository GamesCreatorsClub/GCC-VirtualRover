package org.ah.piwars.virtualrover.world;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;

import org.ah.piwars.virtualrover.VisibleObject;
import org.ah.piwars.virtualrover.game.PiWarsGame;
import org.ah.piwars.virtualrover.game.objects.ToyCubeObject;
import org.ah.piwars.virtualrover.game.objects.ToyCubeObject.ToyCubeColour;
import org.ah.piwars.virtualrover.game.rovers.Rover;

import static org.ah.piwars.virtualrover.MainGame.SCALE;

public class ToyCubeModelLink implements VisibleObject {

    public static final float HEIGHT_FUDGE_FACTOR = 5f;

    public int id;
    public Color colour;
    public PiWarsGame game;
    public ModelInstance kubeModelInstance;

    public ToyCubeModelLink(PiWarsGame game, int id, Color colour) {
        this.game = game;
        this.id = id;
        this.colour = colour;
    }

    public ToyCubeModelLink(PiWarsGame game, int id, ToyCubeColour toyKubeColour) {
        this(game, id, fromKubeColour(toyKubeColour));
    }

    public void make(AssetManager assetManager) {
        kubeModelInstance = new ModelInstance(assetManager.get("3d/challenges/toy-cube.obj", Model.class));
        kubeModelInstance.transform.scale(SCALE, SCALE, SCALE).translate(0f, 0f, -0f);
        kubeModelInstance.materials.get(0).set(ColorAttribute.createDiffuse(colour));
    }

    @Override
    public void render(ModelBatch batch, Environment environment) {
        if (game != null) {
            ToyCubeObject toyKubeObject = game.getCurrentGameState().get(id);

            Vector3 position = toyKubeObject.getPosition();

            kubeModelInstance.transform
                .setToTranslationAndScaling(position.x * SCALE, 0, -position.y * SCALE, SCALE, SCALE, SCALE)
                .translate(0f, -ToyCubeObject.KUBE_SIDE_LENGTH / 2f - HEIGHT_FUDGE_FACTOR, 0f);
//                .translate(0f, -20f, 0f);
        }
        batch.render(kubeModelInstance, environment);
    }

    public void setGamePlayerPositionAndOrientation(int x, int y, Quaternion orientation) {
        Rover rover = game.getCurrentGameState().get(id);
        rover.setPosition(x, y);
        rover.setOrientation(orientation);
    }

    public static Color fromKubeColour(ToyCubeColour kubeColour) {
        if (kubeColour == ToyCubeColour.GREEN) {
            return Color.GREEN;
        } else if (kubeColour == ToyCubeColour.RED) {
            return Color.RED;
        } else if (kubeColour == ToyCubeColour.BLUE) {
            return Color.BLUE;
        }
        return Color.WHITE;
    }

    @Override
    public void dispose() {
    }
}
