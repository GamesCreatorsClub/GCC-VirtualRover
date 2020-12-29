package org.ah.piwars.fishtank.game.fish;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;

import org.ah.piwars.fishtank.game.FishtankGame;
import org.ah.themvsus.engine.common.game.Game;
import org.ah.themvsus.engine.common.game.GameObject;
import org.ah.themvsus.engine.common.game.GameObjectFactory;
import org.ah.themvsus.engine.common.game.GameObjectWithPosition;
import org.ah.themvsus.engine.common.game.GameObjectWithPositionAndOrientation;
import org.ah.themvsus.engine.common.transfer.Serializer;

public abstract class Fish extends GameObjectWithPositionAndOrientation {

    enum Behaviour {
        FAST_SWIMMING,
        SLOW_SWIMMING,
        SHOALING,
        IN_SHOAL,
        MIRROR
    }

    protected static final float SMALL_ANGLE = MathUtils.PI / 64f;

    protected float speed = 0.4f;
    protected Behaviour behaviour = Behaviour.FAST_SWIMMING;

    protected Matrix4 transformation = new Matrix4();

    private Quaternion tempQuaternion = new Quaternion();
    private Vector3 direction = new Vector3();
    private Vector3 temp = new Vector3();
    protected Ray ray = new Ray();

    public Fish(GameObjectFactory factory, int id) {
        super(factory, id);
    }

    @Override
    public void free() {
        super.free();
    }

    public void updateTransformation() {
        transformation.set(getPosition(), getOrientation());
    }

    @Override
    public void process(Game g, Iterable<GameObjectWithPosition> objects) {
        super.process(g, objects);

        FishtankGame game = (FishtankGame)g;

        Quaternion orientation = getOrientation();
        Vector3 position = getPosition();
        float speed = getSpeed();

        float speed_tmp = speed * 60 * 3; // 3seconds window

        float speed_distance2 = speed_tmp * speed_tmp;

        ray.origin.set(position);
        direction.set(1f, 0, 0);
        direction.mul(orientation).nor();
        ray.direction.set(direction);

        float forwardDistance = game.distanceToEdge(ray, temp); // Using direction as temp vector

        if (forwardDistance < speed_distance2) {
            Quaternion newDirection = obstacleRays(game, direction, speed_distance2);

            tempQuaternion.set(orientation);
            tempQuaternion.mul(newDirection);

            orientation.slerp(tempQuaternion, (1f - forwardDistance / speed_distance2) * 0.1f);

            direction.set(1f, 0, 0);
            direction.mul(orientation).nor();
        }
        direction.scl(speed);
        position.add(direction);

        updateTransformation();
    }

    public abstract BoundingBox getBoundingBox();

    public boolean checkForCollision(GameObjectWithPosition object, Iterable<GameObjectWithPosition> objects) {
        Vector3 objectPosition = object.getPosition();
        float objectCentreX = objectPosition.x;
        float objectCentreY = objectPosition.y;
        for (GameObjectWithPosition o : objects) {
            if (o != object) {
                Vector3 otherPos = o.getPosition();
                float centreX = otherPos.x;
                float centreY = otherPos.y;

                float distancesquared = ((objectCentreX - centreX) * (objectCentreX - centreX)) + ((objectCentreY - centreY) * (objectCentreY - centreY));

                if (distancesquared < (120 * 120)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void serialize(boolean full, Serializer serializer) {
        super.serialize(full, serializer);
        int s = (int)(speed * 200);
        serializer.serializeUnsignedByte(s);
    }

    @Override
    public void deserialize(boolean full, Serializer serializer) {
        super.deserialize(full, serializer);
        int s = serializer.deserializeUnsignedByte();

        speed = s / 200f;
    }

    @Override
    public int size(boolean full) {
        return super.size(full) + 1;
    }

    @Override
    protected GameObject copyInt(GameObject newObject) {
        super.copyInt(newObject);

        Fish fish = (Fish)newObject;
        fish.speed = speed;

        return newObject;
    }

    @Override
    public String toString() {
        return "Rover" + (changed ? debugChangedText + "*[" : "[") + id + ",(" + position.x + "," + position.y + ")]";
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public float dst(float a, float b) {
        if (a - b < 0) {
            return b - a;
        }
        return a - b;
    }

    private Quaternion obstacleRays(FishtankGame game, Vector3 forward, float minDistance) {

        for (int i = 0; i < BoidHelper.DIRECTION.length; i++) {
            ray.direction.set(forward).mul(BoidHelper.DIRECTION[i]);

            float d2 = game.distanceToEdge(ray, temp); // Using direction as temp vector

            if (d2 > minDistance) {
                return BoidHelper.NORMALISED_DIRECTION[i];
            }
        }

        ray.direction.set(forward);

        return BoidHelper.NORMALISED_DIRECTION[0];
    }

    public static float abs(float a) {
        if (a < 0f) {
            return -a;
        }
        return a;
    }
}
