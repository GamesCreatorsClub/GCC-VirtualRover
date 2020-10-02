package org.ah.piwars.virtualrover.challenges;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Camera;
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
import com.badlogic.gdx.graphics.g3d.environment.DirectionalShadowLight;
import com.badlogic.gdx.graphics.g3d.utils.DepthShaderProvider;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Shape2D;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.utils.IntMap;

import org.ah.piwars.virtualrover.VisibleObject;
import org.ah.piwars.virtualrover.game.PiWarsCollidableObject;
import org.ah.piwars.virtualrover.game.attachments.PiNoonAttachment;
import org.ah.piwars.virtualrover.game.challenge.Box2DPhysicalWorldSimulationChallenge;
import org.ah.piwars.virtualrover.game.challenge.Challenge;
import org.ah.piwars.virtualrover.game.rovers.Rover;
import org.ah.piwars.virtualrover.world.PiNoonAttachmentModelLink;
import org.ah.piwars.virtualrover.world.PlayerModelLink;

import java.util.ArrayList;
import java.util.List;

import static org.ah.piwars.virtualrover.MainGame.SCALE;
import static org.ah.piwars.virtualrover.utils.MeshUtils.createRect;

@SuppressWarnings("deprecation")
public abstract class AbstractChallenge implements ChallengeArena {

    protected ModelInstance challengeModelInstance;

    public boolean showRovers = true;
    public boolean showShadows = true;
    public boolean showPlan = false;

    protected ModelBatch modelBatch;
    protected ModelBatch shadowBatch;

    protected Environment shadowEnvironment;

    protected DirectionalShadowLight shadowLight;

    protected AssetManager assetManager;

    private IntMap<VisibleObject> defaultVisibleObjets = new IntMap<VisibleObject>();

    protected float width = 2200;
    protected float length = 2200;

    // public int shadowTextureSize = 8192;
    // public int shadowTextureSize = 2048;
    public int shadowTextureSize = 1280;

    protected Challenge challenge;

    protected ShapeRenderer debugShapeRenderer;

    protected FrameBuffer debugFrameBuffer;
    protected OrthographicCamera debugFloorCamera;
    protected Mesh debugFloorMesh;
    protected Model debugFloorModel;
    protected ModelInstance debugFloorModelInstance;

    protected OrthographicCamera debugBox2dCamera;
    protected Box2DDebugRenderer debugBox2dDebugRenderer;

