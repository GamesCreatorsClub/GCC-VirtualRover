package org.ah.gcc.virtualrover.utils;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;

public class SoundManager {

    private Sound ready1;
    private Sound fight1;
    private boolean muted;
    private boolean noSound;

    public SoundManager(boolean noSound) {
        this.noSound = noSound;
        this.muted = noSound;
    }

    public void dispose() {

    }

    public void requestAssets(AssetManager assetManager) {
        if (!noSound) {
            assetManager.load("sounds/ready1.wav", Sound.class);
            assetManager.load("sounds/fight1.wav", Sound.class);
        }
    }

    public void fetchSounds(AssetManager assetManager) {
        if (!noSound) {
            ready1 = assetManager.get("sounds/ready1.wav");
            fight1 = assetManager.get("sounds/fight1.wav");
        }
    }

    public void playReady() {
        if (!muted) {
            ready1.play();
        }
    }

    public void playFight() {
        if (!muted) {
            fight1.play();
        }
    }
}
