package org.ah.piwars.fishtank.game;


import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Plane;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.Array;

import org.ah.piwars.fishtank.game.fish.Fish;
import org.ah.themvsus.engine.common.game.Game;
import org.ah.themvsus.engine.common.game.GameObject;
import org.ah.themvsus.engine.common.game.GameObjectFactory;
import org.ah.themvsus.engine.common.game.GameObjectWithPosition;
import org.ah.themvsus.engine.common.input.PlayerInputs;

public class FishtankGame extends Game {

    public static Vector3 LEFT  = new Vector3(-1f,  0f,  0f);
    public static Vector3 RIGHT = new Vector3( 1f,  0f,  0f);
//    public static Vector3 UP    = new Vector3( 0f,  1f,  0f);
//    public static Vector3 DOWN  = new Vector3( 0f, -1f,  0f);
//    public static Vector3 BACK  = new Vector3( 0f,  0f, -1f);
//    public static Vector3 FRONT = new Vector3( 0f,  0f,  1f);

    public static Vector3 UP    = new Vector3( 0f,  0f,  1f);
    public static Vector3 DOWN  = new Vector3( 0f,  0f, -1f);
    public static Vector3 BACK  = new Vector3( 0f, -1f,  0f);
    public static Vector3 FRONT = new Vector3( 0f,  1f,  0f);


    public static int GAME_TICK_IN_us = 16000;

    public static float WIDTH = 200;
    public static float HALF_WIDTH = 100;
    public static float HEIGHT = 200;
    public static float HALF_HEIGHT = 100;
    public static float DEPTH = 120;
    public static float HALF_DEPTH = 60;

    public static int TOP_PLANE_INDEX = 0;
    public static int LEFT_PLANE_INDEX = 1;
    public static int BACK_PLANE_INDEX = 2;
    public static int RIGHT_PLANE_INDEX = 3;
    public static int FRONT_PLANE_INDEX = 4;
    public static int BOTTOM_PLANE_INDEX = 5;

    private Plane[] planes = new Plane[6];

    public FishtankGame(String mapId) {
        super(GAME_TICK_IN_us);

        planes[TOP_PLANE_INDEX] = new Plane(UP, HALF_DEPTH);
        planes[LEFT_PLANE_INDEX] = new Plane(LEFT, HALF_WIDTH);
        planes[BACK_PLANE_INDEX] = new Plane(BACK, HALF_HEIGHT);
        planes[RIGHT_PLANE_INDEX] = new Plane(RIGHT, HALF_WIDTH);
        planes[FRONT_PLANE_INDEX] = new Plane(FRONT, HALF_HEIGHT);
        planes[BOTTOM_PLANE_INDEX] = new Plane(DOWN, HALF_DEPTH);
    }

    public Fish spawnFish(int id, FishtankGameTypeObject fishType) {
        Fish fish = getGameObjectFactory().newGameObjectWithId(fishType, id);

        addNewGameObject(fish);

        players.add(id);
        return fish;
    }

    @Override
    protected GameObjectFactory createGameFactory() {
        return new FishtankGameObjectFactory();
    }

    @Override
    protected void processGameObjects(Array<GameObjectWithPosition> processedGameObjects) {
        for (GameObject gameObject : getCurrentGameState().gameObjects().values()) {
            gameObject.process(this, processedGameObjects);
        }
    }

    @Override
    public boolean checkForCollision(GameObjectWithPosition object, Iterable<GameObjectWithPosition> objects) {
        return super.checkForCollision(object, objects);
    }

    @Override
    protected void postProcessGameState() {
        super.postProcessGameState();
    }


    @Override
    public void processPlayerInputs(int playerId, PlayerInputs playerInputs) {
        super.processPlayerInputs(playerId, playerInputs);
    }

    @Override
    protected void fireGameObjectAdded(GameObject newGameObject) {
        super.fireGameObjectAdded(newGameObject);
    }

    @Override
    protected void fireObjectRemoved(GameObject objectToRemove) {
        super.fireObjectRemoved(objectToRemove);
    }

    public float getWidth() { return WIDTH; }

    public float getHeight() { return HEIGHT; }

    public float getDepth() { return DEPTH; }

    public float distanceToEdge(Ray ray, Vector3 temp) {
        float distance = -1f;
        for (Plane plane : planes) {
            if (Intersector.intersectRayPlane(ray, plane, temp)) {
                if (distance < 0) {
                    distance = ray.origin.dst2(temp);;
                } else {
                    float d = ray.origin.dst2(temp);
                    if (d < distance) {
                        distance = d;
                    }
                }
            }
        }
        return distance;
    }
 }
