package org.ah.gcc.virtualrover.input;

import org.ah.themvsus.engine.common.factory.AbstractPoolFactory;
import org.ah.themvsus.engine.common.input.PlayerInput;
import org.ah.themvsus.engine.common.transfer.Serializer;

public class GCCPlayerInput extends PlayerInput {

    private float moveX = 0f;
    private float moveY = 0f;

    private float rotateX = 0f;
    private float rotateY = 0f;

    private float desiredForwardSpeed = 300f;
    private float desiredRotationSpeed = 300f;

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
        serializer.serializeUnsignedShort(fitTo8Bits((int)(desiredForwardSpeed / 10)) << 8 | fitTo8Bits((int)(desiredRotationSpeed / 10)));
    }

    @Override
    public void deserialize(Serializer deserializer) {
        int moveXY = deserializer.deserializeUnsignedShort();
        moveX = (((moveXY >> 8) & 0xff) / 127) - 1;
        moveY = ((moveXY & 0xff) / 127) - 1;

        int rotateXY = deserializer.deserializeUnsignedShort();
        rotateX = (((rotateXY >> 8) & 0xff) / 127) - 1;
        rotateY = ((rotateXY & 0xff) / 127) - 1;

        int desiredSpeeds = deserializer.deserializeUnsignedShort();
        desiredForwardSpeed = ((((desiredSpeeds >> 8) & 0xff) / 127) - 1) * 10;
        desiredRotationSpeed = (((desiredSpeeds & 0xff) / 127) - 1) * 10;
    }

    static int fitTo8Bits(float v) {
        int intValue  = (int)((v + 1) * 127);
        return intValue;
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
        desiredForwardSpeed = gccPlayerInput.desiredForwardSpeed;
        desiredRotationSpeed = gccPlayerInput.desiredRotationSpeed;
    }

    @Override public String toString() {
        return "PlayerInput[" + sequenceNo + "," + moveX + "," + moveY + "," + rotateX + "," + rotateY + "," + desiredForwardSpeed + "," + desiredRotationSpeed + "]";
    }
}
