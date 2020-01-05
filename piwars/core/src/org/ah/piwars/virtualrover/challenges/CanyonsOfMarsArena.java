package org.ah.piwars.virtualrover.challenges;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap;

import org.ah.piwars.virtualrover.ModelFactory;
import org.ah.piwars.virtualrover.VisibleObject;

import static org.ah.piwars.virtualrover.MainGame.SCALE;

public class CanyonsOfMarsArena extends AbstractChallenge {

    private Model floorModel;
    private ModelInstance floorModelInstance;
    private Material floorMaterial;

    private Material wallMaterial;
    private Material alienMaterial;
    private Model verticalWallModel;
    private Model horizontalWallModel;
    private Model alienPosterModel;
    private Array<ModelInstance> walls = new Array<>();

    public CanyonsOfMarsArena(ModelFactory modelFactory, AssetManager assetManager) {
        super(modelFactory);

        int attrs = Usage.Position | Usage.ColorUnpacked  | Usage.TextureCoordinates | Usage.Normal;
        floorMaterial = new Material(ColorAttribute.createDiffuse(new Color(0.3f, 0.3f, 0.3f, 1f)));
        wallMaterial = new Material(ColorAttribute.createDiffuse(new Color(0.8f, 0.0f, 0.0f, 1f)));

        alienMaterial = new Material(
                TextureAttribute.createDiffuse(assetManager.get("3d/Alien.png", Texture.class)),
                new BlendingAttribute());

        ModelBuilder modelBuilder = new ModelBuilder();
        floorModel = modelBuilder.createBox(3400, 10, 1830, floorMaterial, attrs);

        floorModelInstance = new ModelInstance(floorModel);
        floorModelInstance.transform.setToTranslationAndScaling(0, -59f * SCALE, 0, SCALE, SCALE, SCALE);

        verticalWallModel = modelBuilder.createBox(10, 200, 610, wallMaterial, attrs);
        horizontalWallModel = modelBuilder.createBox(680, 200, 10, wallMaterial, attrs);
        alienPosterModel = modelBuilder.createRect(0, -60, -60, 0, -60, 60, 0, 60, 60, 0, 60, -60, 0, 0, 1, alienMaterial, attrs);


        addVerticalWall(1695, -610);
        addVerticalWall(1695, 0);
        addVerticalWall(1695, 610);
        addHorizontalWall(1360, 910);
        addHorizontalAlien(1360, 910, true);
        addHorizontalWall(680, 910);
        addHorizontalWall(0, 910);
        addHorizontalWall(-680, 910);
        addHorizontalAlien(-680, 910, true);
        addHorizontalWall(-1360, 910);
        addVerticalWall(-1695, 610);
        addVerticalAlien(-1695, 610, true);
        addVerticalWall(-1695, 0);
        addVerticalWall(-1695, -610);
        addHorizontalWall(-1360, -910);
        addHorizontalAlien(-1360, -910, false);
        addHorizontalWall(-680, -910);
        addHorizontalWall(0, -910);

        addVerticalWall(1010, -610);
        addVerticalAlien(1010, -610, false);
        addVerticalWall(1010, 0);
        addHorizontalWall(680, 305);
        addVerticalWall(340, 0);
        addHorizontalWall(0, -305);
        addHorizontalAlien(0, -305, false);
        addHorizontalWall(-680, -305);
        addVerticalWall(-1010, 0);
        addVerticalAlien(-1010, 0, true);

        addVerticalWall(-340, 610);
        addVerticalAlien(-340, 610, true);
    }

    private void addVerticalWall(float x, float y) {
        ModelInstance wall = new ModelInstance(verticalWallModel);
        wall.transform.setToTranslationAndScaling(x * SCALE, (100 - 59) * SCALE, - y * SCALE, SCALE, SCALE, SCALE);
        walls.add(wall);
    }

    private void addHorizontalWall(float x, float y) {
        ModelInstance wall = new ModelInstance(horizontalWallModel);
        wall.transform.setToTranslationAndScaling(x * SCALE, (100 - 59) * SCALE, - y * SCALE, SCALE, SCALE, SCALE);
        walls.add(wall);
    }

    private void addVerticalAlien(float x, float y, boolean left) {
        if (left) {
            x += 10;
        } else {
            x -= 10;
        }
        ModelInstance wall = new ModelInstance(alienPosterModel);
        wall.transform.setToTranslationAndScaling(x * SCALE, (100 - 59) * SCALE, - y * SCALE, SCALE, SCALE, SCALE);
        if (left) {
            wall.transform.rotate(0f, 1f, 0f, 180);
        }
        walls.add(wall);
    }

    private void addHorizontalAlien(float x, float y, boolean down) {
        if (down) {
            y -= 10;
        } else {
            y += 10;
        }
        ModelInstance wall = new ModelInstance(alienPosterModel);
        wall.transform.setToTranslationAndScaling(x * SCALE, (100 - 59) * SCALE, - y * SCALE, SCALE, SCALE, SCALE);
        if (down) {
            wall.transform.rotate(0f, 1f, 0f, 90f);
        } else {
            wall.transform.rotate(0f, 1f, 0f, -90f);
        }
        walls.add(wall);
    }

    @Override
    public void init() {
    }

    protected ModelInstance createChallengeModelInstance(ModelFactory modelFactory) {
        Model arenaModel = modelFactory.loadModel("arena.obj");

        ModelInstance arena = new ModelInstance(arenaModel);
        arena.transform.setToTranslationAndScaling(0, -70 * SCALE, 0, SCALE, SCALE, SCALE);
        arena.materials.get(0).set(ColorAttribute.createDiffuse(new Color(0.5f, 0.1f, 0.1f, 1f)));

        return arena;
    }

    @Override
    public void dispose() {
        alienPosterModel.dispose();
        floorModel.dispose();
        verticalWallModel.dispose();
        horizontalWallModel.dispose();
    }

    @Override
    protected void renderChallenge(ModelBatch batch, Environment environment, IntMap<VisibleObject> visibleObjects) {
        batch.render(floorModelInstance, environment);
        for (ModelInstance wall : walls) {
            batch.render(wall, environment);
        }
    }
}
