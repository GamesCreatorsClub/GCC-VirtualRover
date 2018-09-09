package org.ah.themvsus.engine.common.input;

import org.ah.themvsus.engine.common.factory.AbstractPoolFactory;
import org.ah.themvsus.engine.common.transfer.Serializer;

public class GCCPlayerInput extends PlayerInput {

    public float speed = 0;
    public float direction = 0;

    GCCPlayerInput(AbstractPoolFactory<PlayerInput> factory) {
        super(factory);
    }

    @Override
    public void serialize(Serializer serializer) {
        int i = fitTo6Bits(direction) << 6 | fitTo6Bits(speed);
        serializer.serializeUnsignedShort(i);
    }

    @Override
    public void deserialize(Serializer deserializer) {
        int s = deserializer.deserializeUnsignedShort();
        int directionInt = (s & 0x03f) >> 6;
        int speedInt = s & 0x3f;

        direction = (directionInt - 31) * 4;

        speed = (speedInt - 31) * 4;
    }

    static int fitTo6Bits(float speed) {
        int speedInt = (int)(speed / 4);
        if (speedInt > 31) { speedInt = 31; }
        if (speedInt < -31) { speedInt = -31; }
        speedInt = speedInt + 31;
        return speedInt;
    }

    public static final AbstractPoolFactory<PlayerInput> INPUTS_FACTORY = new InputsFactory();

    public static class InputsFactory extends AbstractPoolFactory<PlayerInput> {
        @Override protected GCCPlayerInput createNew() { return new GCCPlayerInput(this); }
    }

    @Override
    public void assignFrom(PlayerInput playerInput) {
        GCCPlayerInput themVsUsPlayerInput = (GCCPlayerInput)playerInput;
        speed = themVsUsPlayerInput.speed;
        direction = themVsUsPlayerInput.direction;
    }

    @Override public String toString() {
        return "PlayerInput[" + sequenceNo + "," + speed + "," + direction + "]";
    }
}

