package org.ah.piwars.virtualrover.game.rovers;

import org.ah.piwars.virtualrover.input.PiWarsPlayerInput;
import org.ah.themvsus.engine.common.input.PlayerInput;

public class FourSteeringWheelsRoverControls implements RoverControls {

    public FourSteeringWheelsRoverControls() {

    }

    @Override
    public void processPlayerInput(Rover rover, PlayerInput playerInput) {
        PiWarsPlayerInput piwarsPlayerInput = (PiWarsPlayerInput)playerInput;

        float moveX = piwarsPlayerInput.moveX();
        float moveY = piwarsPlayerInput.moveY();

        float rotateX = piwarsPlayerInput.rotateX();
        float rotateY = piwarsPlayerInput.rotateY();

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
            rover.setSpeed(0f);
            rover.setDirection(0f);
            rover.setTurnSpeed(0f);
        } else if (Math.abs(rotateX) < 0.1f) {
            rover.setSpeed(moveDistance * piwarsPlayerInput.getDesiredForwardSpeed());
            rover.setDirection((float)(Math.atan2(moveX, moveY) * 180 / Math.PI));
            rover.setTurnSpeed(0f);
        } else if (moveDistance < 0.1f) {
            rover.setSpeed(0f);
            rover.setDirection(0f);
            rover.setTurnSpeed(rotateX * piwarsPlayerInput.getDesiredRotationSpeed());
        } else {
            rover.setSpeed(moveDistance * piwarsPlayerInput.getDesiredForwardSpeed());
            rover.setDirection((float)(Math.atan2(moveX, moveY) * 180 / Math.PI));
            rover.setTurnSpeed(rotateX * piwarsPlayerInput.getDesiredRotationSpeed());
        }
    }
}
