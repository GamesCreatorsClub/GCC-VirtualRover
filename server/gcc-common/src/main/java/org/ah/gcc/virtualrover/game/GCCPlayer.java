package org.ah.gcc.virtualrover.game;

import com.badlogic.gdx.math.Vector3;

import org.ah.gcc.virtualrover.game.rovers.RoverType;
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

    private RoverType roverType;

    public GCCPlayer(GameObjectFactory factory, int id) {
        super(factory, id);
        setRoverType(RoverType.GCC);
    }

    public void setRoverType(RoverType roverType) {
        this.roverType = roverType;
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
        roverType.getRoverDefinition().getRoverControls().processPlayerInputs(this, playerInputs);
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
