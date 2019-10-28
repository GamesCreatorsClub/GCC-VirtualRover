package org.ah.gcc.virtualrover.input;

import org.ah.themvsus.engine.common.input.PlayerInputs;

public class GCCPlayerInputs extends PlayerInputs {

    public void addInputs(int currentFrameNo, GCCPlayerInput playerInput) {
        GCCPlayerInput newPlayerInput = newPlayerInput();
        newPlayerInput.moveX(playerInput.moveX());
        newPlayerInput.moveY(playerInput.moveY());
        newPlayerInput.rotateX(playerInput.rotateX());
        newPlayerInput.rotateY(playerInput.rotateY());
        newPlayerInput.setDesiredForwardSpeed(playerInput.getDesiredForwardSpeed());
        newPlayerInput.setDesiredRotationSpeed(playerInput.getDesiredRotationSpeed());
        super.addInputs(currentFrameNo, newPlayerInput);
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
