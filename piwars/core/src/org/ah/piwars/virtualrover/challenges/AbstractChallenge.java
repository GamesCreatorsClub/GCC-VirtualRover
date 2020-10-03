package org.ah.piwars.virtualrover.challenges;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Attribute;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Shape2D;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.utils.IntMap;

import org.ah.piwars.virtualrover.VisibleObject;
import org.ah.piwars.virtualrover.game.PiWarsCollidableObject;
import org.ah.piwars.virtualrover.game.attachments.PiNoonAttachment;
import org.ah.piwars.virtualrover.game.challenge.Box2DPhysicalWorldSimulationChallenge;
import org.ah.piwars.virtualrover.game.challenge.Challenge;
import org.ah.piwars.virtualrover.game.rovers.Rover;
import org.ah.piwars.virtualrover.screens.RenderingContext;
import org.ah.piwars.virtualrover.world.PiNoonAttachmentModelLink;
import org.ah.piwars.virtualrover.world.PlayerModelLink;

import java.util.ArrayList;
import java.util.List;

import static org.ah.piwars.virtualrover.MainGame.SCALE;
import static org.ah.piwars.virtualrover.utils.MeshUtils.createRect;

public abstract class AbstractChallenge implements ChallengeArena {

    protected ModelInstance challengeModelInstance;

    protected AssetManager assetManager;

    private IntMap<VisibleObject> defaultVisibleObjets = new IntMap<VisibleObject>();

    protected float width = 2200;
    protected float length = 2200;

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
    }

    @Override
    public void render(RenderingContext renderingContext, IntMap<VisibleObject> visibleObjects) {

        renderChallenge(renderingContext, visibleObjects);
        renderVisibleObjects(renderingContext, visibleObjects);

        if (renderingContext.showPlan && debugBox2D()) {
            debugFrameBuffer.begin();

            Gdx.gl.glClearColor(1f, 1f, 1f, 1f);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
            debugBox2dDebugRenderer.render(((Box2DPhysicalWorldSimulationChallenge)challenge).getBox2DPhysicalWorld().getWorld(), debugBox2dCamera.combined);

            debugFrameBuffer.end();
        }

        if (renderingContext.showPlan && debugVisibleObjects()) {
            debugFrameBuffer.begin();
            Gdx.gl.glClearColor(1f, 1f, 1f, 1f);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

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

    protected void renderChallenge(RenderingContext renderingContext, IntMap<VisibleObject> visibleObjects) {
        renderingContext.modelBatch.render(challengeModelInstance, renderingContext.environment);
    }

    protected void renderVisibleObjects(RenderingContext renderingContext, IntMap<VisibleObject> visibleObjects) {
        if (renderingContext.showRovers) {
            for (VisibleObject visibleObject : visibleObjects.values()) {
                visibleObject.render(renderingContext.modelBatch, renderingContext.environment);
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
