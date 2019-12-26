package org.ah.gcc.virtualrover.game;

import org.ah.themvsus.engine.common.game.GameObjectFactory;
import org.ah.themvsus.engine.common.game.GameObjectType;
import org.ah.themvsus.engine.common.game.MovingGameObjectWithPositionAndOrientation;
import org.ah.themvsus.engine.common.transfer.Serializer;

public class BarrelObject extends MovingGameObjectWithPositionAndOrientation {

    public static enum BarrelColour {
        GREEN, RED
    }

    public static BarrelColour[] BARREL_COLOUR_VALUES = BarrelColour.values();

    private BarrelColour barrelColour = BarrelColour.GREEN;

    public BarrelObject(GameObjectFactory factory, int id) {
        super(factory, id);
    }

    public BarrelColour getBarrelColour() {
        return barrelColour;
    }

    public void setBarrelColour(BarrelColour barrelColour) {
        this.changed = this.changed || this.barrelColour != barrelColour;
        this.barrelColour = barrelColour;
    }

    @Override
    public GameObjectType getType() { return GCCGameTypeObject.BarrelObject; }

    @Override
    public void serialize(boolean full, Serializer serializer) {
        super.serialize(full, serializer);
        if (full) {
            serializer.serializeUnsignedByte(barrelColour.ordinal());
        }
    }

    @Override
    public void deserialize(boolean full, Serializer serializer) {
        super.deserialize(full, serializer);
        if (full) {
            int barrelColour = serializer.deserializeUnsignedByte();
            if (barrelColour < 0 || barrelColour >= BARREL_COLOUR_VALUES.length) {
                barrelColour = 0;
            }
            this.barrelColour = BARREL_COLOUR_VALUES[barrelColour];
        }
    }

    @Override
    public int size(boolean full) {
        return super.size(full) + (full ? 1 : 0);
    }
}
