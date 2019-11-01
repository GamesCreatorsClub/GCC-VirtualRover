package org.ah.gcc.virtualrover.challenges;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Attribute;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.IntMap;

import org.ah.gcc.virtualrover.ModelFactory;
import org.ah.gcc.virtualrover.VisibleObject;
import org.ah.gcc.virtualrover.game.GCCPlayer;
import org.ah.gcc.virtualrover.world.PlayerModel;

import java.util.ArrayList;
import java.util.List;

import static org.ah.gcc.virtualrover.MainGame.SCALE;
import static org.ah.gcc.virtualrover.utils.MeshUtils.createRect;
import static org.ah.gcc.virtualrover.utils.MeshUtils.polygonFromBoundingBox;
import static org.ah.gcc.virtualrover.utils.MeshUtils.polygonsOverlap;

public class PiNoonArena implements Challenge {

    private ModelInstance arena;

    private List<BoundingBox> boundingBoxes;
    private List<Polygon> polygons;

    protected OrthographicCamera floorCamera;

    private FrameBuffer frameBuffer;

    private Mesh floorMesh;

    private ShapeRenderer shapeRenderer;

    private Model floorModel;

    private ModelInstance floorModelInstance;

    public boolean showRovers = true;
    public boolean showPlan = true;
    public boolean showShadows = false;

    public PiNoonArena(ModelFactory modelFactory) {
        Model arenaModel = modelFactory.loadModel("arena.obj");

        arena = new ModelInstance(arenaModel);
        arena.transform.setToTranslationAndScaling(0, -70 * SCALE, 0, SCALE, SCALE, SCALE);
        arena.materials.get(0).set(ColorAttribute.createDiffuse(new Color(0.5f, 0.1f, 0.1f, 1f)));

        boundingBoxes = new ArrayList<BoundingBox>();
        float wallWidth = 0.1f;
        boundingBoxes.add(new BoundingBox(new Vector3(-1000 * SCALE, 100 * SCALE, -1000 * SCALE), new Vector3(1000 * SCALE, 100 * SCALE, (-1000 - wallWidth) * SCALE)));
        boundingBoxes.add(new BoundingBox(new Vector3(-1000 * SCALE, 100 * SCALE, 1000 * SCALE), new Vector3(1000 * SCALE, 100 * SCALE, (1000 + wallWidth) * SCALE)));

        boundingBoxes.add(new BoundingBox(new Vector3(-1000 * SCALE, 100 * SCALE, -1000 * SCALE), new Vector3((-1000 - wallWidth) * SCALE, 100 * SCALE, 1000 * SCALE)));
        boundingBoxes.add(new BoundingBox(new Vector3(1000 * SCALE, 100 * SCALE, -1000 * SCALE), new Vector3((1000 + wallWidth) * SCALE, 100 * SCALE, 1000 * SCALE)));

        polygons = new ArrayList<Polygon>();
        for (BoundingBox boundingBox : boundingBoxes) {
            Polygon polygon = polygonFromBoundingBox(boundingBox);
            polygons.add(polygon);
        }

        floorCamera = new OrthographicCamera(2000, 2000);
        floorCamera.update();

        int primitiveType = GL20.GL_TRIANGLES;
        int attrs = Usage.Position | Usage.ColorUnpacked | Usage.TextureCoordinates;

        frameBuffer = new FrameBuffer(Format.RGBA8888, 1024, 1024, false);
        shapeRenderer = new ShapeRenderer();
        shapeRenderer.setAutoShapeType(true);
        shapeRenderer.setProjectionMatrix(floorCamera.combined);

        floorMesh = createRect(0, 0, -59f * SCALE, 1000 * SCALE, 1000 * SCALE, new Color(1f, 1f, 1f, 1f));

        ModelBuilder modelBuilder = new ModelBuilder();
        modelBuilder.begin();
        modelBuilder.part("floor", primitiveType, attrs, new Material()).addMesh(floorMesh);
        floorModel = modelBuilder.end();
        floorModelInstance = new ModelInstance(floorModel);

        List<Attribute> attributesList = new ArrayList<Attribute>();
        attributesList.add(TextureAttribute.createDiffuse(frameBuffer.getColorBufferTexture()));
        Attribute[] attributes = attributesList.toArray(new Attribute[attributesList.size()]);
        floorModelInstance.materials.get(0).set(attributes);
    }

    @Override
    public boolean collides(List<Polygon> polygons) {
        return polygonsOverlap(this.polygons, polygons);
    }

    @Override
    public void dispose() {
        floorModel.dispose();
        floorMesh.dispose();
        frameBuffer.dispose();
        shapeRenderer.dispose();
    }

    @Override
    public void render(ModelBatch batch, Environment environment, IntMap<VisibleObject> visibleObjects) {
        if (showPlan) {
            frameBuffer.begin();
            Gdx.gl.glClearColor(1f, 1f, 1f, 1f);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

            shapeRenderer.begin();

            for (VisibleObject visibleObject : visibleObjects.values()) {
                if (visibleObject instanceof PlayerModel) {
                    PlayerModel playerModel = (PlayerModel)visibleObject;

                    GCCPlayer gccPlayer = playerModel.getGCCPlayer();

                    shapeRenderer.setColor(playerModel.colour);

                    for (Polygon polygon : gccPlayer.getCollisionPolygons()) {
                        shapeRenderer.polygon(polygon.getTransformedVertices());
                    }

                    int balloonBits = gccPlayer.getChallengeBits();
                    for (int i = 0; i < 3; i++) {
                        if ((balloonBits & 1 << i) != 0) {
                            Circle balloon = gccPlayer.getBalloon(i);
                            shapeRenderer.circle(balloon.x, balloon.y, balloon.radius);
                        }
                    }

                    Vector2 sharpEnd = gccPlayer.getSharpEnd();
                    shapeRenderer.circle(sharpEnd.x, sharpEnd.y, 5);
                }
            }

            shapeRenderer.end();
            frameBuffer.end();
        }

        batch.render(arena, environment);

        if (showPlan) { batch.render(floorModelInstance); }

        if (showRovers) {
            for (VisibleObject visibleObject : visibleObjects.values()) {
                visibleObject.render(batch, environment);
            }
        }
    }
}
