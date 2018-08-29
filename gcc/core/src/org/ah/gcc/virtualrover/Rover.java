package org.ah.gcc.virtualrover;

public abstract class Rover implements Robot {
    private String name;

    private int balloons = 3;

    public Rover(String name) {
        this.name = name;
        setBalloons(3);
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
