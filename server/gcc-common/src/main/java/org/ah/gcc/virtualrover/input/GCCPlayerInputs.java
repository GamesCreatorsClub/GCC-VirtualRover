package org.ah.gcc.virtualrover.input;

import org.ah.themvsus.engine.common.input.PlayerInputs;

public class GCCPlayerInputs extends PlayerInputs {

    public void addInputs(int currentFrameNo, float moveX, float moveY, float rotateX, float rotateY) {
        GCCPlayerInput playerInput = newPlayerInput();
        playerInput.moveX(moveX);
        playerInput.moveY(moveY);
        playerInput.rotateX(rotateX);
        playerInput.rotateY(rotateY);
        super.addInputs(currentFrameNo, playerInput);
    }

    @Override
    public GCCPlayerInput newPlayerInput() {
        return (GCCPlayerInput)GCCPlayerInput.INPUTS_FACTORY.obtain();
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
