package org.ah.piwars.fishtank.input;

import org.ah.themvsus.engine.common.factory.AbstractPoolFactory;
import org.ah.themvsus.engine.common.input.PlayerInput;
import org.ah.themvsus.engine.common.transfer.Serializer;

public class FishtankPlayerInput extends PlayerInput {

    private float camX = 0f;
    private float camY = 0f;
    private float camZ = 0f;

    private boolean trigger;
    private boolean pause;

    FishtankPlayerInput(AbstractPoolFactory<PlayerInput> factory) {
        super(factory);
    }

    public float camX() {
        return camX;
    }

    public FishtankPlayerInput camX(float camX) {
        this.camX = camX;
        return this;
    }

    public float camY() {
        return camY;
    }

    public FishtankPlayerInput camY(float camY) {
        this.camY = camY;
        return this;
    }

    public float camZ() {
        return camZ;
    }

    public FishtankPlayerInput camZ(float camZ) {
        this.camZ = camZ;
        return this;
    }

    public boolean trigger() {
        return trigger;
    }

    public FishtankPlayerInput trigger(boolean trigger) {
        this.trigger = trigger;
        return this;
    }

    public boolean pause() {
        return pause;
    }

    public FishtankPlayerInput pause(boolean pause) {
        this.pause = pause;
        return this;
    }

    @Override
    public int size() {
        return 9;
    }

    @Override
    public void serialize(Serializer serializer) {
        serializer.serializeFloat(camX);
        serializer.serializeFloat(camY);
        serializer.serializeFloat(camZ);

        int bits = (trigger ? 1 : 0)
                 | (pause ? 2 : 0)
                 ;
        serializer.serializeUnsignedShort(bits);
    }

    @Override
    public void deserialize(Serializer deserializer) {
        camX = deserializer.deserializeFloat();
        camY = deserializer.deserializeFloat();
        camZ = deserializer.deserializeFloat();

        int bits = deserializer.deserializeUnsignedShort();
        trigger = (bits & 1) != 0;
        pause = (bits & 2) != 0;
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
        @Override protected FishtankPlayerInput createNew() { return new FishtankPlayerInput(this); }
    }

    @Override
    public void free() {
        camX = 0f;
        camY = 0f;
        camZ = 0f;

        trigger = false;
        pause = false;

        super.free();
    }

    @Override
    public void assignFrom(PlayerInput playerInput) {
        super.assignFrom(playerInput);
        FishtankPlayerInput piwarsPlayerInput = (FishtankPlayerInput)playerInput;
        camX = piwarsPlayerInput.camX;
        camY = piwarsPlayerInput.camY;
        camZ = piwarsPlayerInput.camZ;

        trigger = piwarsPlayerInput.trigger;
        pause = piwarsPlayerInput.pause;
    }

    @Override public String toStringInternal() {
        return sequenceNo + "," + camX + "," + camY + "," + camZ + "," + trigger + "," + pause;
    }

    @Override public String toString() {
        return "PiWarsPlayerInput[" + toStringInternal() + "]";
    }
}
