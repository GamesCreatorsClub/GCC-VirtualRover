package org.ah.gcc.virtualrover.game;

import org.ah.themvsus.engine.common.game.GameObjectFactory;
import org.ah.themvsus.engine.common.game.GameObjectType;
import org.ah.themvsus.engine.common.game.MovingGameObjectWithPositionAndOrientation;
import org.ah.themvsus.engine.common.transfer.Serializer;

public class BarrelObject extends MovingGameObjectWithPositionAndOrientation {

    public BarrelObject(GameObjectFactory factory, int id) {
        super(factory, id);
    }

    @Override
    public GameObjectType getType() { return GCCGameTypeObject.BarrelObject; }

    @Override
    public void serialize(boolean full, Serializer serializer) {
        super.serialize(full, serializer);
    }

    @Override
    public void deserialize(boolean full, Serializer serializer) {
        super.deserialize(full, serializer);
    }

    @Override
    public int size(boolean full) {
        return super.size(full);
    }
}
