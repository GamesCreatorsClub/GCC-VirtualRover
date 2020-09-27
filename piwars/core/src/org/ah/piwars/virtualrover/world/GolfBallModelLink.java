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

public class GolfBallModelLink implements VisibleObject {

    public final Color GOLF_BALL_COLOUR = Color.LIGHT_GRAY;

    private Vector3 UP = new Vector3(1f, 0f, 0f);

    public int id;
    public PiWarsGame game;
    public ModelInstance golfBallModelInstance;

    public GolfBallModelLink(PiWarsGame game, int id) {
        this.game = game;
        this.id = id;
    }

    public GolfBallModelLink(PiWarsGame game, int id, ToyCubeColour toyKubeColour) {
        this(game, id);
    }

    public void make(AssetManager assetManager) {
        golfBallModelInstance = new ModelInstance(assetManager.get("3d/challenges/toy-kube.obj", Model.class));
        golfBallModelInstance.transform.scale(SCALE, SCALE, SCALE);
        golfBallModelInstance.materials.get(0).set(ColorAttribute.createDiffuse(GOLF_BALL_COLOUR));
    }

    @Override
    public void render(ModelBatch batch, Environment environment) {
        if (game != null) {
            ToyCubeObject toyKubeObject = game.getCurrentGameState().get(id);

            Vector3 position = toyKubeObject.getPosition();

            golfBallModelInstance.transform.setToTranslationAndScaling(position.x * SCALE, 0, -position.y * SCALE, SCALE, SCALE, SCALE).rotate(UP, 90).translate(0f, 0f, 20f);
        }
        batch.render(golfBallModelInstance, environment);
    }

    public void setGamePlayerPositionAndOrientation(int x, int y, Quaternion orientation) {
        Rover rover = game.getCurrentGameState().get(id);
        rover.setPosition(x, y);
        rover.setOrientation(orientation);
    }

    @Override
    public void dispose() {
    }
}
