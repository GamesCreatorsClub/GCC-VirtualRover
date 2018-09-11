package org.ah.gcc.virtualrover;

import com.badlogic.gdx.graphics.Color;

public abstract class Rover implements Robot {

    private String name;
    private Color colour;

    private int balloons = 3;

    public Rover(String name, Color colour) {
        this.name = name;
        this.colour = colour;
        setBalloons(3);
    }

    public String getName() {
        return name;
    }

    public Color getColour() {
        return colour;
    }

    public int getBalloons() {
        return balloons;
    }

    public void setBalloons(int balloons) {
        this.balloons = balloons;
    }

    public void popBaloon() {
        balloons -= 1;
    }

}
