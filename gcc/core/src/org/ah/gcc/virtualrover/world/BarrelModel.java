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
import org.ah.gcc.virtualrover.game.GCCGame;
import org.ah.gcc.virtualrover.game.GCCPlayer;
import org.ah.themvsus.engine.common.game.GameObject;

import static org.ah.gcc.virtualrover.MainGame.SCALE;

public class BarrelModel implements VisibleObject {

    private Vector3 UP = new Vector3(1f, 0f, 0f);

    public int id;
    public Color colour;
    public GCCGame game;
    public ModelInstance barrel;

    public BarrelModel(GCCGame game, int id, Color colour) {
        this.game = game;
        this.id = id;
        this.colour = colour;
    }

    public void make(ModelFactory modelFactory) {
        barrel = new ModelInstance(modelFactory.getBarrel());
        barrel.transform.scale(SCALE, SCALE, SCALE).rotate(UP, 90).translate(0f, 0f, 20f);
        barrel.materials.get(0).set(ColorAttribute.createDiffuse(colour));
    }

    @Override
    public void render(ModelBatch batch, Environment environment) {
        batch.render(barrel, environment);
    }

    @Override
    public void update(GameObject gameObject) {
        System.out.println("Got object, id=" + gameObject.getId() + "; " + gameObject);
    }

    public void setGamePlayerPositionAndOrientation(int x, int y, Quaternion orientation) {
        GCCPlayer gccPlayer = (GCCPlayer)game.getCurrentGameState().get(id);
        gccPlayer.setPosition(x, y);
        gccPlayer.setOrientation(orientation);
    }
}
