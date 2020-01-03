package org.ah.piwars.virtualrover.game.rovers;

import org.ah.piwars.virtualrover.input.PiWarsPlayerInput;
import org.ah.themvsus.engine.common.input.PlayerInput;

public class TankRoverControls implements RoverControls {

    @Override
    public void processPlayerInput(Rover rover, PlayerInput playerInput) {
        PiWarsPlayerInput piwarsPlayerInput = (PiWarsPlayerInput)playerInput;

        float moveX = piwarsPlayerInput.moveX();
        float moveY = piwarsPlayerInput.moveY();

        float rotateX = piwarsPlayerInput.rotateX();
        // float rotateY = piwarsPlayerInput.rotateY();

        if (Math.abs(rotateX) < Math.abs(moveX)) {
            rotateX = moveX;
        }

        if (moveY < 0.1f && moveY > -0.1f && rotateX < 0.1f && rotateX > -0.1f) {
            rover.setSpeed(0f);
            rover.setDirection(0f);
            rover.setTurnSpeed(0f);
        } else if (moveY < 0.1f && moveY > -0.1f) {
            rover.setSpeed(0f);
            rover.setDirection(0f);
            rover.setTurnSpeed(rotateX * piwarsPlayerInput.getDesiredRotationSpeed());
        } else {
            rover.setSpeed(moveY * piwarsPlayerInput.getDesiredForwardSpeed());
            rover.setDirection(0f);
            rover.setTurnSpeed(rotateX * piwarsPlayerInput.getDesiredRotationSpeed());
        }
    }
}
