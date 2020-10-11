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
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

import org.ah.piwars.virtualrover.screens.RenderingContext;

import static org.ah.piwars.virtualrover.MainGame.SCALE;
import static org.ah.piwars.virtualrover.challenges.AbstractChallengeScreen.UP;

public abstract class Local3DDisplay {

    public static final int CORNER_WIDTH = 32;
    public static final int ARROW_WIDTH = 15;
    public static final int ARROW_HEIGHT = 20;

    protected PerspectiveCamera camera;
    protected FrameBuffer frameBuffer;
    protected Texture texture;
    protected float rotationAngle = 0;
    protected float x;
    protected float y;
    protected int width;
    protected int height;
    protected float modelWidth;
    protected float modelHeight;

    protected SpriteBatch cornerBatch;
    protected OrthographicCamera cornerCamera;
    protected Texture cornerTexture;
    protected ModelBatch modelBatch;
    protected Environment environment;

    protected Stage stage;
    protected Skin skin;

    protected RenderingContext renderingContext;

    protected TextButton leftButton;
    protected TextButton rightButton;

    public Local3DDisplay(
            Texture cornerTexture,
            Stage stage, Skin skin,
            ModelBatch modelBatch, Environment environment,
            int width, int height) {
        this.cornerTexture = cornerTexture;
        this.stage = stage;
        this.skin = skin;
        this.modelBatch = modelBatch;
        this.environment = environment;
        this.width = width;
        this.height = height;

        create();
    }

    public void create() {
        cornerCamera = new OrthographicCamera(CORNER_WIDTH, CORNER_WIDTH);
        cornerCamera.setToOrtho(true, CORNER_WIDTH, CORNER_WIDTH);

        cornerBatch = new SpriteBatch();
        cornerCamera.setToOrtho(true, width, height);
        cornerBatch.setProjectionMatrix(cornerCamera.combined);

        camera = new PerspectiveCamera(45, width, height);
        camera.position.set(300f * SCALE, 3000 * SCALE, 300f * SCALE);
        camera.lookAt(0f, 0f, 0f);
        camera.near = 0.02f;
        camera.far = 1000f;
        camera.up.set(UP);
        camera.fieldOfView = 45f;
        camera.update();

        frameBuffer = new FrameBuffer(Format.RGBA8888, width, height, true);
        texture = frameBuffer.getColorBufferTexture();
        renderingContext = new RenderingContext(modelBatch, environment, frameBuffer);

        leftButton = new TextButton("<", skin, "opaque") {
            Polygon leftArray = new Polygon(new float[] { 0, ARROW_HEIGHT / 2, ARROW_WIDTH, 0, ARROW_WIDTH, ARROW_HEIGHT });
            {
                leftArray.setOrigin(0, ARROW_HEIGHT / 2);
            }
            @Override
            public void setPosition (float x, float y) {
                super.setPosition(x, y);
                leftArray.setPosition(x, y);
            }

            @Override
            public void draw (Batch batch, float parentAlpha) {
                super.draw(batch, parentAlpha);
//                shapeRenderer.set(ShapeType.Filled);
//                shapeRenderer.setColor(Color.BLACK);
//                shapeRenderer.polygon(leftArray.getVertices());
            }
        };
        leftButton.setSize(ARROW_WIDTH, ARROW_HEIGHT);
        leftButton.setChecked(false);
        leftButton.setProgrammaticChangeEvents(false);

        rightButton = new TextButton(" ", skin, "opaque") {
            private Polygon rightArray = new Polygon(new float[] { ARROW_WIDTH, ARROW_HEIGHT / 2, 0, 0, 0, ARROW_HEIGHT });
            {
                rightArray.setOrigin(ARROW_WIDTH, ARROW_HEIGHT / 2);
            }
            @Override
            public void setPosition (float x, float y) {
                super.setPosition(x, y);
                rightArray.setPosition(x, y);
            }

            @Override
            public void draw (Batch batch, float parentAlpha) {
                super.draw(batch, parentAlpha);
//                shapeRenderer.set(ShapeType.Filled);
//                shapeRenderer.setColor(Color.BLACK);
//                shapeRenderer.polygon(rightArray.getVertices());
            }
        };
        rightButton.setSize(ARROW_WIDTH, ARROW_HEIGHT);
        rightButton.setChecked(false);
        rightButton.setProgrammaticChangeEvents(false);

        stage.addActor(leftButton);
        stage.addActor(rightButton);
    }

