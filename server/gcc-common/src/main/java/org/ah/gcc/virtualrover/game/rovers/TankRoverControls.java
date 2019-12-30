package org.ah.gcc.virtualrover.game.rovers;

import org.ah.gcc.virtualrover.input.GCCPlayerInput;
import org.ah.themvsus.engine.common.input.PlayerInput;

public class TankRoverControls implements RoverControls {

    @Override
    public void processPlayerInput(Rover gccPlayer, PlayerInput playerInput) {
        GCCPlayerInput gccPlayerInput = (GCCPlayerInput)playerInput;

        float moveX = gccPlayerInput.moveX();
        float moveY = gccPlayerInput.moveY();

        float rotateX = gccPlayerInput.rotateX();
        // float rotateY = gccPlayerInput.rotateY();

        if (Math.abs(rotateX) < Math.abs(moveX)) {
            rotateX = moveX;
        }

        if (moveY < 0.1f && moveY > -0.1f && rotateX < 0.1f && rotateX > -0.1f) {
            gccPlayer.setSpeed(0f);
            gccPlayer.setDirection(0f);
            gccPlayer.setTurnSpeed(0f);
        } else if (moveY < 0.1f && moveY > -0.1f) {
            gccPlayer.setSpeed(0f);
            gccPlayer.setDirection(0f);
            gccPlayer.setTurnSpeed(rotateX * gccPlayerInput.getDesiredRotationSpeed());
        } else {
            gccPlayer.setSpeed(moveY * gccPlayerInput.getDesiredForwardSpeed());
            gccPlayer.setDirection(0f);
            gccPlayer.setTurnSpeed(rotateX * gccPlayerInput.getDesiredRotationSpeed());
        }
    }
}
