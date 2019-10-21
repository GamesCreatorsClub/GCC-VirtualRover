package org.ah.gcc.virtualrover;

public class Inputs {

    private float moveX = 0f;
    private float moveY = 0f;

    private float rotateX = 0f;
    private float rotateY = 0f;


    public static Inputs create() {
        return new Inputs();
    }

    public Inputs() {

    }

    public float moveX() {
        return moveX;
    }

    public Inputs moveX(float moveX) {
        this.moveX = moveX;
        return this;
    }

    public float moveY() {
        return moveY;
    }

    public Inputs moveY(float moveY) {
        this.moveY = moveY;
        return this;
    }

    public float rotateX() {
        return rotateX;
    }

    public Inputs rotateX(float rotateX) {
        this.rotateX = rotateX;
        return this;
    }

    public float rotateY() {
        return rotateY;
    }

    public Inputs rotateUp(float rotateY) {
        this.rotateY = rotateY;
        return this;
    }
}
