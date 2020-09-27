package org.ah.piwars.virtualrover.challenges;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalShadowLight;
import com.badlogic.gdx.graphics.g3d.utils.DepthShaderProvider;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.IntMap;

import org.ah.piwars.virtualrover.VisibleObject;

@SuppressWarnings("deprecation")
public abstract class AbstractChallenge implements ChallengeArena {

    protected ModelInstance challengeModelInstance;

    public boolean showRovers = true;
    public boolean showShadows = true;

    private ModelBatch modelBatch;
    private ModelBatch shadowBatch;

    private Environment shadowEnvironment;

    private DirectionalShadowLight shadowLight;

    protected AssetManager assetManager;

    private IntMap<VisibleObject> defaultVisibleObjets = new IntMap<VisibleObject>();

    private float width = 2200;
    private float length = 2200;

    // public int shadowTextureSize = 8192;
    // public int shadowTextureSize = 2048;
    public int shadowTextureSize = 1280;

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

    @Override
    public void init() {
    }

    @Override
    public void dispose() {
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

}
