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
        sb.append("FishtankPlayerInputs[\n");
        sb.append(toStringInternal());
        sb.append("]");
        return sb.toString();
    }

    public boolean getPlayerInput(FishtankPlayerInput playerInput) {
        if (inputs.isEmpty()) {
            return false;
        }
        FishtankPlayerInput fishtankPlayerInput = (FishtankPlayerInput) inputs.get(inputs.size - 1);
        playerInput.assignFrom(fishtankPlayerInput);
        clean();
        return true;
    }
}
