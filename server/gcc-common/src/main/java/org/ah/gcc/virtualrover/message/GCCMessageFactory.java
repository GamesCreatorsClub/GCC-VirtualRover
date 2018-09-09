package org.ah.gcc.virtualrover.message;

import org.ah.themvsus.engine.common.message.Message;
import org.ah.themvsus.engine.common.message.MessageCode;
import org.ah.themvsus.engine.common.message.MessageFactory;
import org.ah.themvsus.engine.common.utils.SynchronizedCircularBuffer;

import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;

public class GCCMessageFactory extends MessageFactory {

    public GCCMessageFactory() {
        for (MessageCode messageCode : MessageCode.values()) {
            freeObjects.put(messageCode, new SynchronizedCircularBuffer<Message>());
        }
    }

    public GCCPlayerInputMessage createPlayerInputCommand() {
        GCCPlayerInputMessage playerInputCommand = newMessage(MessageCode.PlayerInput);
        return playerInputCommand;
    }

    public GCCPlayerServerUpdateMessage playerServerUpdateCommand(int id, int serverFrameNo, Vector3 position, Vector3 velocity, Quaternion orientation, float turnSpeed, float health, float turretBearing) {
        GCCPlayerServerUpdateMessage serverUpdateCommand = newMessage(MessageCode.PlayerServerUpdate);
        serverUpdateCommand.setValues(id, serverFrameNo, position, velocity, orientation, turnSpeed, health, turretBearing);
        return serverUpdateCommand;
    }

    @Override
    protected Message createNewObject(MessageCode gameObjectType) {
        if (gameObjectType == MessageCode.PlayerServerUpdate) {
            return new GCCPlayerServerUpdateMessage(this);
        } else if (gameObjectType == MessageCode.PlayerInput) {
            return new GCCPlayerInputMessage(this);
        }
        return gameObjectType.newObject(this);
    }
}
