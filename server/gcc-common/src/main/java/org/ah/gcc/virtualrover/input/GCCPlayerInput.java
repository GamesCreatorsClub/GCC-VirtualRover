package org.ah.gcc.virtualrover.input;

import org.ah.themvsus.engine.common.factory.AbstractPoolFactory;
import org.ah.themvsus.engine.common.input.PlayerInput;
import org.ah.themvsus.engine.common.transfer.Serializer;

public class GCCPlayerInput extends PlayerInput {

    private float moveX = 0f;
    private float moveY = 0f;

    private float rotateX = 0f;
    private float rotateY = 0f;

    private float leftTrigger = 0f;
    private float rightTrigger = 0f;

    private boolean circle;
    private boolean cross;
    private boolean square;
    private boolean triangle;

    private boolean home;
    private boolean share;
    private boolean options;
    private boolean trackpad;

    private boolean hatUp;
    private boolean hatDown;
    private boolean hatLeft;
    private boolean hatRight;

    private float desiredForwardSpeed = 296f;
    private float desiredRotationSpeed = 296f;

    GCCPlayerInput(AbstractPoolFactory<PlayerInput> factory) {
        super(factory);
    }

    public float moveX() {
        return moveX;
    }

    public GCCPlayerInput moveX(float moveX) {
        this.moveX = moveX;
        return this;
    }

    public float moveY() {
        return moveY;
    }

    public GCCPlayerInput moveY(float moveY) {
        this.moveY = moveY;
        return this;
    }

    public float rotateX() {
        return rotateX;
    }

    public GCCPlayerInput rotateX(float rotateX) {
        this.rotateX = rotateX;
        return this;
    }

    public float rotateY() {
        return rotateY;
    }

    public GCCPlayerInput rotateY(float rotateY) {
        this.rotateY = rotateY;
        return this;
    }

    public float leftTrigger() {
        return leftTrigger;
    }

    public GCCPlayerInput leftTrigger(float leftTrigger) {
        this.leftTrigger = sanitiseTrigger(leftTrigger);
        return this;
    }

    public float rightTrigger() {
        return rightTrigger;
    }

    public GCCPlayerInput rightTrigger(float leftTrigger) {
        this.leftTrigger = sanitiseTrigger(leftTrigger);
        return this;
    }

    private float sanitiseTrigger(float trigger) {
        if (trigger < 0f) { trigger = 0f; }
        if (trigger > 1f) { trigger = 1f; }

        trigger = ((int)trigger * 15f) / 15f;

        return 0;
    }

    public boolean circle() { return circle; }
    public GCCPlayerInput circle(boolean circle) { this.circle = circle; return this; }

    public boolean cross() { return cross; }
    public GCCPlayerInput cross(boolean cross) { this.cross = cross; return this; }

    public boolean square() { return square; }
    public GCCPlayerInput square(boolean square) { this.square = square; return this; }

    public boolean triangle() { return triangle; }
    public GCCPlayerInput triangle(boolean triangle) { this.triangle = triangle; return this; }

    public boolean home() { return home; }
    public GCCPlayerInput home(boolean home) { this.home = home; return this; }

    public boolean share() { return share; }
    public GCCPlayerInput share(boolean share) { this.share = share; return this; }

    public boolean options() { return options; }
    public GCCPlayerInput options(boolean options) { this.options = options; return this; }

    public boolean trackpad() { return trackpad; }
    public GCCPlayerInput trackpad(boolean trackpad) { this.trackpad = trackpad; return this; }

    public boolean hatUp() { return hatUp; }
    public GCCPlayerInput hatUp(boolean hatUp) { this.hatUp = hatUp; return this; }

    public boolean hatDown() { return hatDown; }
    public GCCPlayerInput hatDown(boolean hatDown) { this.hatDown = hatDown; return this; }

    public boolean hatLeft() { return hatLeft; }
    public GCCPlayerInput hatLeft(boolean hatLeft) { this.hatLeft = hatLeft; return this; }

    public boolean hatRight() { return hatRight; }
    public GCCPlayerInput hatRight(boolean hatRight) { this.hatRight = hatRight; return this; }

    public float getDesiredForwardSpeed() {
        return desiredForwardSpeed;
    }

    public GCCPlayerInput setDesiredForwardSpeed(float desiredForwardSpeed) {
        this.desiredForwardSpeed = desiredForwardSpeed;
        return this;
    }

    public float getDesiredRotationSpeed() {
        return desiredRotationSpeed;
    }

    public GCCPlayerInput setDesiredRotationSpeed(float desiredRotationSpeed) {
        this.desiredRotationSpeed = desiredRotationSpeed;
        return this;
    }

