package org.ah.piwars.virtualrover.challenges.pinoon;

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
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Shape2D;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.IntMap;

import org.ah.piwars.virtualrover.VisibleObject;
import org.ah.piwars.virtualrover.challenges.AbstractChallenge;
import org.ah.piwars.virtualrover.game.attachments.PiNoonAttachment;
import org.ah.piwars.virtualrover.game.rovers.Rover;
import org.ah.piwars.virtualrover.screens.RenderingContext;
import org.ah.piwars.virtualrover.world.PiNoonAttachmentModelLink;
import org.ah.piwars.virtualrover.world.PlayerModelLink;

import java.util.ArrayList;
import java.util.List;

import static org.ah.piwars.virtualrover.MainGame.SCALE;
import static org.ah.piwars.virtualrover.game.challenge.PiNoonChallenge.CHALLENGE_WIDTH;
import static org.ah.piwars.virtualrover.utils.MeshUtils.createRect;

public class PiNoonArena extends AbstractChallenge {

    private FrameBuffer frameBuffer;

    private OrthographicCamera floorCamera;

    private Mesh floorMesh;

    private ShapeRenderer shapeRenderer;

    private Model floorModel;
    private ModelInstance floorModelInstance;

    private IntMap<VisibleObject> localVisibleObjects = new IntMap<>();
    private IntMap<VisibleObject> allVisibleObjects = new IntMap<VisibleObject>();

    public PiNoonArena(AssetManager assetManager) {
        super(assetManager);

        setDimensions(CHALLENGE_WIDTH, CHALLENGE_WIDTH);

        frameBuffer = new FrameBuffer(Format.RGBA8888, 1024, 1024, false);

        floorCamera = new OrthographicCamera(2000, 2000);
        floorCamera.update();

        int primitiveType = GL20.GL_TRIANGLES;
        int attrs = Usage.Position | Usage.ColorUnpacked | Usage.TextureCoordinates;

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
    public void init() {
        challengeModelInstance = new ModelInstance(assetManager.get("3d/challenges/pi-noon-arena.obj", Model.class));
        challengeModelInstance.transform.setToTranslationAndScaling(0, -70 * SCALE, 0, SCALE, SCALE, SCALE);
        challengeModelInstance.materials.get(0).set(ColorAttribute.createDiffuse(new Color(0.5f, 0.1f, 0.1f, 1f)));
    }

    @Override
    public void dispose() {
        frameBuffer.dispose();
        floorModel.dispose();
        floorMesh.dispose();
        shapeRenderer.dispose();
    }

    @Override
    public void render(RenderingContext renderingContext, IntMap<VisibleObject> visibleObjects) {
        if (renderingContext.showPlan) {
            frameBuffer.begin();
            Gdx.gl.glClearColor(1f, 1f, 1f, 1f);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

            shapeRenderer.begin();

            for (VisibleObject visibleObject : visibleObjects.values()) {
                if (visibleObject instanceof PlayerModelLink) {
                    PlayerModelLink playerModel = (PlayerModelLink)visibleObject;

                    Rover rover = playerModel.getGameObject();

                    shapeRenderer.setColor(playerModel.getColour());

                    for (Shape2D shape : rover.getCollisionPolygons()) {
                        if (shape instanceof Polygon) {
                            shapeRenderer.polygon(((Polygon)shape).getTransformedVertices());
                        }
                    }
                } else if (visibleObject instanceof PiNoonAttachmentModelLink) {
                    PiNoonAttachmentModelLink attachmentModel = (PiNoonAttachmentModelLink)visibleObject;

                    PiNoonAttachment attachment = attachmentModel.getGameObject();
                    shapeRenderer.setColor(attachmentModel.getColour());

                    int balloonBits = attachment.getBalloonBits();
                    for (int i = 0; i < 3; i++) {
                        if ((balloonBits & 1 << i) != 0) {
                            Circle balloon = attachment.getBalloon(i);
                            shapeRenderer.circle(balloon.x, balloon.y, balloon.radius);
                        }
                    }

                    Vector2 sharpEndPos = attachment.getSharpEnd();
                    shapeRenderer.circle(sharpEndPos.x, sharpEndPos.y, 5);
                }
            }

            shapeRenderer.end();
            frameBuffer.end();
        }

        allVisibleObjects.clear();
        allVisibleObjects.putAll(visibleObjects);
        allVisibleObjects.putAll(localVisibleObjects);

        super.render(renderingContext, allVisibleObjects);
    }

    @Override
    protected void renderChallenge(RenderingContext renderingContext, IntMap<VisibleObject> visibleObjects) {
        super.renderChallenge(renderingContext, visibleObjects);
        if (renderingContext.showPlan) { renderingContext.modelBatch.render(floorModelInstance); }
    }
}
