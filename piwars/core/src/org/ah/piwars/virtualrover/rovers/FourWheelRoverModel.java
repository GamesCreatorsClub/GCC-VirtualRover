package org.ah.piwars.virtualrover.rovers;

import com.badlogic.gdx.graphics.Color;

import java.util.NoSuchElementException;

public abstract class FourWheelRoverModel extends AbstractRoverModel {

    protected AbstractWheel fr;
    protected AbstractWheel br;
    protected AbstractWheel bl;
    protected AbstractWheel fl;

    protected FourWheelRoverModel(String name, Color colour) throws NoSuchElementException {
        super(name, colour);
    }

    protected void setWheelSpeeds(float speed) {
        fl.setSpeed(speed);
        fr.setSpeed(speed);
        bl.setSpeed(speed);
        br.setSpeed(speed);
    }

    @Override
    public void dispose() {
        fr.dispose();
        br.dispose();
        bl.dispose();
        fl.dispose();
    }
}