    @Override
    public void serialize(Serializer serializer) {
        serializer.serializeUnsignedShort(fitTo8Bits(moveX) << 8 | fitTo8Bits(moveY));
        serializer.serializeUnsignedShort(fitTo8Bits(rotateX) << 8 | fitTo8Bits(rotateY));

        int triggers = ((int)(leftTrigger * 15f)) + ((int)(rightTrigger * 15f)) * 16;

        serializer.serializeUnsignedByte(triggers);

        int bits = (circle ? 1 : 0)
                | (cross ? 2 : 0)
                | (square ? 4 : 0)
                | (triangle ? 8 : 0)
                | (home ? 16 : 0)
                | (share ? 32 : 0)
                | (options ? 64 : 0)
                | (trackpad ? 128 : 0)
                | (hatUp ? 256 : 0)
                | (hatDown ? 512 : 0)
                | (hatLeft ? 1024 : 0)
                | (hatRight ? 2048 : 0);
        serializer.serializeUnsignedShort(bits);

        serializer.serializeUnsignedShort(fitTo8BitsWithLimit(desiredForwardSpeed, 0, 1024) << 8 | fitTo8BitsWithLimit(desiredRotationSpeed, -1024, 1024));
    }

    @Override
    public void deserialize(Serializer deserializer) {
        int moveXY = deserializer.deserializeUnsignedShort();
        moveX = (((moveXY >> 8) & 0xff) / 127) - 1;
        moveY = ((moveXY & 0xff) / 127) - 1;

        int rotateXY = deserializer.deserializeUnsignedShort();
        rotateX = (((rotateXY >> 8) & 0xff) / 127) - 1;
        rotateY = ((rotateXY & 0xff) / 127) - 1;

        int triggers = deserializer.deserializeUnsignedByte();
        leftTrigger = (triggers & 0xf) / 15f;
        rightTrigger = ((triggers >> 4) & 0xf) / 15f;

        int bits = deserializer.deserializeUnsignedShort();
        circle = (bits & 1) != 0;
        cross = (bits & 2) != 0;
        square = (bits & 4) != 0;
        triangle = (bits & 8) != 0;

        home = (bits & 16) != 0;
        share = (bits & 32) != 0;
        options = (bits & 64) != 0;
        trackpad = (bits & 128) != 0;

        hatUp = (bits & 256) != 0;
        hatDown = (bits & 512) != 0;
        hatLeft = (bits & 1024) != 0;
        hatRight = (bits & 2048) != 0;

        int desiredSpeeds = deserializer.deserializeUnsignedShort();
        desiredForwardSpeed = from8BitWithLimit((desiredSpeeds >> 8) & 0xff, 0, 1024);
        desiredRotationSpeed = from8BitWithLimit(desiredSpeeds & 0xff, -1024, 1024);
    }

    static int fitTo8Bits(float v) {
        int intValue  = (int)((v + 1) * 127);
        return intValue;
    }

    static int fitTo4Bits(float v) {
        int intValue  = (int)(v * 15);
        return intValue;
    }

    static int fitTo8BitsWithLimit(float v, int min, int max) {
        if (v >= max) {
            v = max;
        } else if (v <= min) {
            v = min;
        }
        v = v - min;
        v = v * 256 / (max - min);
        return (int)v;
    }

    static float from8BitWithLimit(int b, int min, int max) {
        return b * (max - min) / 256f + min;
    }

    public static final AbstractPoolFactory<PlayerInput> INPUTS_FACTORY = new InputsFactory();

    public static class InputsFactory extends AbstractPoolFactory<PlayerInput> {
        @Override protected GCCPlayerInput createNew() { return new GCCPlayerInput(this); }
    }

    @Override
    public void free() {
        moveX = 0f;
        moveY = 0f;

        rotateX = 0f;
        rotateY = 0f;

        leftTrigger = 0f;
        rightTrigger = 0f;

        circle = false;
        cross = false;
        square = false;
        triangle = false;

        home = false;
        share = false;
        options = false;
        trackpad = false;

        hatUp = false;
        hatDown = false;
        hatLeft = false;
        hatRight = false;

        desiredForwardSpeed = 300f;
        desiredRotationSpeed = 300f;

        super.free();
    }

    @Override
    public void assignFrom(PlayerInput playerInput) {
        GCCPlayerInput gccPlayerInput = (GCCPlayerInput)playerInput;
        moveX = gccPlayerInput.moveX;
        moveY = gccPlayerInput.moveY;
        rotateX = gccPlayerInput.rotateX;
        rotateY = gccPlayerInput.rotateY;
        leftTrigger = gccPlayerInput.leftTrigger;
        rightTrigger = gccPlayerInput.rightTrigger;
        circle = gccPlayerInput.circle;
        cross = gccPlayerInput.cross;
        square = gccPlayerInput.square;
        triangle = gccPlayerInput.triangle;
        home = gccPlayerInput.home;
        share = gccPlayerInput.share;
        options = gccPlayerInput.options;
        trackpad = gccPlayerInput.trackpad;
        hatUp = gccPlayerInput.hatUp;
        hatDown = gccPlayerInput.hatDown;
        hatLeft = gccPlayerInput.hatLeft;
        hatRight = gccPlayerInput.hatRight;
        desiredForwardSpeed = gccPlayerInput.desiredForwardSpeed;
        desiredRotationSpeed = gccPlayerInput.desiredRotationSpeed;
    }

    @Override public String toString() {
        return "PlayerInput[" + sequenceNo + "," + moveX + "," + moveY + "," + rotateX + "," + rotateY + "," + desiredForwardSpeed + "," + desiredRotationSpeed + "]";
    }
}
