package org.ah.gcc.virtualrover.message;

import static org.ah.themvsus.engine.common.message.MessageCode.PlayerServerUpdate;

import org.ah.themvsus.engine.common.message.MessageCode;
import org.ah.themvsus.engine.common.message.MessageFactory;
import org.ah.themvsus.engine.common.message.ServerUpdateMessage;
import org.ah.themvsus.engine.common.transfer.Serializer;

import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;

public class GCCPlayerServerUpdateMessage extends ServerUpdateMessage {

    private float turretBearing;

    GCCPlayerServerUpdateMessage(MessageFactory messageFactory) {
        super(messageFactory);
    }

    @Override
    public MessageCode getType() { return PlayerServerUpdate; }

    public float getTurretBearing() {
        return turretBearing;
    }

    public void setValues(int id, int serverFrameNo, Vector3 position, Vector3 velocity, Quaternion orientation, float turnSpeed, float health, float turretBearing) {
        super.setValues(id, serverFrameNo, position, velocity, orientation, turnSpeed, health);
        this.turretBearing = turretBearing;
    }

    @Override
    protected void deserializeImpl(Serializer deserializer) {
        super.deserializeImpl(deserializer);

        turretBearing = deserializer.deserializeFloat();
    }

    @Override
    protected void serializeImpl(Serializer serializer) {
        super.serializeImpl(serializer);
        serializer.serializeFloat(turretBearing);
    }
}
