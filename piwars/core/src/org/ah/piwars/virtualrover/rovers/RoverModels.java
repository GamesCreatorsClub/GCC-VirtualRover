package org.ah.piwars.virtualrover.rovers;

import com.badlogic.gdx.assets.AssetManager;

public class RoverModels {

    public RoverModels() {

    }

    public void load(AssetManager assetManager) {
        GCCRoverModelM16.load(assetManager);
        GCCRoverModelM18.load(assetManager);
        CBiSRoverModel.load(assetManager);
        MacFeegleModel.load(assetManager);
    }
}
