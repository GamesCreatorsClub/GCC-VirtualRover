package org.ah.piwars.fishtank.game;


import com.badlogic.gdx.utils.Array;

import org.ah.piwars.fishtank.game.fish.Fish;
import org.ah.themvsus.engine.common.game.Game;
import org.ah.themvsus.engine.common.game.GameObject;
import org.ah.themvsus.engine.common.game.GameObjectFactory;
import org.ah.themvsus.engine.common.game.GameObjectWithPosition;
import org.ah.themvsus.engine.common.input.PlayerInputs;

public class FishtankGame extends Game {

    public static int GAME_TICK_IN_us = 16000;

    public FishtankGame(String mapId) {
        super(GAME_TICK_IN_us);
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
}
