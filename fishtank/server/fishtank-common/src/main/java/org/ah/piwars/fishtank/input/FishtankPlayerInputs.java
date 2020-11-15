package org.ah.piwars.fishtank.input;

import org.ah.themvsus.engine.common.input.PlayerInput;
import org.ah.themvsus.engine.common.input.PlayerInputs;

public class FishtankPlayerInputs extends PlayerInputs {

    @Override
    public PlayerInput newPlayerInput() {
        return FishtankPlayerInput.INPUTS_FACTORY.obtain();
    }

    @Override public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("PiWarsPlayerInputs[\n");
        sb.append(toStringInternal());
        sb.append("]");
        return sb.toString();
    }
}
