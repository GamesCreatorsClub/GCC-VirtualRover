package org.ah.piwars.virtualrover.game.physics;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Shape2D;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.IntArray;

import org.ah.piwars.virtualrover.engine.utils.CollisionUtils;
import org.ah.piwars.virtualrover.game.PiWarsCollidableObject;
import org.ah.piwars.virtualrover.game.PiWarsGame;
import org.ah.piwars.virtualrover.game.objects.BarrelObject;
import org.ah.piwars.virtualrover.game.rovers.Rover;
import org.ah.themvsus.engine.common.game.GameObject;
import org.ah.themvsus.engine.common.game.GameObjectWithPosition;
import org.ah.themvsus.engine.common.game.GameState;
import org.ah.themvsus.engine.common.game.MovingGameObjectWithPositionAndOrientation;

import java.util.List;

import static com.badlogic.gdx.math.Intersector.overlapConvexPolygons;

import static org.ah.piwars.virtualrover.engine.utils.CollisionUtils.circleOverlapPolygonSeqment;
import static org.ah.piwars.virtualrover.engine.utils.CollisionUtils.findCollidedShape;

public class PhysicsWorld {

    protected PiWarsGame piwarsGame;
    protected IntArray objectIds;

    private Vector2 start = new Vector2();
    private Vector2 end = new Vector2();
    private Vector2 point = new Vector2();
    private Vector2 displacement = new Vector2();
    private Intersector.MinimumTranslationVector minimalDisplacementVector = new Intersector.MinimumTranslationVector();

    private PhysicsObject[] knownObjects = new PhysicsObject[0];
    private Polygon arenasPolygon;

    public PhysicsWorld(PiWarsGame piwarsGame, Polygon worldPolygon) {
        this.piwarsGame = piwarsGame;
        this.arenasPolygon = worldPolygon;
    }

    public void setup(IntArray objectIds) {
        this.objectIds = objectIds;
        if (knownObjects.length != objectIds.size) {
            knownObjects = new PhysicsObject[objectIds.size];
        }
        GameState currentGameState = piwarsGame.getCurrentGameState();
        for (int i = 0; i < knownObjects.length; i++) {
            if (knownObjects[i] == null) {
                knownObjects[i] = new PhysicsObject(currentGameState.get(objectIds.get(i)));
            } else {
                knownObjects[i].update(currentGameState.get(objectIds.get(i)));
            }
        }
    }

