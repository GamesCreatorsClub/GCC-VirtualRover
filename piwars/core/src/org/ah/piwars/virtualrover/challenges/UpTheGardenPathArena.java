package org.ah.piwars.virtualrover.challenges;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Shape2D;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap;

import org.ah.piwars.virtualrover.VisibleObject;
import org.ah.piwars.virtualrover.game.challenge.UpTheGardenPathChallenge;
import org.ah.piwars.virtualrover.game.rovers.Rover;
import org.ah.piwars.virtualrover.screens.RenderingContext;
import org.ah.piwars.virtualrover.world.PlayerModelLink;

import static com.badlogic.gdx.math.MathUtils.PI;

import static org.ah.piwars.virtualrover.MainGame.SCALE;
import static org.ah.piwars.virtualrover.game.challenge.UpTheGardenPathChallenge.CHALLENGE_WIDTH;
import static org.ah.piwars.virtualrover.game.challenge.UpTheGardenPathChallenge.WALL_HEIGHT;
import static org.ah.piwars.virtualrover.game.challenge.UpTheGardenPathChallenge.WALL_POLYGONS;
import static org.ah.piwars.virtualrover.utils.MeshUtils.extrudePolygonY;

public class UpTheGardenPathArena extends AbstractChallenge {

    private Model floorModel;
    private ModelInstance floorModelInstance;
    private Material floorMaterial;

    private Material wallMaterial;

    // private ModelInstance targetBoxModelInstance;

    private Array<Model> wallModels = new Array<Model>();
    private Array<ModelInstance> wallInstances = new Array<>();

    private IntMap<VisibleObject> localVisibleObjects = new IntMap<>();

    public UpTheGardenPathArena(AssetManager assetManager) {
        super(assetManager);

        setDimensions(CHALLENGE_WIDTH, CHALLENGE_WIDTH);
    }

    @Override
    public void init() {
        setDimensions(CHALLENGE_WIDTH, CHALLENGE_WIDTH);

        int attrs = Usage.Position | Usage.ColorUnpacked  | Usage.TextureCoordinates | Usage.Normal;
        floorMaterial = new Material(TextureAttribute.createDiffuse(assetManager.get("3d/upthegardenpath.png", Texture.class)));
        wallMaterial = new Material(ColorAttribute.createDiffuse(new Color(0.6f, 0.6f, 0.55f, 1f)));

        ModelBuilder modelBuilder = new ModelBuilder();
        floorModel = modelBuilder.createRect(
                CHALLENGE_WIDTH / 2, 0, CHALLENGE_WIDTH / 2,
                CHALLENGE_WIDTH / 2, 0, -CHALLENGE_WIDTH / 2,
                -CHALLENGE_WIDTH / 2, 0, -CHALLENGE_WIDTH / 2,
                -CHALLENGE_WIDTH / 2, 0, CHALLENGE_WIDTH / 2,
                0, 1, 0, floorMaterial, attrs);

        floorModelInstance = new ModelInstance(floorModel);
        floorModelInstance.transform.setToTranslationAndScaling(0, -59f * SCALE, 0, SCALE, SCALE, SCALE);
        floorModelInstance.transform.rotateRad(0f, 1f, 0f, -PI / 2f);

        for (Polygon wallPolygon : WALL_POLYGONS) {
            Model wallModel = extrudePolygonY(modelBuilder, wallPolygon, WALL_HEIGHT, attrs, wallMaterial);
            ModelInstance wallInstance = new ModelInstance(wallModel);
            wallInstance.transform.setToTranslationAndScaling(0, (WALL_HEIGHT / 2 - 59) * SCALE, 0, SCALE, SCALE, SCALE);
            wallModels.add(wallModel);
            wallInstances.add(wallInstance);
        }

        prepareDebugAssets();
    }

    @Override protected boolean debugVisibleObjects() { return true; }
    @Override protected int getChallengeWidth() { return (int)CHALLENGE_WIDTH; }
    @Override protected int getChallengeHeight() { return (int)CHALLENGE_WIDTH; }
    @Override protected float getFloorHeight() { return -55f; }

    @Override
    public void dispose() {
        floorModel.dispose();
        for (Model wallModel : wallModels) {
            wallModel.dispose();
        }
        for (VisibleObject localVisibleObject : localVisibleObjects.values()) {
            localVisibleObject.dispose();
        }
    }

    @Override
    public void render(RenderingContext renderingContext, IntMap<VisibleObject> visibleObjects) {

        IntMap<VisibleObject> newMap = new IntMap<VisibleObject>();
        newMap.putAll(visibleObjects);
        newMap.putAll(localVisibleObjects);

        super.render(renderingContext, newMap);

        if (renderingContext.showPlan) {
            renderingContext.modelBatch.render(debugFloorModelInstance);

            debugFrameBuffer.begin();
            debugShapeRenderer.begin();

            for (VisibleObject visibleObject : visibleObjects.values()) {
                if (visibleObject instanceof PlayerModelLink) {
                    PlayerModelLink playerModel = (PlayerModelLink) visibleObject;

                    Rover rover = playerModel.getGameObject();

                    Color colour = playerModel.getColour();
                    if (colour.equals(Color.WHITE)) {
                        colour = Color.BLACK;
                    }
                    debugShapeRenderer.setColor(colour);

                    for (Shape2D shape : rover.getCollisionPolygons()) {
                        if (shape instanceof Polygon) {
                            debugShapeRenderer.polygon(((Polygon) shape).getTransformedVertices());
                        }
                    }
                }
            }

            debugShapeRenderer.setColor(Color.PINK);
            float[] vertices = ((UpTheGardenPathChallenge)challenge).getPathLine().getTransformedVertices();
            for (int i = 0; i < vertices.length - 2; i = i + 2) {
                debugShapeRenderer.line(vertices[i], vertices[i + 1], vertices[i + 2], vertices[i + 3]);
            }

            debugShapeRenderer.end();
            debugFrameBuffer.end();
        }
    }

    @Override
    protected void renderChallenge(RenderingContext renderingContext, IntMap<VisibleObject> visibleObjects) {
        renderingContext.modelBatch.render(floorModelInstance, renderingContext.environment);
        for (ModelInstance wall : wallInstances) {
            renderingContext.modelBatch.render(wall, renderingContext.environment);
        }

        if (renderingContext.showPlan) { renderingContext.modelBatch.render(debugFloorModelInstance); }

    }
}
