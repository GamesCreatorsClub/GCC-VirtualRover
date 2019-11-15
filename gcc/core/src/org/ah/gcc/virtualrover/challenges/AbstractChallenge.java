package org.ah.gcc.virtualrover.challenges;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalShadowLight;
import com.badlogic.gdx.graphics.g3d.utils.DepthShaderProvider;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.IntMap;

import org.ah.gcc.virtualrover.ModelFactory;
import org.ah.gcc.virtualrover.VisibleObject;

import java.util.List;

import static org.ah.gcc.virtualrover.utils.MeshUtils.polygonsOverlap;

@SuppressWarnings("deprecation")
public abstract class AbstractChallenge implements Challenge {

    protected ModelInstance challengeModelInstance;

    protected List<Polygon> polygons;

    protected FrameBuffer frameBuffer;

    public boolean showRovers = true;
    public boolean showShadows = true;

    private ModelBatch modelBatch;
    private ModelBatch shadowBatch;

    private Environment shadowEnvironment;

    private DirectionalShadowLight shadowLight;

    private ModelFactory modelFactory;

    public AbstractChallenge(ModelFactory modelFactory) {
        this.modelFactory = modelFactory;

        frameBuffer = new FrameBuffer(Format.RGBA8888, 1024, 1024, false);

        shadowLight = new DirectionalShadowLight(1024, 1024, 6f, 6f, 0.01f, 100f);
        shadowLight.set(1f, 1f, 1f, new Vector3(-0.5f, -1f, 0.5f));
        shadowEnvironment = new Environment();
        shadowEnvironment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.6f, 0.6f, 0.6f, 1f));
        shadowEnvironment.add(shadowLight);
        shadowEnvironment.shadowMap = shadowLight;

        modelBatch = new ModelBatch();
        shadowBatch = new ModelBatch(new DepthShaderProvider());
    }

    @Override
    public void init() {
        challengeModelInstance = createChallengeModelInstance(modelFactory);
        polygons = createCollidingPolygons();
    }

    protected abstract ModelInstance createChallengeModelInstance(ModelFactory modelFactory);
    protected abstract List<Polygon> createCollidingPolygons();

    @Override
    public boolean collides(List<Polygon> polygons) {
        return polygonsOverlap(this.polygons, polygons);
    }

    @Override
    public void dispose() {
        frameBuffer.dispose();
        shadowBatch.dispose();
    }

    @Override
    public void render(ModelBatch batch, Environment environment, IntMap<VisibleObject> visibleObjects) {
        if (showShadows) {
            Camera cam = batch.getCamera();
            batch.end();

            shadowLight.begin(Vector3.Zero, cam.direction);
            shadowBatch.begin(shadowLight.getCamera());

            renderChallenge(shadowBatch, environment, visibleObjects);
            renderVisibleObjects(shadowBatch, environment, visibleObjects);

            shadowBatch.end();
            shadowLight.end();

            modelBatch.begin(cam);
            modelBatch.render(challengeModelInstance, shadowEnvironment);

            renderChallenge(modelBatch, environment, visibleObjects);
            renderVisibleObjects(modelBatch, environment, visibleObjects);

            modelBatch.end();
            batch.begin(cam);
        } else {
            batch.render(challengeModelInstance, environment);

            renderChallenge(batch, environment, visibleObjects);
            renderVisibleObjects(batch, environment, visibleObjects);
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
}
