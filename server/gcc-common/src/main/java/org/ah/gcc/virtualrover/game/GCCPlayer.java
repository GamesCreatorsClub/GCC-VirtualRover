package org.ah.gcc.virtualrover.game;

import com.badlogic.gdx.math.Vector3;

import org.ah.gcc.virtualrover.game.rovers.RoverType;
import org.ah.gcc.virtualrover.input.GCCPlayerInput;
import org.ah.gcc.virtualrover.message.GCCPlayerServerUpdateMessage;
import org.ah.themvsus.engine.common.game.Game;
import org.ah.themvsus.engine.common.game.GameObject;
import org.ah.themvsus.engine.common.game.GameObjectFactory;
import org.ah.themvsus.engine.common.game.GameObjectWithPosition;
import org.ah.themvsus.engine.common.game.Player;
import org.ah.themvsus.engine.common.input.PlayerInput;
import org.ah.themvsus.engine.common.message.Message;
import org.ah.themvsus.engine.common.transfer.Serializer;

public class GCCPlayer extends Player {

    private float desiredForwardSpeed = 300f;
    private float desiredRotationSpeed = 300f;

    private RoverType roverType = RoverType.GCC;

    public GCCPlayer(GameObjectFactory factory, int id) {
        super(factory, id);
    }

    public RoverType getRoverType() {
        return roverType;
    }

    @Override
    public void process(Game game, Iterable<GameObjectWithPosition> objects) {
         super.process(game, objects);
    }

    public boolean checkForCollision(GameObjectWithPosition object, Iterable<GameObjectWithPosition> objects) {
        Vector3 objectPosition = object.getPosition();
        float objectCentreX = objectPosition.x;
        float objectCentreY = objectPosition.y;
        for (GameObjectWithPosition o : objects) {
            if (o != object) {
                Vector3 otherPos = o.getPosition();
                float centreX = otherPos.x;
                float centreY = otherPos.y;

                float distancesquared = ((objectCentreX - centreX) * (objectCentreX - centreX)) + ((objectCentreY - centreY) * (objectCentreY - centreY));

                if (distancesquared < (120 * 120)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void processPlayerInputs(PlayerInput playerInputs) {
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
            speed = 0f;
            turnSpeed = 0f;
            // velocity.set(0f, 0f, 0f);

        } else if (Math.abs(rotateX) < 0.1f) {
            speed = moveDistance * desiredForwardSpeed;
            direction = (float)(Math.atan2(moveX, moveY) * 180 / Math.PI);
            turnSpeed = 0f;
        } else if (moveDistance < 0.1f) {
            turnSpeed = rotateX * desiredRotationSpeed;
            speed = 0f;
        } else {
            speed = moveDistance * desiredForwardSpeed;
            direction = (float)(Math.atan2(moveX, moveY) * 180 / Math.PI);
            turnSpeed = rotateX * desiredRotationSpeed;
        }
    }

    @Override
    public void performCommand(Message command) {
        if (command instanceof GCCPlayerServerUpdateMessage) {
            super.performCommand(command);
        } else {
            super.performCommand(command);
        }
    }

    @Override
    public void serialize(boolean full, Serializer serializer) {
        super.serialize(full, serializer);
        serializer.serializeByte((byte)roverType.getId());
    }

    @Override
    public void deserialize(boolean full, Serializer serializer) {
        super.deserialize(full, serializer);
        roverType = RoverType.getById(serializer.deserializeByte());
    }

    @Override
    protected GameObject copyInt(GameObject newObject) {
        super.copyInt(newObject);

        GCCPlayer gccPlayer = (GCCPlayer)newObject;
        gccPlayer.roverType = roverType;

        return newObject;
    }
}
