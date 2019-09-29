package org.ah.gcc.virtualrover;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class GCCVirtualRover extends ApplicationAdapter {
	SpriteBatch batch;
	Texture img;
	Sound wavSound;
	boolean playingStarted = false;

	@Override
	public void create () {
		batch = new SpriteBatch();
		img = new Texture("badlogic.jpg");
		wavSound = Gdx.audio.newSound(Gdx.files.internal("house_lo.wav"));
		// wavSound.setVolume(1.0f);
	}

	@Override
	public void render () {
		if (!playingStarted && wavSound != null) {
			wavSound.loop();
			playingStarted = true;
		}

		Gdx.gl.glClearColor(1, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();
		if (img != null) {
			batch.draw(img, (Gdx.graphics.getWidth() - img.getWidth()) / 2, (Gdx.graphics.getHeight() - img.getHeight()) / 2);
		}
		batch.end();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		img.dispose();
	}
}
