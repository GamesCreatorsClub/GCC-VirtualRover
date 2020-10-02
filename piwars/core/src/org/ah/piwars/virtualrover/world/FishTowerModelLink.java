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
import org.ah.piwars.virtualrover.game.objects.FishTowerObject;
import org.ah.piwars.virtualrover.game.rovers.Rover;
import org.ah.themvsus.engine.common.game.GameObject;

import static com.badlogic.gdx.math.MathUtils.PI;

import static org.ah.piwars.virtualrover.MainGame.SCALE;

public class FishTowerModelLink implements VisibleObject {

    public final Color FISH_TOWER_COLOUR = new Color(.8f, 0f, 0f, 1f);

    public int id;
    public PiWarsGame game;
    public ModelInstance fishTowerModelInstance;


    public FishTowerModelLink(PiWarsGame game, int id) {
        this.game = game;
        this.id = id;
    }

    public void make(AssetManager assetManager) {
        fishTowerModelInstance = new ModelInstance(assetManager.get("3d/challenges/feed-the-fish-tower.obj", Model.class));
        fishTowerModelInstance.transform.scale(SCALE, SCALE, SCALE);
        fishTowerModelInstance.materials.get(0).set(ColorAttribute.createDiffuse(FISH_TOWER_COLOUR));
    }

    @Override
    public void render(ModelBatch batch, Environment environment) {
        if (game != null) {
            FishTowerObject fishTowerObject = game.getCurrentGameState().get(id);

            Vector3 position = fishTowerObject.getPosition();

            float verticalOffset = +337f;

            fishTowerModelInstance.transform
                .setToTranslationAndScaling(position.x * SCALE, verticalOffset * SCALE, -position.y * SCALE, SCALE, SCALE, SCALE)
                .rotateRad(new Vector3(0, 1, 0), fishTowerObject.getBearingRad() + PI)
                // .translate(0f, -FishTowerObject.TOWER_WIDTH / 2f - FishTowerObject.TOWER_WIDTH, 0f);
                ;
        }
        batch.render(fishTowerModelInstance, environment);
    }

    public void setGamePlayerPositionAndOrientation(int x, int y, Quaternion orientation) {
        Rover rover = game.getCurrentGameState().get(id);
        rover.setPosition(x, y);
        rover.setOrientation(orientation);
    }

    @Override
    @SuppressWarnings("unchecked")
    public  <T extends GameObject> T getGameObject() {
        return (T)game.getCurrentGameState().get(id);
    }

    @Override
    public Color getColour() {
        return FISH_TOWER_COLOUR;
    }

    @Override
    public void dispose() {
    }
}
