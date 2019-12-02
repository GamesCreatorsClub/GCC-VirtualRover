package org.ah.gcc.virtualrover.input;

import org.ah.themvsus.engine.common.input.PlayerInput;
import org.ah.themvsus.engine.common.input.PlayerInputs;

public class GCCPlayerInputs extends PlayerInputs {

    @Override
    public PlayerInput newPlayerInput() {
        return GCCPlayerInput.INPUTS_FACTORY.obtain();
    }

    @Override public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("PLayerInputs[\n");
        for (int i = 0; i < inputs.size; i++) {
            sb.append(inputs.get(i)).append("\n");
        }
        sb.append("]");
        return sb.toString();
    }
}
