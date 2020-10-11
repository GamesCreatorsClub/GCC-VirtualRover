package org.ah.piwars.virtualrover.challenges.straightline;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.g3d.utils.shapebuilders.BoxShapeBuilder;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap;

import org.ah.piwars.virtualrover.VisibleObject;
import org.ah.piwars.virtualrover.challenges.AbstractChallenge;
import org.ah.piwars.virtualrover.screens.RenderingContext;

import static org.ah.piwars.virtualrover.MainGame.SCALE;
import static org.ah.piwars.virtualrover.game.challenge.StraightLineSpeedTestChallenge.CHICANES_POLYGONS;
import static org.ah.piwars.virtualrover.game.challenge.StraightLineSpeedTestChallenge.CHICANE_WIDTH;
import static org.ah.piwars.virtualrover.game.challenge.StraightLineSpeedTestChallenge.COURSE_LENGTH;
import static org.ah.piwars.virtualrover.game.challenge.StraightLineSpeedTestChallenge.COURSE_WIDTH;
import static org.ah.piwars.virtualrover.game.challenge.StraightLineSpeedTestChallenge.CUT_MODIFIER;
import static org.ah.piwars.virtualrover.game.challenge.StraightLineSpeedTestChallenge.FLOOR_POLYGON;
import static org.ah.piwars.virtualrover.game.challenge.StraightLineSpeedTestChallenge.WALL_HEIGHT;
import static org.ah.piwars.virtualrover.game.challenge.StraightLineSpeedTestChallenge.WALL_POLYGONS;
import static org.ah.piwars.virtualrover.utils.MeshUtils.extrudePolygonY;

public class StraightLineSpeedTestArena extends AbstractChallenge {

    private Model floorModel;
    private ModelInstance floorModelInstance;
    private Material floorMaterial;

    private Material wallMaterial;
    private Array<Model> wallModels = new Array<>();
    private Array<ModelInstance> wallInstances = new Array<>();

    private Material lineMaterial;
    private Model lineModel;
    private ModelInstance line;

    private Material chicaneMaterial;
    private Array<Model> chicaneModels = new Array<Model>();
    private Array<ModelInstance> chicaneInstances = new Array<ModelInstance>();

    public StraightLineSpeedTestArena(AssetManager assetManager) {
        super(assetManager);

        setDimensions(COURSE_WIDTH, COURSE_LENGTH);

        int attrs = Usage.Position | Usage.ColorUnpacked  | Usage.TextureCoordinates | Usage.Normal;
        floorMaterial = new Material(ColorAttribute.createDiffuse(new Color(0.2f, 0.2f, 0.2f, 1f)));
        wallMaterial = new Material(ColorAttribute.createDiffuse(new Color(0.2f, 0.2f, 0.2f, 1f)));
        lineMaterial = new Material(ColorAttribute.createDiffuse(new Color(0.9f, 0.9f, 0.9f, 1f)));
        chicaneMaterial = new Material(ColorAttribute.createDiffuse(new Color(0.8f, 0.0f, 0.0f, 1f)));

        ModelBuilder modelBuilder = new ModelBuilder();
        floorModel = extrudePolygonY(modelBuilder, FLOOR_POLYGON, 10, attrs, floorMaterial);

        floorModelInstance = new ModelInstance(floorModel);
        floorModelInstance.transform.setToTranslationAndScaling(0, -59f * SCALE, 0, SCALE, SCALE, SCALE);

        for (Polygon wallPolygon : WALL_POLYGONS) {
            Model wallModel = extrudePolygonY(modelBuilder, wallPolygon, WALL_HEIGHT, attrs, wallMaterial);
            ModelInstance wallInstance = new ModelInstance(wallModel);
            wallInstance.transform.setToTranslationAndScaling(0, (WALL_HEIGHT / 2 - 59) * SCALE, 0, SCALE, SCALE, SCALE);
            wallModels.add(wallModel);
            wallInstances.add(wallInstance);
        }

        lineModel = modelBuilder.createRect(-COURSE_LENGTH / 2, 0, -9.5f, -COURSE_LENGTH / 2, 0, 9.5f, COURSE_LENGTH / 2, 0, 5, COURSE_LENGTH / 2, 0, -5, 0f, 1f, 0f, lineMaterial, attrs);
        line = new ModelInstance(lineModel);
        line.transform.setToTranslationAndScaling(0, - 50 * SCALE, 0 * SCALE, SCALE, SCALE, SCALE);

        for (Polygon chicanePolygon : CHICANES_POLYGONS) {
            Model chicaneModel = extrudePolygonY(modelBuilder, chicanePolygon, CHICANE_WIDTH, attrs, chicaneMaterial);
            ModelInstance chicaneInstance = new ModelInstance(chicaneModel);
            chicaneInstance.transform.setToTranslationAndScaling(0, (CHICANE_WIDTH / 2 - 59) * SCALE, 0, SCALE, SCALE, SCALE);
            chicaneModels.add(chicaneModel);
            chicaneInstances.add(chicaneInstance);
        }
    }

    @Override
    public void init() {
    }

    @Override
    public void dispose() {
        floorModel.dispose();
        lineModel.dispose();
        for (Model chicaneModel : chicaneModels) {
            chicaneModel.dispose();
        }
        for (Model wallModel : wallModels) {
            wallModel.dispose();
        }
    }

    @Override
    protected void renderChallenge(RenderingContext renderingContext, IntMap<VisibleObject> visibleObjects) {
        renderingContext.modelBatch.render(floorModelInstance, renderingContext.environment);
        for (ModelInstance wall : wallInstances) {
            renderingContext.modelBatch.render(wall, renderingContext.environment);
        }
        for (ModelInstance chicane : chicaneInstances) {
            renderingContext.modelBatch.render(chicane, renderingContext.environment);
        }
        renderingContext.modelBatch.render(line, renderingContext.environment);
    }

    public static class ChicaneBShapeuilder extends BoxShapeBuilder {
        public static void build(MeshPartBuilder meshPartBuilder, int length) {
            float width = 38;
            float x = 0;
            float y = 0;
            float z = 0;
            final float hw = length * 0.5f;
            final float hh = width * 0.5f;
            final float hd = width * 0.5f;

            final float x0 = x - hw;
            final float y0 = y - hh;
            final float z0 = z - hd;
            final float x1 = x + hw;
            final float y1 = y + hh;
            final float z1 = z + hd;

            build(meshPartBuilder,
                    obtainV3().set(x0 - width * CUT_MODIFIER, y0, z0),
                    obtainV3().set(x0 - width * CUT_MODIFIER, y1, z0),
                    obtainV3().set(x1 + width * CUT_MODIFIER, y0, z0),
                    obtainV3().set(x1 + width * CUT_MODIFIER, y1, z0),
                    obtainV3().set(x0, y0, z1),
                    obtainV3().set(x0, y1, z1),
                    obtainV3().set(x1, y0, z1),
                    obtainV3().set(x1, y1, z1));
            freeAll();
        }
    }
}
