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
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap;

import org.ah.piwars.virtualrover.ModelFactory;
import org.ah.piwars.virtualrover.VisibleObject;

import static org.ah.piwars.virtualrover.MainGame.SCALE;
import static org.ah.piwars.virtualrover.game.challenge.CanyonsOfMarsChallenge.CHALLENGE_HEIGHT;
import static org.ah.piwars.virtualrover.game.challenge.CanyonsOfMarsChallenge.CHALLENGE_WIDTH;
import static org.ah.piwars.virtualrover.game.challenge.CanyonsOfMarsChallenge.FLOOR_POLYGON;
import static org.ah.piwars.virtualrover.game.challenge.CanyonsOfMarsChallenge.WALL_POLYGONS;
import static org.ah.piwars.virtualrover.game.challenge.CanyonsOfMarsChallenge.WALL_HEIGHT;
import static org.ah.piwars.virtualrover.utils.MeshUtils.extrudePolygonY;

public class CanyonsOfMarsArena extends AbstractChallenge {

    private Model floorModel;
    private ModelInstance floorModelInstance;
    private Material floorMaterial;

    private Material wallMaterial;
    private Material alienMaterial;
    private Array<Model> wallModels = new Array<Model>();
    private Array<ModelInstance> wallInstances = new Array<>();
    private Model alienPosterModel;

    public CanyonsOfMarsArena(ModelFactory modelFactory, AssetManager assetManager) {
        super(modelFactory);

        setDimensions(CHALLENGE_WIDTH, CHALLENGE_HEIGHT);

        int attrs = Usage.Position | Usage.ColorUnpacked  | Usage.TextureCoordinates | Usage.Normal;
        floorMaterial = new Material(ColorAttribute.createDiffuse(new Color(0.3f, 0.3f, 0.3f, 1f)));
        wallMaterial = new Material(ColorAttribute.createDiffuse(new Color(0.8f, 0.0f, 0.0f, 1f)));

        alienMaterial = new Material(
                TextureAttribute.createDiffuse(assetManager.get("3d/Alien.png", Texture.class)),
                new BlendingAttribute());

        ModelBuilder modelBuilder = new ModelBuilder();
        floorModel = modelBuilder.createBox(3400, 10, 1830, floorMaterial, attrs);
        floorModel = extrudePolygonY(modelBuilder, FLOOR_POLYGON, 10, attrs, floorMaterial);

        floorModelInstance = new ModelInstance(floorModel);
        floorModelInstance.transform.setToTranslationAndScaling(0, -59f * SCALE, 0, SCALE, SCALE, SCALE);

        alienPosterModel = modelBuilder.createRect(0, -60, -60, 0, -60, 60, 0, 60, 60, 0, 60, -60, 0, 0, 1, alienMaterial, attrs);

        for (Polygon wallPolygon : WALL_POLYGONS) {
            Model wallModel = extrudePolygonY(modelBuilder, wallPolygon, WALL_HEIGHT, attrs, wallMaterial);
            ModelInstance wallInstance = new ModelInstance(wallModel);
            wallInstance.transform.setToTranslationAndScaling(0, (WALL_HEIGHT / 2 - 59) * SCALE, 0, SCALE, SCALE, SCALE);
            wallModels.add(wallModel);
            wallInstances.add(wallInstance);
        }

        addHorizontalAlien(1360, 915, true);
        addHorizontalAlien(-680, 915, true);
        addVerticalAlien(-1700, 610, true);
        addHorizontalAlien(-1360, -915, false);

        addVerticalAlien(1020, -610, false);
        addHorizontalAlien(0, -315, false);
        addVerticalAlien(-1020, 0, true);

        addVerticalAlien(-330, 610, true);
    }

    private void addVerticalAlien(float x, float y, boolean left) {
        if (left) {
            x += 11;
        } else {
            x -= 11;
        }
        ModelInstance wall = new ModelInstance(alienPosterModel);
        wall.transform.setToTranslationAndScaling(x * SCALE, (100 - 59) * SCALE, - y * SCALE, SCALE, SCALE, SCALE);
        if (left) {
            wall.transform.rotate(0f, 1f, 0f, 180);
        }
        wallInstances.add(wall);
    }

    private void addHorizontalAlien(float x, float y, boolean down) {
        if (down) {
            y -= 11;
        } else {
            y += 11;
        }
        ModelInstance wall = new ModelInstance(alienPosterModel);
        wall.transform.setToTranslationAndScaling(x * SCALE, (100 - 59) * SCALE, - y * SCALE, SCALE, SCALE, SCALE);
        if (down) {
            wall.transform.rotate(0f, 1f, 0f, 90f);
        } else {
            wall.transform.rotate(0f, 1f, 0f, -90f);
        }
        wallInstances.add(wall);
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
        for (Model wallModel : wallModels) {
            wallModel.dispose();
        }
    }

    @Override
    protected void renderChallenge(ModelBatch batch, Environment environment, IntMap<VisibleObject> visibleObjects) {
        batch.render(floorModelInstance, environment);
        for (ModelInstance wall : wallInstances) {
            batch.render(wall, environment);
        }
    }
}
