package org.ah.piwars.virtualrover.challenges;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap;

import org.ah.piwars.virtualrover.VisibleObject;
import org.ah.piwars.virtualrover.screens.RenderingContext;

import static org.ah.piwars.virtualrover.MainGame.SCALE;
import static org.ah.piwars.virtualrover.game.challenge.BlastOffChallenge.COURSE_LENGTH;
import static org.ah.piwars.virtualrover.game.challenge.BlastOffChallenge.COURSE_WIDTH;
import static org.ah.piwars.virtualrover.game.challenge.BlastOffChallenge.FLOOR_POLYGONS;
import static org.ah.piwars.virtualrover.game.challenge.BlastOffChallenge.LINE_POLYGONS;
import static org.ah.piwars.virtualrover.game.challenge.BlastOffChallenge.WALLS_POLYGONS;
import static org.ah.piwars.virtualrover.game.challenge.BlastOffChallenge.WALL_HEIGHT;
import static org.ah.piwars.virtualrover.utils.MeshUtils.extrudePolygonY;
import static org.ah.piwars.virtualrover.utils.MeshUtils.flatPolygon;

public class BlastOffArena extends AbstractChallenge {

    private Material floorMaterial;
    private Array<Model> floorModels = new Array<>();
    private Array<ModelInstance> floorInstances = new Array<>();

    private Material wallMaterial;
    private Array<Model> wallModels = new Array<>();
    private Array<ModelInstance> wallInstances = new Array<>();

    private Material lineMaterial;
    private Array<Model> lineModels = new Array<>();
    private Array<ModelInstance> lineInstances = new Array<>();

    public BlastOffArena(AssetManager assetManager) {
        super(assetManager);

        setDimensions(COURSE_WIDTH, COURSE_LENGTH);

        int attrs = Usage.Position | Usage.ColorUnpacked  | Usage.TextureCoordinates | Usage.Normal;
        floorMaterial = new Material(ColorAttribute.createDiffuse(new Color(0.2f, 0.2f, 0.2f, 1f)));
        wallMaterial = new Material(ColorAttribute.createDiffuse(new Color(0.2f, 0.2f, 0.2f, 1f)));
        lineMaterial = new Material(ColorAttribute.createDiffuse(new Color(0.9f, 0.9f, 0.9f, 1f)));

        ModelBuilder modelBuilder = new ModelBuilder();

        for (Polygon floorPolygon : FLOOR_POLYGONS) {
            Model floorModel = extrudePolygonY(modelBuilder, floorPolygon, 10, attrs, floorMaterial);
            ModelInstance floorModelInstance = new ModelInstance(floorModel);
            floorModelInstance.transform.setToTranslationAndScaling(0, (- 59) * SCALE, 0, SCALE, SCALE, SCALE);
            floorModels.add(floorModel);
            floorInstances.add(floorModelInstance);
        }

        for (Polygon wallPolygon : WALLS_POLYGONS) {
            Model wallModel = extrudePolygonY(modelBuilder, wallPolygon, WALL_HEIGHT, attrs, wallMaterial);
            ModelInstance wallInstance = new ModelInstance(wallModel);
            wallInstance.transform.setToTranslationAndScaling(0, (WALL_HEIGHT / 2 - 59) * SCALE, 0, SCALE, SCALE, SCALE);
            wallModels.add(wallModel);
            wallInstances.add(wallInstance);
        }

        for (Polygon linePolygon : LINE_POLYGONS) {
            Model lineModel = flatPolygon(modelBuilder, linePolygon, 0, attrs, lineMaterial);
            ModelInstance lineInstance = new ModelInstance(lineModel);
            lineInstance.transform.setToTranslationAndScaling(0, - 50 * SCALE, 0, SCALE, SCALE, SCALE);
            lineModels.add(lineModel);
            lineInstances.add(lineInstance);
        }
    }

    @Override
    public void init() {
    }

    @Override
    public void dispose() {
        for (Model floorModel : floorModels) {
            floorModel.dispose();
        }
        for (Model wallModel : wallModels) {
            wallModel.dispose();
        }
        for (Model lineModel : lineModels) {
            lineModel.dispose();
        }
    }

    @Override
    protected void renderChallenge(RenderingContext renderingContext, IntMap<VisibleObject> visibleObjects) {
        for (ModelInstance floor : floorInstances) {
            renderingContext.modelBatch.render(floor, renderingContext.environment);
        }
        for (ModelInstance wall : wallInstances) {
            renderingContext.modelBatch.render(wall, renderingContext.environment);
        }
        for (ModelInstance line : lineInstances) {
            renderingContext.modelBatch.render(line, renderingContext.environment);
        }
    }
}
