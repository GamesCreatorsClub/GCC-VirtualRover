package org.ah.gcc.virtualrover.backgrounds;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.badlogic.gdx.graphics.g3d.utils.DefaultTextureBinder;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import org.ah.gcc.virtualrover.utils.MeshUtils;

import static org.ah.gcc.virtualrover.utils.MeshUtils.createRect;

public class PerlinNoiseBackground {

    private Renderable renderable;
    private DefaultShader shader;

    private Mesh backgroundMesh;
    private RenderContext renderContext;

    private long a = 1;

    public PerlinNoiseBackground() {
        backgroundMesh = createRect(0, 0, 120, 120);
        renderable = new Renderable();
        renderable.meshPart.mesh = backgroundMesh;
        renderable.meshPart.primitiveType = GL20.GL_TRIANGLES;
        renderable.meshPart.size = backgroundMesh.getNumIndices();
        renderable.material = null;
        renderable.worldTransform.idt();

        renderContext = new RenderContext(new DefaultTextureBinder(DefaultTextureBinder.WEIGHTED, 1));

        String vertexProgram = Gdx.files.internal("background.vs").readString();
        String fragmentProgram = Gdx.files.internal("background.fs").readString();

        shader = new DefaultShader(renderable, new DefaultShader.Config(vertexProgram, fragmentProgram));
        shader.init();
        if (!shader.program.isCompiled()) {
            Gdx.app.log("Shader error: ", shader.program.getLog());
            Gdx.app.exit();
        }
    }

    public void dispose() {
        backgroundMesh.dispose();
        shader.dispose();
    }

    public void render(Camera camera, ModelBatch batch, Environment environment) {
        a++;

        renderContext.begin();
        shader.begin(camera, renderContext);
        shader.program.setUniformMatrix("u_projViewTrans", camera.combined);
        shader.program.setUniformMatrix("u_worldTrans", renderable.worldTransform);
        shader.program.setUniformf("u_time", a);
        shader.render(renderable);

        shader.end();
        renderContext.end();
    }
}
