package org.ah.piwars.virtualrover.world;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;

import org.ah.piwars.virtualrover.ModelFactory;
import org.ah.piwars.virtualrover.VisibleObject;
import org.ah.piwars.virtualrover.game.PiWarsGame;
import org.ah.piwars.virtualrover.game.objects.BarrelObject;
import org.ah.piwars.virtualrover.game.objects.BarrelObject.BarrelColour;
import org.ah.piwars.virtualrover.game.rovers.Rover;

import static org.ah.piwars.virtualrover.MainGame.SCALE;

public class BarrelModelLink implements VisibleObject {

    private Vector3 UP = new Vector3(1f, 0f, 0f);

    public int id;
    public Color colour;
    public PiWarsGame game;
    public ModelInstance barrelModel;

    public BarrelModelLink(PiWarsGame game, int id, Color colour) {
        this.game = game;
        this.id = id;
        this.colour = colour;
    }

    public BarrelModelLink(PiWarsGame game, int id, BarrelColour barrelColour) {
        this(game, id, fromBarrelColour(barrelColour));
    }

    public void make(ModelFactory modelFactory) {
        barrelModel = new ModelInstance(modelFactory.getBarrel());
        barrelModel.transform.scale(SCALE, SCALE, SCALE).rotate(UP, 90).translate(0f, 0f, 20f);
        barrelModel.materials.get(0).set(ColorAttribute.createDiffuse(colour));
    }

    @Override
    public void render(ModelBatch batch, Environment environment) {
        if (game != null) {
            BarrelObject barrelObject = game.getCurrentGameState().get(id);

            Vector3 position = barrelObject.getPosition();

            barrelModel.transform.setToTranslationAndScaling(position.x * SCALE, 0, -position.y * SCALE, SCALE, SCALE, SCALE).rotate(UP, 90).translate(0f, 0f, 20f);
        }
        batch.render(barrelModel, environment);
    }

    public void setGamePlayerPositionAndOrientation(int x, int y, Quaternion orientation) {
        Rover rover = game.getCurrentGameState().get(id);
        rover.setPosition(x, y);
        rover.setOrientation(orientation);
    }

    public static Color fromBarrelColour(BarrelColour barrelColour) {
        if (barrelColour == BarrelColour.GREEN) {
            return Color.GREEN;
        } else if (barrelColour == BarrelColour.RED) {
            return Color.RED;
        }
        return Color.WHITE;
    }

    @Override
    public void dispose() {
    }
}
