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

public class GolfBallObject extends MovingGameObjectWithPositionAndOrientation implements PiWarsCollidableObject {

    public static int GOLF_BALL_DIAMETER = 42;

    private Circle circle = new Circle(0, 0, GOLF_BALL_DIAMETER / 2);
    private List<Shape2D> collisionPolygons = new ArrayList<Shape2D>();

    public GolfBallObject(GameObjectFactory factory, int id) {
        super(factory, id);
        collisionPolygons.add(circle);
    }

    @Override
    public GameObjectType getType() { return PiWarsGameTypeObject.GolfBallObject; }

    public Circle getCircle() {
        circle.x = position.x;
        circle.y = position.y;
        return circle;
    }

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

    @Override
    public List<Shape2D> getCollisionPolygons() {
        getCircle(); // update circle
        return collisionPolygons;
    }
}
