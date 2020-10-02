package org.ah.piwars.virtualrover.world;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;

import org.ah.piwars.virtualrover.VisibleObject;
import org.ah.piwars.virtualrover.game.PiWarsGame;
import org.ah.piwars.virtualrover.game.objects.GolfBallObject;
import org.ah.piwars.virtualrover.game.objects.ToyCubeObject.ToyCubeColour;
import org.ah.piwars.virtualrover.game.rovers.Rover;
import org.ah.themvsus.engine.common.game.GameObject;

import static com.badlogic.gdx.math.MathUtils.PI2;

import static org.ah.piwars.virtualrover.MainGame.SCALE;
import static org.ah.piwars.virtualrover.game.objects.GolfBallObject.GOLF_BALL_DIAMETER;

public class GolfBallModelLink implements VisibleObject {

    public static float CIRCUMFERENCE = GOLF_BALL_DIAMETER / MathUtils.PI;

    public final Color GOLF_BALL_COLOUR = Color.LIGHT_GRAY;

    private Vector3 FORWARD = new Vector3(0f, 0f, 1f);
    private Vector3 SIDE = new Vector3(1f, 0f, 0f);

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
        golfBallModelInstance = new ModelInstance(assetManager.get("3d/challenges/golfball.obj", Model.class));
        golfBallModelInstance.transform.scale(SCALE, SCALE, SCALE);
        golfBallModelInstance.materials.get(0).set(ColorAttribute.createDiffuse(GOLF_BALL_COLOUR));
    }

    private float positionToAngle(float position) {
        float angle = -position / CIRCUMFERENCE;
        while (angle < 0) {
            angle = angle + PI2;
        }
        while (angle > PI2) {
            angle = angle - PI2;
        }
        return angle;
    }

    @Override
    public void render(ModelBatch batch, Environment environment) {
        if (game != null) {
            GolfBallObject golfBallObject = game.getCurrentGameState().get(id);

            Vector3 position = golfBallObject.getPosition();

            golfBallModelInstance.transform
                .setToTranslationAndScaling(position.x * SCALE, -42f * SCALE, -position.y * SCALE, SCALE, SCALE, SCALE)
                .rotateRad(FORWARD, positionToAngle(position.x))
                .rotateRad(SIDE, positionToAngle(position.y))
                ;
        }
        batch.render(golfBallModelInstance, environment);
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
        return Color.LIGHT_GRAY;
    }

    @Override
    public void dispose() {
    }
}
