package org.ah.gcc.virtualrover.game.rovers;

import org.ah.gcc.virtualrover.game.GCCPlayer;
import org.ah.gcc.virtualrover.input.GCCPlayerInput;
import org.ah.themvsus.engine.common.input.PlayerInput;

public class FourSteeringWheelsRoverControls implements RoverControls {

    public FourSteeringWheelsRoverControls() {

    }

    @Override
    public void processPlayerInputs(GCCPlayer gccPlayer, PlayerInput playerInputs) {
        GCCPlayerInput gccPlayerInput = (GCCPlayerInput)playerInputs;

        float moveX = gccPlayerInput.moveX();
        float moveY = gccPlayerInput.moveY();

        float rotateX = gccPlayerInput.rotateX();
        float rotateY = gccPlayerInput.rotateY();

        float moveDistance = (float)Math.sqrt(moveX * moveX + moveY * moveY);
        if (moveDistance > 1f) {
            moveDistance = 1f;
        }

        float rotateDistance = (float)Math.sqrt(rotateX * rotateX + rotateY * rotateY);
        if (rotateDistance > 1f) {
            rotateDistance = 1f;
        }

        if (moveDistance < 0.1f && Math.abs(rotateX) < 0.1f) {
            // Stop
            gccPlayer.setSpeed(0f);
            gccPlayer.setDirection(0f);
            gccPlayer.setTurnSpeed(0f);
        } else if (Math.abs(rotateX) < 0.1f) {
            gccPlayer.setSpeed(moveDistance * gccPlayerInput.getDesiredForwardSpeed());
            gccPlayer.setDirection((float)(Math.atan2(moveX, moveY) * 180 / Math.PI));
            gccPlayer.setTurnSpeed(0f);
        } else if (moveDistance < 0.1f) {
            gccPlayer.setSpeed(0f);
            gccPlayer.setDirection(0f);
            gccPlayer.setTurnSpeed(rotateX * gccPlayerInput.getDesiredRotationSpeed());
        } else {
            gccPlayer.setSpeed(moveDistance * gccPlayerInput.getDesiredForwardSpeed());
            gccPlayer.setDirection((float)(Math.atan2(moveX, moveY) * 180 / Math.PI));
            gccPlayer.setTurnSpeed(rotateX * gccPlayerInput.getDesiredRotationSpeed());
        }
    }
}
