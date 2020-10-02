package org.ah.piwars.virtualrover.game.objects;

import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Shape2D;

import org.ah.piwars.virtualrover.game.PiWarsCollidableObject;
import org.ah.piwars.virtualrover.game.PiWarsGameTypeObject;
import org.ah.themvsus.engine.common.game.GameObjectFactory;
import org.ah.themvsus.engine.common.game.GameObjectType;
import org.ah.themvsus.engine.common.game.MovingGameObjectWithPositionAndOrientation;
import org.ah.themvsus.engine.common.transfer.Serializer;

import java.util.ArrayList;
import java.util.List;

public class FishTowerObject extends MovingGameObjectWithPositionAndOrientation implements PiWarsCollidableObject {

    public static int TOWER_WIDTH = 200;
    public static int WALL_WIDTH = 5;

    private List<Shape2D> collisionPolygons = new ArrayList<Shape2D>();

    public FishTowerObject(GameObjectFactory factory, int id) {
        super(factory, id);
        collisionPolygons.add(new Polygon(makeLeftPolygonVertices()));
        collisionPolygons.add(new Polygon(makeRightPolygonVertices()));
        collisionPolygons.add(new Polygon(makeBackPolygonVertices()));
    }

    @Override
    public GameObjectType getType() { return PiWarsGameTypeObject.FishTowerObject; }

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
        return collisionPolygons;
    }

    private float[] makeLeftPolygonVertices() {
        float half = TOWER_WIDTH / 2.0f;
        return new float[] { -half, half, half, half,
                            half, half - WALL_WIDTH, -half, half - WALL_WIDTH};
    }

    private float[] makeRightPolygonVertices() {
        float half = TOWER_WIDTH / 2.0f;
        return new float[] { -half, -half, half, -half,
                            half, -half + WALL_WIDTH, -half, -half + WALL_WIDTH};
    }

    private float[] makeBackPolygonVertices() {
        float half = TOWER_WIDTH / 2.0f;
        return new float[] { half - WALL_WIDTH, half - WALL_WIDTH, half, half - WALL_WIDTH,
                            half, -half + WALL_WIDTH, half - WALL_WIDTH, -half + WALL_WIDTH};
    }
}
