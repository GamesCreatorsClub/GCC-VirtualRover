package org.ah.piwars.virtualrover.screens.greetingscreen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.scenes.scene2d.Actor;

import org.ah.piwars.virtualrover.screens.RenderingContext;

import static org.ah.piwars.virtualrover.MainGame.SCALE;
import static org.ah.piwars.virtualrover.challenges.AbstractChallengeScreen.UP;

public abstract class ModelActor extends Actor {

    public static final int CORNER_WIDTH = 32;

    private Texture cornerTexture;

    private OrthographicCamera cornerCamera;
    private SpriteBatch cornerBatch;
    private ShapeRenderer borderShapeRenderer;
    private PerspectiveCamera modelCamera;
    private FrameBuffer modelFrameBuffer;
    private Texture modelTexture;

    private FrameBuffer borderFrameBuffer;
    private Texture borderTexture;
    private OrthographicCamera borderCamera;

    private ModelBatch modelBatch;
    private RenderingContext renderingContext;

    protected float modelWidth;
    protected float modelHeight;

    private float rotationAngle;

    public ModelActor(
            Texture cornerTexture,
            ModelBatch modelBatch,
            Environment environment,
            int width, int height) {

        this.cornerTexture = cornerTexture;
        this.modelBatch = modelBatch;

        setSize(width, height);

        cornerCamera = new OrthographicCamera(width, height);
        cornerBatch = new SpriteBatch();

        modelCamera = new PerspectiveCamera(45, width, height);
        modelCamera.position.set(300f * SCALE, 3000 * SCALE, 300f * SCALE);
        modelCamera.lookAt(0f, 0f, 0f);
        modelCamera.near = 0.02f;
        modelCamera.far = 1000f;
        modelCamera.up.set(UP);
        modelCamera.fieldOfView = 45f;
        modelCamera.update();
        renderingContext = new RenderingContext(modelBatch, environment, modelFrameBuffer);

        create(width, height);

        borderCamera = new OrthographicCamera(width, height);
        borderShapeRenderer = new ShapeRenderer();
        borderShapeRenderer.setAutoShapeType(true);
        createBorder(width, height);
    }

    public void create(int width, int height) {
        cornerCamera.setToOrtho(true, width - 4, height - 4);

        cornerBatch.setProjectionMatrix(cornerCamera.combined);

        if (modelTexture != null) { modelTexture.dispose(); }
        if (modelFrameBuffer != null) { modelFrameBuffer.dispose(); }

        modelFrameBuffer = new FrameBuffer(Format.RGBA8888, width - 4, height - 4, true);
        modelTexture = modelFrameBuffer.getColorBufferTexture();

        renderingContext.frameBuffer = modelFrameBuffer;
    }

