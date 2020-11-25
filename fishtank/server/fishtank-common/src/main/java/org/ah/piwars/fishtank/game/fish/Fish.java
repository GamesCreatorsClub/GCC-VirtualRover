package org.ah.piwars.fishtank.game.fish;

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

    protected static final float BALLOONS_RADIUS = 35;
    protected static final float SHARP_POINT_LENGTH = 130;

    protected float speed = 0.1f;

    protected Matrix4 transformation = new Matrix4();

    protected Quaternion tempQuaternion = new Quaternion();

    protected Vector3 direction = new Vector3();

    protected Ray ray = new Ray();

    protected BoundingBox boundingBox = new BoundingBox(
            new Vector3(-2.3f, -12.8f, -8.2f),
            new Vector3(2.3f, 12.7f, 7.7f));

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
        ray.direction.set(1f, 0f, 0f);
        ray.direction.mul(orientation).nor();

        float d2 = game.distanceToEdge(ray, direction); // Using direction as temp vector

        if (dst(position.x, FishtankGame.HALF_WIDTH) < 5
                || dst(position.x, -FishtankGame.HALF_WIDTH) < 5
                || dst(position.z, FishtankGame.HALF_HEIGHT) < 5
                || dst(position.z, -FishtankGame.HALF_HEIGHT) < 5
                ) {

            float angle = 0.5f;
            tempQuaternion.setFromAxis(0f, 1f, 0f, angle);
            System.out.println(String.format("Too close to the side for angle %.2f", angle));
            orientation.mul(tempQuaternion);

            direction.set(1f, 0f, 0f);
            direction.mul(orientation);
            direction.nor();
        } else if (d2 < speed_distance2) {
             // turn
             float r = d2 / speed_distance2;
             speed = speed * r;
             float angle = 0.1f + 0.5f * (1 - r);
             System.out.println(String.format("Turning at speed %.3f ratio %.2f for angle %.2f", speed, r, angle));
             tempQuaternion.setFromAxis(0f, 1f, 0f, angle);
             orientation.mul(tempQuaternion);

             direction.set(1f, 0f, 0f);
             direction.mul(orientation);
             direction.nor();
         } else if (dst(position.x, FishtankGame.HALF_WIDTH) < 15
                 || dst(position.z, FishtankGame.HALF_HEIGHT) < 15) {

             float angle = 0.2f;
             tempQuaternion.setFromAxis(0f, 1f, 0f, angle);
             orientation.mul(tempQuaternion);

             direction.set(1f, 0f, 0f);
             direction.mul(orientation);
             direction.nor();
         } else {
             direction.set(ray.direction);
         }

         direction.scl(speed);
         position.add(direction);
         updateTransformation();
//
//
//         direction.set(baseDirection);
//         direction.mul(orientation);
//
//
//
//
//
//
//         float z = position.z;
//         position.z = 0;
//         float r1 = position.dst(0f, 0f, 0f);
//         position.z = z;
//
//         translation.set(direction);
//         translation.scl(getSpeed());
//         position.add(translation);
//
//         z = position.z;
//         position.z = 0;
//         float r2 = position.dst(0f, 0f, 0f);
//         position.z = z;
//
//         translation.set(position).nor().scl((r2 - r1) * 0.95f);
//         position.add(translation);
//
//         orientation.setFromAxisRad(0f, 1f, 0f, atan2(position.x, position.z));
    }

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
}