    public AbstractChallenge(AssetManager assetManager) {
        this.assetManager = assetManager;

        shadowLight = new DirectionalShadowLight(shadowTextureSize, shadowTextureSize, 8.5f, 8.5f, 0.01f, 100f);
        // shadowLight = new DirectionalShadowLight(shadowTextureSize, shadowTextureSize, 15f, 15f, 0.01f, 100f);
        shadowLight.set(1f, 1f, 1f, new Vector3(-0.5f, -1f, 0.5f));
        shadowEnvironment = new Environment();
        shadowEnvironment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.6f, 0.6f, 0.6f, 1f));
        shadowEnvironment.add(shadowLight);
        shadowEnvironment.shadowMap = shadowLight;

        modelBatch = new ModelBatch();
        shadowBatch = new ModelBatch(new DepthShaderProvider());
    }

    protected boolean debugVisibleObjects() { return false; }
    protected boolean debugBox2D() { return false; }

    protected int getChallengeWidth() { return 1024; }
    protected int getChallengeHeight() { return 1024; }
    protected float getFloorHeight() { return -50f; }

    protected void prepareDebugAssets() {
        debugFrameBuffer = new FrameBuffer(Format.RGBA8888, 1024, 1024, false);

        debugFloorCamera = new OrthographicCamera(getChallengeWidth(), getChallengeHeight());
        debugFloorCamera.update();

        int primitiveType = GL20.GL_TRIANGLES;
        int attrs = Usage.Position | Usage.ColorUnpacked | Usage.TextureCoordinates;

        if (debugVisibleObjects()) {
            debugShapeRenderer = new ShapeRenderer();
            debugShapeRenderer.setAutoShapeType(true);
            debugShapeRenderer.setProjectionMatrix(debugFloorCamera.combined);
        }
        debugFloorMesh = createRect(0, 0, getFloorHeight() * SCALE, getChallengeWidth() * SCALE / 2, getChallengeHeight() * SCALE / 2, new Color(1f, 1f, 1f, 1f));

        ModelBuilder modelBuilder = new ModelBuilder();
        modelBuilder.begin();
        modelBuilder.part("floor", primitiveType, attrs, new Material()).addMesh(debugFloorMesh);
        debugFloorModel = modelBuilder.end();
        debugFloorModelInstance = new ModelInstance(debugFloorModel);

        List<Attribute> attributesList = new ArrayList<Attribute>();
        attributesList.add(TextureAttribute.createDiffuse(debugFrameBuffer.getColorBufferTexture()));
        Attribute[] attributes = attributesList.toArray(new Attribute[attributesList.size()]);
        debugFloorModelInstance.materials.get(0).set(attributes);

        if (debugBox2D()) {
            debugBox2dCamera = new OrthographicCamera(getChallengeWidth(), getChallengeHeight());
            debugBox2dDebugRenderer = new Box2DDebugRenderer(true, true, true, true, true, true);
        }
    }

    @Override
    public void init() {
        if (debugBox2D() || debugVisibleObjects()) {
            prepareDebugAssets();
        }
    }

    @Override
    public void dispose() {
        if (debugFrameBuffer != null) { debugFrameBuffer.dispose(); }
        if (debugFloorModel != null) { debugFloorModel.dispose(); }
        if (debugFloorMesh != null) { debugFloorMesh.dispose(); }
        if (debugShapeRenderer != null) { debugShapeRenderer.dispose(); }

        shadowBatch.dispose();
    }

    @Override
    public void render(ModelBatch batch, Environment environment, FrameBuffer frameBuffer, IntMap<VisibleObject> visibleObjects) {
        if (showShadows) {
            Camera cam = batch.getCamera();
            batch.end();
            if (frameBuffer != null) { frameBuffer.end(); }

            shadowLight.begin(Vector3.Zero, cam.direction);
            shadowBatch.begin(shadowLight.getCamera());

            renderChallenge(shadowBatch, environment, visibleObjects);
            renderVisibleObjects(shadowBatch, environment, visibleObjects);

            shadowBatch.end();
            shadowLight.end();

            if (frameBuffer != null) {
                frameBuffer.begin();
                Gdx.gl20.glEnable(GL20.GL_DEPTH_TEST);
//                Gdx.gl20.glDepthFunc(GL20.GL_LEQUAL);
//                Gdx.gl.glEnable(GL20.GL_POLYGON_OFFSET_FILL);
//                Gdx.gl20.glPolygonOffset(1.0f, 1.0f);
            }
            modelBatch.begin(cam);

            renderChallenge(modelBatch, shadowEnvironment, visibleObjects);
            renderVisibleObjects(modelBatch, shadowEnvironment, visibleObjects);

            modelBatch.end();
            batch.begin(cam);
        } else {
            // batch.render(challengeModelInstance, environment);

            renderChallenge(batch, environment, visibleObjects);
            renderVisibleObjects(batch, environment, visibleObjects);
        }
        if (showPlan && debugBox2D()) {
            debugFrameBuffer.begin();

            Gdx.gl.glClearColor(1f, 1f, 1f, 1f);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
            debugBox2dDebugRenderer.render(((Box2DPhysicalWorldSimulationChallenge)challenge).getBox2DPhysicalWorld().getWorld(), debugBox2dCamera.combined);

            debugFrameBuffer.end();
        }

        if (showPlan && debugVisibleObjects()) {
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
                } else if (visibleObject instanceof PiNoonAttachmentModelLink) {
                    PiNoonAttachmentModelLink attachmentModel = (PiNoonAttachmentModelLink) visibleObject;

                    PiNoonAttachment attachment = attachmentModel.getGameObject();
                    debugShapeRenderer.setColor(attachmentModel.getColour());

                    int balloonBits = attachment.getBalloonBits();
                    for (int i = 0; i < 3; i++) {
                        if ((balloonBits & 1 << i) != 0) {
                            Circle balloon = attachment.getBalloon(i);
                            debugShapeRenderer.circle(balloon.x, balloon.y, balloon.radius);
                        }
                    }

                    Vector2 sharpEndPos = attachment.getSharpEnd();
                    debugShapeRenderer.circle(sharpEndPos.x, sharpEndPos.y, 5);
                } else if (visibleObject.getGameObject() instanceof PiWarsCollidableObject) {
                    PiWarsCollidableObject collidableObject = (PiWarsCollidableObject) visibleObject.getGameObject();
                    Color colour = visibleObject.getColour();
                    if (colour.equals(Color.WHITE)) {
                        colour = Color.BLACK;
                    }
                    debugShapeRenderer.setColor(colour);

                    for (Shape2D shape : collidableObject.getCollisionPolygons()) {
                        if (shape instanceof Polygon) {
                            debugShapeRenderer.polygon(((Polygon) shape).getTransformedVertices());
                        } else if (shape instanceof Circle) {
                            Circle circle = (Circle) shape;
                            debugShapeRenderer.circle(circle.x, circle.y, circle.radius);
                        }
                    }
                }
            }
            debugShapeRenderer.end();
            debugFrameBuffer.end();
        }
    }

    protected void renderChallenge(ModelBatch batch, Environment environment, IntMap<VisibleObject> visibleObjects) {
        batch.render(challengeModelInstance, environment);
    }

    protected void renderVisibleObjects(ModelBatch batch, Environment environment, IntMap<VisibleObject> visibleObjects) {
        if (showRovers) {
            for (VisibleObject visibleObject : visibleObjects.values()) {
                visibleObject.render(batch, environment);
            }
        }
    }

    @Override
    public IntMap<VisibleObject> defaultVisibleObjets() {
        return defaultVisibleObjets ;
    }

    protected void setDimensions(float width, float length) {
        this.width = width;
        this.length = length;
    }

    @Override
    public float getWidth() {
        return width;
    }

    @Override
    public float getLength() {
        return length;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Challenge> T getChallenge() {
        return (T)challenge;
    }

    @Override
    public void setChallenge(Challenge challenge) {
        this.challenge = challenge;
    }
}
