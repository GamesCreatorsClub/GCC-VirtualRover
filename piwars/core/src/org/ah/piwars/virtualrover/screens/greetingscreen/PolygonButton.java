package org.ah.piwars.virtualrover.screens.greetingscreen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class PolygonButton extends Button {

    private OrthographicCamera camera;
    private ShapeRenderer shapeRenderer;
    private FrameBuffer frameBuffer;
    private Texture texture;
    private Polygon polygon;
    private TextureRegion textureRegion;


    public PolygonButton(Polygon polygon, Skin skin, String style) {
        this(polygon.getVertices(), skin, style);
    }

    public PolygonButton(float[] vertices, Skin skin, String style) {
        super(skin, style);
        this.polygon = new Polygon(vertices);

        camera = new OrthographicCamera(getWidth(), getHeight());
        shapeRenderer = new ShapeRenderer();
    }

    @Override
    public void setSize(float width, float height) {
        super.setSize(width, height);
        if (width > 1f && height > 1f) {
            if (texture != null) { texture.dispose(); }
            if (frameBuffer != null) { frameBuffer.dispose(); }

            camera.setToOrtho(true, width, height);

            frameBuffer = new FrameBuffer(Format.RGBA8888, (int)width, (int)height, false);
            texture = frameBuffer.getColorBufferTexture();

            shapeRenderer.setProjectionMatrix(camera.combined);

            float maxX = 0;
            float maxY = 0;
            float[] vertices = polygon.getVertices();
            for (int i = 0; i < vertices.length; i = i + 2) {
                if (vertices[i] > maxX) { maxX = vertices[i]; }
                if (vertices[i + 1] > maxY) { maxY = vertices[i + 1]; }
            }

            polygon.setPosition((width - maxX) / 2f, (height - maxY) / 2f);

            frameBuffer.begin();
            Gdx.gl.glClearColor(0f, 0f, 0f, 0f);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
            Gdx.gl.glEnable(GL20.GL_BLEND);

            vertices = polygon.getTransformedVertices();

            shapeRenderer.setAutoShapeType(true);
            shapeRenderer.begin(ShapeType.Filled);
            shapeRenderer.setColor(getColor());
            shapeRenderer.triangle(vertices[0], vertices[1], vertices[2], vertices[3], vertices[4], vertices[5]);
            shapeRenderer.end();
            frameBuffer.end();

            textureRegion = new TextureRegion(texture);
            setBounds(textureRegion.getRegionX(), textureRegion.getRegionY(),
                    textureRegion.getRegionWidth(), textureRegion.getRegionHeight());
        }
    }

    @Override
    public void draw (Batch batch, float parentAlpha) {
        Color color = getColor();
        batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);
        batch.draw(textureRegion, getX(), getY(), getOriginX(), getOriginY(),
              getWidth(), getHeight(), getScaleX(), getScaleY(), getRotation());
    }
}
