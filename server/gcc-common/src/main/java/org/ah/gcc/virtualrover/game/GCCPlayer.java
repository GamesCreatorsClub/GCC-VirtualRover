package org.ah.gcc.virtualrover.game;

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

    private RoverType roverType = RoverType.GCC;

    public GCCPlayer(GameObjectFactory factory, int id) {
        super(factory, id);
    }

    public RoverType getRoverType() {
        return roverType;
    }

    @Override
    public void process(Game game, Iterable<GameObjectWithPosition> objects) {
        // long deltaMillis = Engine.ENGINE_LOOP_TIME;

        // TODO definitively do something own - no way it will work it passed to parent class
        // super.process(game, objects);
    }

    @Override
    public void processPlayerInputs(PlayerInput playerInputs) {
        GCCPlayerInput themVsUsPlayerInput = (GCCPlayerInput)playerInputs;
// TODO
//        float speed = themVsUsPlayerInput.speed;
//
//        velocity.set(1f, 0f, 0f);
//        velocity.mul(orientation).nor();
//        velocity.scl(speed);
//        this.speed = speed;
//        // velocity.x = (float) Math.cos(bearing * RAD_TO_DEG) * speed;
//        // velocity.y = (float) Math.sin(bearing * RAD_TO_DEG) * speed;
    }

    @Override
    public void performCommand(Message command) {
        if (command instanceof GCCPlayerServerUpdateMessage) {
            super.performCommand(command);
//            GCCPlayerServerUpdateMessage message = (GCCPlayerServerUpdateMessage) command;
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