    public void setVisible(boolean visible) {
        leftButton.setVisible(visible);
        rightButton.setVisible(visible);
    }

    public void dispose() {
        modelBatch.dispose();
        cornerBatch.dispose();
        texture.dispose();
        frameBuffer.dispose();
    }

    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
//        leftArray.setPosition(x + 5, height / 2);
//        rightArray.setPosition(x + width - 5, height / 2);

        leftButton.setPosition(x + 5 , y + height / 2 - leftButton.getHeight() / 2);
        rightButton.setPosition(x + width - 5 - rightButton.getWidth(), y + height / 2 - leftButton.getHeight() / 2);
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
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

        camera.position.set(x, radius * SCALE, y);
        camera.lookAt(0f, 0f, 0f);
        camera.up.set(UP);
        camera.update();
    }

    public void render() {
        renderingContext.frameBuffer.begin();
        Gdx.gl.glClearColor(0.6f, 0.75f, 1f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);

        renderingContext.modelBatch = modelBatch;
        renderingContext.modelBatch.begin(camera);
        drawModel(renderingContext);
        drawCorners(width, height);
        renderingContext.modelBatch.end();
        renderingContext.frameBuffer.end();
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

    public void drawTexture(SpriteBatch batch) {
        batch.draw(texture, x, y, width, height, 0, 0, width, height, false, true);
    }

    public void drawTextureBorder(ShapeRenderer shapeRenderer) {
        shapeRenderer.set(ShapeType.Filled);
        shapeRenderer.setColor(Color.YELLOW);
        shapeRenderer.rectLine(x - 1, y + CORNER_WIDTH - 2, x - 1, y + height - CORNER_WIDTH + 2, 2, Color.YELLOW, Color.YELLOW);
        shapeRenderer.rectLine(x + width + 1, y + CORNER_WIDTH - 2, x + width + 1, y + 1 + height - CORNER_WIDTH + 2, 2, Color.YELLOW, Color.YELLOW);
        shapeRenderer.rectLine(x + CORNER_WIDTH - 2, y - 1, x + width - CORNER_WIDTH + 2, y - 1, 2, Color.YELLOW, Color.YELLOW);
        shapeRenderer.rectLine(x + CORNER_WIDTH - 2, y + height + 1, x + width - CORNER_WIDTH + 2, y + height + 1, 2, Color.YELLOW, Color.YELLOW);
        //shapeRenderer.set(ShapeType.Line);
        shapeRenderer.arc(x - 2 + CORNER_WIDTH, y - 2 + CORNER_WIDTH, CORNER_WIDTH, 180, 90, 5);
        shapeRenderer.arc(x + 2 + width - CORNER_WIDTH, y - 2 + CORNER_WIDTH, CORNER_WIDTH, 270, 90, 5);
        shapeRenderer.arc(x + 2 + width - CORNER_WIDTH, y + height + 2 - CORNER_WIDTH, CORNER_WIDTH, 0, 90, 5);
        shapeRenderer.arc(x - 2 + CORNER_WIDTH, y + height + 2 - CORNER_WIDTH, CORNER_WIDTH, 90, 90, 5);
    }

    public void drawArrows(ShapeRenderer shapeRenderer) {
        shapeRenderer.set(ShapeType.Filled);
        shapeRenderer.setColor(Color.BLACK);

//        shapeRenderer.polygon(leftArray.getTransformedVertices());
//        shapeRenderer.polygon(rightArray.getTransformedVertices());
    }

    public abstract void drawModel(RenderingContext renderingContext);

    public abstract void reset();

    public abstract void previous();
    public abstract void next();
}