    public boolean tryMovingRover(Rover rover, Iterable<GameObjectWithPosition> objects) {
        List<Shape2D> roverPolygons = rover.getCollisionPolygons();


        for (GameObjectWithPosition o : objects) {
            if (o != rover && o instanceof BarrelObject) {
                BarrelObject barrel = ((BarrelObject)o);
                Circle barrelCircle = barrel.getCirle();
                Shape2D collidedShape = findCollidedShape(barrelCircle, roverPolygons);
                if (collidedShape != null && !tryMovingObject(collidedShape, barrel)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean tryMovingObject(Shape2D collidedShape, PiWarsCollidableObject startingObject) {
//        for (int i = 0; i < objectIds.size; i++) {
//            knownObjects[i] = piwarsGame.getCurrentGameState().get(objectIds.get(i));
//            // barrelObjects[i].getCirle(); // Make sure all circles are updated
//        }

        for (Shape2D startingObjectShape : startingObject.getCollisionPolygons()) {
            if (collidedShape instanceof Polygon) {
                Polygon collidedPolygon = (Polygon)collidedShape;
                if (startingObjectShape instanceof Circle) {
                    Circle startingObjectCircle = (Circle)startingObjectShape;

                    circleOverlapPolygonSeqment(startingObjectCircle, collidedPolygon, displacement);
                    point.set(startingObjectCircle.x, startingObjectCircle.y);
                    point.add(displacement);

                    startingObjectCircle.setPosition(point);
                    if (!canMove(displacement, startingObjectCircle)) {
                        return false;
                    }
                } else if (startingObjectShape instanceof Polygon) {
                    Polygon startingObjectPolygon = (Polygon)startingObjectShape;

                    overlapConvexPolygons(collidedPolygon, startingObjectPolygon, minimalDisplacementVector);

                    point.set(startingObjectPolygon.getX(), startingObjectPolygon.getY());
                    point.add(displacement);

                    startingObjectPolygon.setPosition(point.x, point.y);
                    if (!canMove(displacement, startingObjectPolygon)) {
                        return false;
                    }
                } else {
                    throw new RuntimeException("Cannot handle Polygon and " + startingObjectShape.getClass() + " movement.");
                }
            } else if (collidedShape instanceof Circle) {
                if (startingObjectShape instanceof Circle) {
                    Circle shapeCircle = ((Circle)collidedShape);
                    Circle startingObjectCircle = (Circle)startingObjectShape;

                    point.x = shapeCircle.x;
                    point.y = shapeCircle.y;
                    end.x = startingObjectCircle.x;
                    end.y = startingObjectCircle.y;
                    end.sub(start);
                    end.nor();
                    end.scl(shapeCircle.radius + startingObjectCircle.radius);
                    point.add(end);

                    startingObjectCircle.x = point.x;
                    startingObjectCircle.y = point.y;

                    if (!canMove(displacement, startingObjectCircle)) {
                        return false;
                    }
                } else if (startingObjectShape instanceof Polygon) {
                    Circle collidedCircle = (Circle)collidedShape;
                    Polygon startingObjectPolygon = (Polygon)startingObjectShape;

                    circleOverlapPolygonSeqment(collidedCircle, (Polygon)startingObjectShape, displacement);
                    point.set(startingObjectPolygon.getX(), startingObjectPolygon.getY());
                    point.add(displacement);

                    startingObjectPolygon.setPosition(point.x, point.y);
                    if (!canMove(displacement, startingObjectShape)) {
                        return false;
                    }
                } else {
                    throw new RuntimeException("Cannot handle Circle and " + startingObjectShape.getClass() + " movement.");
                }
            } else {
                throw new RuntimeException("Cannot handle " + collidedShape.getClass() + " and " + startingObjectShape.getClass() + " movement.");
            }

            if (checkBarrelMovements(startingObjectShape)) {
                for (PhysicsObject objectToMove : knownObjects) {
                    if (objectToMove.movingObject != null) {
                        getShapePosition(startingObjectShape, start);
                        Vector3 position = objectToMove.movingObject.getPosition();
                        if (start.x != position.x || start.y != position.y) {
                            objectToMove.movingObject.setPosition(start.x, start.y);
                        }
                    }
                }
                return true;
            }
        }
        return false;
    }

    private boolean checkBarrelMovements(Shape2D currentShape) {
        Circle currentCircle = (Circle)currentShape;
        while (true) {
            int i = 0;
            Circle nextCircle = null;
            while (i < knownObjects.length && nextCircle == null) {
                PhysicsObject object = knownObjects[i];
                Circle barrelCircle = ((BarrelObject)object.movingObject).getCirleInt();
                if (barrelCircle != currentCircle) {
                    float d = barrelCircle.radius + currentCircle.radius;
                    d = d * d;
                    if (distance2(barrelCircle.x, barrelCircle.y, currentCircle.x, currentCircle.y) < d) {

                        point.x = currentCircle.x;
                        point.y = currentCircle.y;
                        displacement.x = barrelCircle.x;
                        displacement.y = barrelCircle.y;
                        displacement.sub(point);
                        displacement.nor();
                        displacement.scl(currentCircle.radius + barrelCircle.radius + 0.1f);
                        point.add(displacement);

                        barrelCircle.x = point.x;
                        barrelCircle.y = point.y;

                        if (!canMove(displacement, barrelCircle)) {
                            return false;
                        }
                        nextCircle = barrelCircle;
                    }
                }
                i++;
            }
            if (nextCircle != null) {
                currentCircle = nextCircle;
            } else {
                return true;
            }
        }
    }

    private boolean canMove(Vector2 movement, Shape2D shape) {
        if (shape instanceof Circle) {
            return !CollisionUtils.overlaps(arenasPolygon, (Circle)shape);
        } else if (shape instanceof Rectangle) {
            return CollisionUtils.overlaps(arenasPolygon, (Rectangle)shape);
        } else if (shape instanceof Polygon) {
            return CollisionUtils.overlaps(arenasPolygon, (Polygon)shape);
        } else {
            throw new RuntimeException("Cannot intersect polygon with " + shape.getClass());
        }
    }

    public static void getShapePosition(Shape2D shape, Vector2 vector) {
        if (shape instanceof Circle) {
            vector.set(((Circle)shape).x, ((Circle)shape).y);
        } else if (shape instanceof Rectangle) {
            // TODO is that OK?
            ((Rectangle)shape).getCenter(vector);
        } else if (shape instanceof Polygon) {
            vector.set(((Polygon)shape).getX(), ((Polygon)shape).getY());
        } else {
            throw new RuntimeException("Cannot get position of " + shape.getClass());
        }
    }

    private static float distance2(float x1, float y1, float x2, float y2) {
        return (x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2);
    }

    private static class PhysicsObject {
        public float x;
        public float y;
        public boolean moved;
        public GameObject gameObject;
        public MovingGameObjectWithPositionAndOrientation movingObject;

        public PhysicsObject(GameObject gameObject) {
            update(gameObject);
        }

        public void update(GameObject gameObject) {
            this.gameObject = gameObject;
            if (gameObject instanceof MovingGameObjectWithPositionAndOrientation) {
                movingObject = (MovingGameObjectWithPositionAndOrientation)gameObject;
                this.x = movingObject.getPosition().x;
                this.y = movingObject.getPosition().y;
            } else {
                movingObject = null;
            }
        }
    }
}
