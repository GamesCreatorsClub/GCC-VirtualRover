package org.ah.gcc.virtualrover;

public class Inputs {

    private boolean moveLeft = false;
    private boolean moveRight = false;
    private boolean moveUp = false;
    private boolean moveDown = false;

    private boolean rotateLeft = false;
    private boolean rotateRight = false;
    private boolean rotateUp = false;
    private boolean rotateDown = false;

    private boolean straightenWheels = false;
    private boolean slantWheels = false;

    public static Inputs create() {
        return new Inputs();
    }

    public Inputs() {

    }

    public boolean moveLeft() {
        return moveLeft;
    }

    public Inputs moveLeft(boolean moveLeft) {
        this.moveLeft = moveLeft;
        return this;
    }

    public boolean moveRight() {
        return moveRight;
    }

    public Inputs moveRight(boolean moveRight) {
        this.moveRight = moveRight;
        return this;
    }

    public boolean moveUp() {
        return moveUp;
    }

    public Inputs moveUp(boolean moveUp) {
        this.moveUp = moveUp;
        return this;
    }

    public boolean moveDown() {
        return moveDown;
    }

    public Inputs moveDown(boolean moveDown) {
        this.moveDown = moveDown;
        return this;
    }

    public boolean rotateLeft() {
        return rotateLeft;
    }

    public Inputs rotateLeft(boolean rotateLeft) {
        this.rotateLeft = rotateLeft;
        return this;
    }

    public boolean rotateRight() {
        return rotateRight;
    }

    public Inputs rotateRight(boolean rotateRight) {
        this.rotateRight = rotateRight;
        return this;
    }

    public boolean rotateUp() {
        return rotateUp;
    }

    public Inputs rotateUp(boolean rotateUp) {
        this.rotateUp = rotateUp;
        return this;
    }

    public boolean rotateDown() {
        return rotateDown;
    }

    public Inputs rotateDown(boolean rotateDown) {
        this.rotateDown = rotateDown;
        return this;
    }

    public Inputs straightenWheels(boolean straightenWheels) {
        this.straightenWheels = straightenWheels;
        return this;
    }

    public boolean straightenWheels() {
        return straightenWheels;
    }

    public Inputs slantWheels(boolean slantWheels) {
        this.slantWheels = slantWheels;
        return this;
    }

    public boolean slantWheels() {
        return slantWheels;
    }
}
