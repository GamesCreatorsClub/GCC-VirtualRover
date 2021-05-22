package org.ah.piwars.fishtank.game.fish;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;

import org.ah.piwars.fishtank.game.FishtankGameTypeObject;
import org.ah.themvsus.engine.common.game.Game;
import org.ah.themvsus.engine.common.game.GameObject;
import org.ah.themvsus.engine.common.game.GameObjectFactory;
import org.ah.themvsus.engine.common.game.GameObjectType;
import org.ah.themvsus.engine.common.game.GameObjectWithPosition;
import org.ah.themvsus.engine.common.game.GameObjectWithPositionAndOrientation;
import org.ah.themvsus.engine.common.transfer.Serializer;

public class BallObject extends GameObjectWithPositionAndOrientation {

    public static BoundingBox BOUNDING_BOX = new BoundingBox(
            new Vector3(-2.3f, -12.8f, -8.2f),
            new Vector3(2.3f, 12.7f, 7.7f));

    protected Vector3 local_position = new Vector3();

    public BallObject(GameObjectFactory factory, int id) {
        super(factory, id);
    }

    private float size = 0f;

    public BoundingBox getBoundingBox() {
        return BOUNDING_BOX;
    }

    @Override
    public void setPosition(float x, float y, float z) {
        super.setPosition(x, y, z);
        local_position.set(position);
    }

    @Override
    public GameObjectType getType() { return FishtankGameTypeObject.Ball; }


    @Override
    public void serialize(boolean full, Serializer serializer) {
        super.serialize(full, serializer);
        int s = (int)(size * 200);
        serializer.serializeUnsignedByte(s);
    }

    @Override
    public void deserialize(boolean full, Serializer serializer) {
        super.deserialize(full, serializer);
        int s = serializer.deserializeUnsignedByte();

        size = s / 200f;
    }

    @Override
    public int size(boolean full) {
        return super.size(full) + 1;
    }

    @Override
    protected GameObject copyInt(GameObject newObject) {
        super.copyInt(newObject);

        BallObject ball = (BallObject)newObject;
        ball.size = size;

        BallObject gameObjectWithPosition = (BallObject)newObject;
        gameObjectWithPosition.local_position.set(local_position);

        return newObject;
    }

    public float getSize() {
        return size;
    }

    public void setSize(float size) {
        this.size = size;
    }


    @Override
    public void process(Game g, Iterable<GameObjectWithPosition> objects) {
        super.process(g, objects);
        if (local_position.y > -55f) {
            local_position.y -= 0.1f;
        } else {
            g.removeGameObject(getId());
        }

        size = (local_position.y + 55f) / 100f;
        if (size < 0.1f) {
            size = 0.1f;
        }

        float a = local_position.y / 5f;

        position.y = local_position.y - 1f * Math.abs((float)Math.sin(a));
        position.x = local_position.x + 3f * (float)Math.cos(a);
        position.z = local_position.z + 3f * Math.abs((float)Math.cos(a));
    }
}