    private void createBorder(int width, int height) {
        borderCamera.setToOrtho(true, width, height);

        borderShapeRenderer.setProjectionMatrix(borderCamera.combined);

        if (borderTexture != null) { borderTexture.dispose(); }
        if (borderFrameBuffer != null) { borderFrameBuffer.dispose(); }

        borderFrameBuffer = new FrameBuffer(Format.RGBA8888, width, height, true);
        borderTexture = borderFrameBuffer.getColorBufferTexture();

        borderFrameBuffer.begin();
        Gdx.gl.glClearColor(0f, 0f, 0f, 0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.gl.glEnable(GL20.GL_BLEND);

        borderShapeRenderer.begin(ShapeType.Filled);
        borderShapeRenderer.setColor(Color.YELLOW);
        borderShapeRenderer.rectLine(1, CORNER_WIDTH,  1,  height - CORNER_WIDTH, 2, Color.YELLOW, Color.YELLOW);
        borderShapeRenderer.rectLine(width - 1,  CORNER_WIDTH,  width - 1,  height - CORNER_WIDTH, 2, Color.YELLOW, Color.YELLOW);
        borderShapeRenderer.rectLine(CORNER_WIDTH,  1,  width - CORNER_WIDTH,  1, 2, Color.YELLOW, Color.YELLOW);
        borderShapeRenderer.rectLine(CORNER_WIDTH, height - 1, width - CORNER_WIDTH, height - 1, 2, Color.YELLOW, Color.YELLOW);

        borderShapeRenderer.arc(CORNER_WIDTH,   CORNER_WIDTH, CORNER_WIDTH, 180, 90, 5);
        borderShapeRenderer.arc(width - CORNER_WIDTH, CORNER_WIDTH, CORNER_WIDTH, 270, 90, 5);
        borderShapeRenderer.arc(width - CORNER_WIDTH, height - CORNER_WIDTH, CORNER_WIDTH, 0, 90, 5);
        borderShapeRenderer.arc(CORNER_WIDTH,  height - CORNER_WIDTH, CORNER_WIDTH, 90, 90, 5);
        borderShapeRenderer.end();
        borderFrameBuffer.end();
    }

    public void setModelDimensions(float modelWidth, float modelHeight) {
        this.modelWidth = modelWidth;
        this.modelHeight =  modelHeight;
    }

    public void update() {
        rotationAngle = rotationAngle + (float)(Math.PI / 180.0);
        if (rotationAngle > (float)(rotationAngle * Math.PI * 2f)) {
            rotationAngle = 0f;
        }

        float radius = modelWidth > modelHeight ? modelWidth : modelHeight;
        radius = radius * 1.2f;

        float x = (float)(Math.sin(rotationAngle) * radius) * SCALE;
        float y = (float)(Math.cos(rotationAngle) * radius) * SCALE;

        modelCamera.position.set(x, radius * SCALE, y);
        modelCamera.lookAt(0f, 0f, 0f);
        modelCamera.up.set(UP);
        modelCamera.update();
    }

    public void dispose() {
        modelBatch.dispose();
        cornerBatch.dispose();
        modelTexture.dispose();
        modelFrameBuffer.dispose();
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        update();

        renderingContext.frameBuffer.begin();
        Gdx.gl.glClearColor(0.6f, 0.75f, 1f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);

        renderingContext.modelBatch.begin(modelCamera);
        drawModel(renderingContext);
        drawCorners(renderingContext.frameBuffer.getWidth(), renderingContext.frameBuffer.getHeight());
        renderingContext.modelBatch.end();
        renderingContext.frameBuffer.end();
    }

    @Override
    public void draw (Batch batch, float parentAlpha) {
        int x = (int)getX();
        int y = (int)getY();
        int width = (int)getWidth();
        int height = (int)getHeight();

        batch.draw(borderTexture, x, y, width, height, 0, 0, width, height, false, true);
        batch.draw(modelTexture, x + 2, y + 2, width - 4, height - 4, 0, 0, width - 4, height - 4, false, true);
    }

    private void drawCorners(int width, int height) {
        cornerBatch.begin();
        cornerBatch.setBlendFunction(GL20.GL_ZERO, GL20.GL_SRC_COLOR);
        cornerBatch.draw(cornerTexture, 0, 0, CORNER_WIDTH, CORNER_WIDTH, 0, 0, CORNER_WIDTH, CORNER_WIDTH, false, false);
        cornerBatch.draw(cornerTexture, width - CORNER_WIDTH, 0, CORNER_WIDTH, CORNER_WIDTH, 0, 0, CORNER_WIDTH, CORNER_WIDTH, true, false);
        cornerBatch.draw(cornerTexture, 0,  height - CORNER_WIDTH, CORNER_WIDTH, CORNER_WIDTH, 0, 0, CORNER_WIDTH, CORNER_WIDTH, false, true);
        cornerBatch.draw(cornerTexture, width - CORNER_WIDTH, height - CORNER_WIDTH, CORNER_WIDTH, CORNER_WIDTH, 0, 0, CORNER_WIDTH, CORNER_WIDTH, true, true);
        cornerBatch.end();
    }

    public abstract void drawModel(RenderingContext renderingContext);
}
