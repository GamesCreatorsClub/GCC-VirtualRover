package org.ah.gcc.virtualrover.world;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;

import org.ah.gcc.virtualrover.ModelFactory;
import org.ah.gcc.virtualrover.VisibleObject;
import org.ah.gcc.virtualrover.game.BarrelObject;
import org.ah.gcc.virtualrover.game.BarrelObject.BarrelColour;
import org.ah.gcc.virtualrover.game.GCCGame;
import org.ah.gcc.virtualrover.game.Rover;

import static org.ah.gcc.virtualrover.MainGame.SCALE;

public class BarrelModelLink implements VisibleObject {

    private Vector3 UP = new Vector3(1f, 0f, 0f);

    public int id;
    public Color colour;
    public GCCGame game;
    public ModelInstance barrelModel;

    public BarrelModelLink(GCCGame game, int id, Color colour) {
        this.game = game;
        this.id = id;
        this.colour = colour;
    }

    public BarrelModelLink(GCCGame game, int id, BarrelColour barrelColour) {
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
        Rover gccPlayer = game.getCurrentGameState().get(id);
        gccPlayer.setPosition(x, y);
        gccPlayer.setOrientation(orientation);
    }

    public static Color fromBarrelColour(BarrelColour barrelColour) {
        if (barrelColour == BarrelColour.GREEN) {
            return Color.GREEN;
        } else if (barrelColour == BarrelColour.RED) {
            return Color.RED;
        }
        return Color.WHITE;
    }
}
