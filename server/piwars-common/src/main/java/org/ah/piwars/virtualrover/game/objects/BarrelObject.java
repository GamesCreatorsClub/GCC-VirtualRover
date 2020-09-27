package org.ah.piwars.virtualrover.game.objects;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Shape2D;

import org.ah.piwars.virtualrover.game.PiWarsCollidableObject;
import org.ah.piwars.virtualrover.game.PiWarsGameTypeObject;
import org.ah.themvsus.engine.common.game.GameObjectFactory;
import org.ah.themvsus.engine.common.game.GameObjectType;
import org.ah.themvsus.engine.common.game.MovingGameObjectWithPositionAndOrientation;
import org.ah.themvsus.engine.common.transfer.Serializer;

import java.util.ArrayList;
import java.util.List;

public class BarrelObject extends MovingGameObjectWithPositionAndOrientation implements PiWarsCollidableObject {
    public static enum BarrelColour {
        GREEN, RED
    }

    public static final int BARREL_DIAMETER = 25;

    public static BarrelColour[] BARREL_COLOUR_VALUES = BarrelColour.values();

    private BarrelColour barrelColour = BarrelColour.GREEN;

    private Circle circle = new Circle(0, 0, BARREL_DIAMETER);
    private List<Shape2D> collisionPolygons = new ArrayList<Shape2D>();

    public BarrelObject(GameObjectFactory factory, int id) {
        super(factory, id);
        collisionPolygons.add(circle);
    }

    public BarrelColour getColour() {
        return barrelColour;
    }

    public void setBarrelColour(BarrelColour barrelColour) {
        this.changed = this.changed || this.barrelColour != barrelColour;
        this.barrelColour = barrelColour;
    }

    @Override
    public GameObjectType getType() { return PiWarsGameTypeObject.BarrelObject; }

    public Circle getCirle() {
        circle.x = position.x;
        circle.y = position.y;
        return circle;
    }

    public Circle getCirleInt() {
        return circle;
    }

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

    @Override
    public List<Shape2D> getCollisionPolygons() {
        getCirle(); // update circle
        return collisionPolygons;
    }
}
