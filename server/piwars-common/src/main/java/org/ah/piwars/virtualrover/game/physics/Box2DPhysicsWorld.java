package org.ah.piwars.virtualrover.game.physics;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Shape2D;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.IntMap;

import org.ah.piwars.virtualrover.game.PiWarsCollidableObject;
import org.ah.piwars.virtualrover.game.PiWarsGame;
import org.ah.piwars.virtualrover.game.rovers.Rover;
import org.ah.themvsus.engine.common.game.Game.GameObjectAddedListener;
import org.ah.themvsus.engine.common.game.Game.GameObjectRemovedListener;
import org.ah.themvsus.engine.common.game.GameObject;
import org.ah.themvsus.engine.common.game.GameObjectWithPosition;
import org.ah.themvsus.engine.common.game.MovingGameObjectWithPositionAndOrientation;

import java.util.List;

public class Box2DPhysicsWorld implements GameObjectAddedListener, GameObjectRemovedListener {

    public World world;
    protected PiWarsGame piwarsGame;

    private IntMap<PhysicsObject> knownObjects = new IntMap<>();
    private Body arenaBody;
    private int roverId;
    private Vector2 roverLinearVelocity = new Vector2();
    private float roverAngularVelocity;

    public Box2DPhysicsWorld(PiWarsGame piwarsGame, List<Shape2D> arenaShapes) {
        this.piwarsGame = piwarsGame;

        world = new World(new Vector2(0f, 0f), true);

        piwarsGame.addGameObjectAddedListener(this);
        piwarsGame.addGameObjectRemovedListener(this);

        createArenaObjects(arenaShapes);
    }

    private void createArenaObjects(List<Shape2D> shapes) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyType.StaticBody;
        bodyDef.position.set(0, 0);

        arenaBody = world.createBody(bodyDef);

