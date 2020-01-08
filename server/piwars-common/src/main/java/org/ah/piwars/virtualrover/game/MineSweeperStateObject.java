package org.ah.piwars.virtualrover.game;

import org.ah.themvsus.engine.common.game.GameObject;
import org.ah.themvsus.engine.common.game.GameObjectFactory;
import org.ah.themvsus.engine.common.game.GameObjectType;
import org.ah.themvsus.engine.common.transfer.Serializer;

public class MineSweeperStateObject extends GameObject {

    private int stateBits = 0x0;

    public MineSweeperStateObject(GameObjectFactory factory, int id) {
        super(factory, id);
        nonInteractive = true;
    }

    @Override
    public GameObjectType getType() { return PiWarsGameTypeObject.MineSweeperStateObject; }

    public int getStateBits() {
        return stateBits;
    }

    public void setStateBits(int stateBits) {
        changed = changed || this.stateBits != stateBits;
        this.stateBits = stateBits;
    }

    @Override
    public void serialize(boolean full, Serializer serializer) {
        super.serialize(full, serializer);

        serializer.serializeUnsignedShort(stateBits);
    }

    @Override
    public void deserialize(boolean full, Serializer serializer) {
        super.deserialize(full, serializer);

        int stateBits = serializer.deserializeUnsignedShort();
        setStateBits(stateBits);
    }

    @Override
    public int size(boolean full) {
        return super.size(full) + 2;
    }

    @Override
    protected GameObject copyInt(GameObject newObject) {
        super.copyInt(newObject);
        MineSweeperStateObject mineSweeperStateObject = (MineSweeperStateObject)newObject;
        mineSweeperStateObject.stateBits = stateBits;
        return mineSweeperStateObject;
    }

    @Override
    public String toString() {
        return "GameMessage" + (changed ? "*[" : "[") + id + ", stateBits=" + Integer.toHexString(stateBits) + "\"]";
    }
}
