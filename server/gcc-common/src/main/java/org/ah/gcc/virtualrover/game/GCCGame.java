package org.ah.gcc.virtualrover.game;


import com.badlogic.gdx.math.Polygon;

import org.ah.gcc.virtualrover.game.challenge.Challenge;
import org.ah.gcc.virtualrover.game.challenge.Challenges;
import org.ah.themvsus.engine.common.game.AbstractPlayer;
import org.ah.themvsus.engine.common.game.Game;
import org.ah.themvsus.engine.common.game.GameObjectFactory;
import org.ah.themvsus.engine.common.game.GameObjectWithPosition;
import org.ah.themvsus.engine.common.game.GameState;
import org.ah.themvsus.engine.common.input.PlayerInputs;

import java.util.List;

import static org.ah.gcc.virtualrover.engine.utils.PolygonUtils.polygonsOverlap;

public class GCCGame extends Game {

    private Challenge challenge;
    public static final long ENGINE_LOOP_TIME_us = 8500;

    public GCCGame(String mapId) {
        super();
        if (mapId != null) {
            this.challenge = Challenges.createChallenge(this, mapId);
        }
    }

    @Override
    public AbstractPlayer spawnPlayer(int id, String alias) {
        AbstractPlayer player = super.spawnPlayer(id, alias);

        return player;
    }

    @Override
    protected GameObjectFactory createGameFactory() {
        return new GCCGameObjectFactory();
    }

    @Override
    public boolean checkForCollision(GameObjectWithPosition object, Iterable<GameObjectWithPosition> objects) {
        if (challenge != null && challenge.checkForCollision(object, objects)) {
            return true;
        }

        if (object instanceof GCCCollidableObject) {
            List<Polygon> roverPolygon = ((GCCCollidableObject)object).getCollisionPolygons();
            for (GameObjectWithPosition o : objects) {
                if (o != object && o instanceof GCCPlayer) {
                    List<Polygon> otherRoverPolygon = ((GCCCollidableObject)o).getCollisionPolygons();
                    if (polygonsOverlap(roverPolygon, otherRoverPolygon)) {
                        return true;
                    }
                }
            }
        }

        return super.checkForCollision(object, objects);
    }

    @Override
    public GameState process() { // ServerEngine.mainloop
        if (challenge != null) {
            challenge.process();
        }
        return super.process();
    }

    @Override
    public void processPlayerInputs(int playerId, PlayerInputs playerInputs) {
        if (challenge == null || challenge.processPlayerInputs(playerId, playerInputs)) {
            super.processPlayerInputs(playerId, playerInputs);
        }
    }

    public Challenge getChallenge() {
        return this.challenge;
    }
}