        for (Shape2D collidableShape : shapes) {
            Shape shape;
            if (collidableShape instanceof Polygon) {
                Polygon polygon = (Polygon)collidableShape;
                ChainShape chainShape = new ChainShape();
                chainShape.createLoop(polygon.getVertices());
                shape = chainShape;
            } else {
                throw new RuntimeException("Cannot cater for " + collidableShape.getClass().getSimpleName() + "(" + collidableShape.getClass().getName() + ") shape");
            }
            arenaBody.createFixture(shape, 0.0f);
            shape.dispose();
        }
    }

    public void updateObjectPositions() {
        for (PhysicsObject physicalObject : knownObjects.values()) {
            MovingGameObjectWithPositionAndOrientation gameObject = piwarsGame.getCurrentGameState().get(physicalObject.objectId);

            physicalObject.body.setTransform(gameObject.getPosition().x, gameObject.getPosition().y, gameObject.getBearingRad());
        }
    }

    @Override
    public void gameObjectAdded(GameObject gameObject) {
        if (gameObject instanceof MovingGameObjectWithPositionAndOrientation && gameObject instanceof PiWarsCollidableObject) {
            knownObjects.put(gameObject.getId(), new PhysicsObject((MovingGameObjectWithPositionAndOrientation)gameObject));

            if (gameObject instanceof Rover) {
                roverId = gameObject.getId();
            }
        }
    }

    @Override
    public void gameObjectRemoved(GameObject gameObject) {
        if (knownObjects.containsKey(gameObject.getId())) {
            PhysicsObject removedPhysicsObject = knownObjects.remove(gameObject.getId());
            removedPhysicsObject.dispose();
        }
    }

    public boolean tryMovingRover(Rover rover, Iterable<GameObjectWithPosition> objects) {
        if (roverId > 0) {
            PhysicsObject physicsObject = knownObjects.get(roverId);

            Vector3 roverPosition = rover.getPosition();

            float factor = 1000000f / piwarsGame.getGameTickMicros();

            roverLinearVelocity.set((roverPosition.x - physicsObject.lastX) * factor, (roverPosition.y - physicsObject.lastY) * factor);
            roverAngularVelocity = rover.getBearingRad() - physicsObject.lastAngle;
            if (roverAngularVelocity > MathUtils.PI) {
                roverAngularVelocity = roverAngularVelocity - 2f * MathUtils.PI;
            } else if (roverAngularVelocity < -MathUtils.PI) {
                roverAngularVelocity = roverAngularVelocity + 2f * MathUtils.PI;
            }
            roverAngularVelocity = roverAngularVelocity * factor;
        }

        return true; // We've recorded what rover wanted to do - so we can prevent it doing it for now.
    }

    public void updateWorld() {

        float deltaTime = piwarsGame.getGameTickMicros() / 1000000f;

        if (roverId > 0) {
            PhysicsObject physicsObject = knownObjects.get(roverId);

            physicsObject.body.setLinearVelocity(roverLinearVelocity);
            physicsObject.body.setAngularVelocity(roverAngularVelocity);
        }

        world.step(deltaTime, 5, 8);

        boolean updateRover = true;

        for (PhysicsObject physicsObject : knownObjects.values()) {
            MovingGameObjectWithPositionAndOrientation gameObject = piwarsGame.getCurrentGameState().get(physicsObject.objectId);

            if (updateRover || !(gameObject instanceof Rover)) {
                Body body = physicsObject.body;
                gameObject.setPosition(body.getPosition().x, body.getPosition().y);
                if (gameObject instanceof Rover) {
//                    float oldBearing = gameObject.getBearingRad();
                    gameObject.setBearingRad(body.getAngle());
//                    if (oldBearing != gameObject.getBearingRad()) {
//                        System.out.println(String.format("OB: %2.5f, LA: %2.5f, NB: %2.5f, BA: %2.5f, AV: %2.5f", oldBearing, physicsObject.lastAngle, gameObject.getBearingRad(), body.getAngle(), roverAngularVelocity));
//                    }
                } else {
                    gameObject.setBearingRad(body.getAngle());
                }

                //body.setTransform(body.getPosition().x, body.getPosition().y, gameObject.getBearingRad());
                body.setLinearVelocity(0f, 0f);
                body.setAngularVelocity(0f);
                physicsObject.lastX = body.getPosition().x;
                physicsObject.lastY = body.getPosition().y;
                physicsObject.lastAngle = body.getAngle();
            }
        }

        if (roverId > 0) {
            roverLinearVelocity.set(0f, 0f);
            roverAngularVelocity = 0f;
        }
    }

    private class PhysicsObject {
        public int objectId;
        public Body body;
        public float lastX;
        public float lastY;
        public float lastAngle;

        public PhysicsObject(MovingGameObjectWithPositionAndOrientation gameObject) {
            this.objectId = gameObject.getId();

            boolean bullet;
            float density;
            float friction;
            if (gameObject instanceof Rover) {
                density = 4f;
                bullet = true;
                friction = 0.7f;
            } else {
                density = 0.2f;
                bullet = false;
                friction = 0.7f;
            }

            lastX = gameObject.getPosition().x;
            lastY = gameObject.getPosition().y;
            lastAngle = gameObject.getBearingRad();

            gameObject.setPosition(0, 0);

            body = createBody(gameObject.getPosition().x, gameObject.getPosition().y, lastAngle, ((PiWarsCollidableObject)gameObject).getCollisionPolygons(), friction, bullet, density);

            gameObject.setPosition(lastX, lastY);
        }

        private Body createBody(float x, float y, float bearing, List<Shape2D> shapes, float friction, boolean bullet, float density) {
            BodyDef bodyDef = new BodyDef();
            bodyDef.type = BodyType.DynamicBody;
            bodyDef.bullet = bullet;
            bodyDef.allowSleep = false;
            bodyDef.linearDamping = 100f;
            bodyDef.angularDamping = 100f;
            // bodyDef.position.set(x, y);

            Body body = world.createBody(bodyDef);

            for (Shape2D collidableShape : shapes) {
                Shape shape;
                if (collidableShape instanceof Polygon) {
                    Polygon polygon = (Polygon)collidableShape;
                    PolygonShape polygonShape = new PolygonShape();

                    polygonShape.set(polygon.getVertices());
                    shape = polygonShape;
                } else if (collidableShape instanceof Circle) {
                    Circle circle = (Circle)collidableShape;
                    CircleShape circleShape = new CircleShape();
                    circleShape.setPosition(new Vector2(circle.x, circle.y));
                    circleShape.setRadius(circle.radius);
                    shape = circleShape;
                } else {
                    throw new RuntimeException("Cannot cater for " + collidableShape.getClass().getSimpleName() + "(" + collidableShape.getClass().getName() + ") shape");
                }
                FixtureDef fixtureDef = new FixtureDef();
                fixtureDef.shape = shape;
                fixtureDef.density = density;
                fixtureDef.friction = friction;
                fixtureDef.restitution = 0f;

                /*fixture = */body.createFixture(fixtureDef);
                shape.dispose();
            }

            body.setTransform(x, y, bearing);

            return body;
        }

        public void dispose() {
        }
    }
}
