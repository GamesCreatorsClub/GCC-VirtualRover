package org.ah.gcc.virtualrover.game;


import com.badlogic.gdx.math.Polygon;

import org.ah.gcc.virtualrover.game.challenge.Challenge;
import org.ah.gcc.virtualrover.game.challenge.Challenges;
import org.ah.themvsus.engine.common.game.Game;
import org.ah.themvsus.engine.common.game.GameObjectFactory;
import org.ah.themvsus.engine.common.game.GameObjectWithPosition;
import org.ah.themvsus.engine.common.game.Player;

import java.util.List;

import static org.ah.gcc.virtualrover.engine.utils.PolygonUtils.polygonsOverlap;

public class GCCGame extends Game {

    private Challenge challenge;

    public GCCGame(String mapId) {
        super();
        this.challenge = Challenges.createChallenge(mapId);
    }

    @Override
    public Player spawnPlayer(int id, String alias) {
        Player player = super.spawnPlayer(id, alias);

        return player;
    }

    @Override
    protected GameObjectFactory createGameFactory() {
        return new GCCGameObjectFactory();
    }

    @Override
    public boolean checkForCollision(GameObjectWithPosition object, Iterable<GameObjectWithPosition> objects) {

        List<Polygon> roverPolygon = null;
        if (object instanceof GCCCollidableObject) {
            roverPolygon = ((GCCCollidableObject)object).getCollisionPolygons();
        }

        if (challenge != null) {
            if (roverPolygon != null) {
                if (polygonsOverlap(challenge.getCollisionPolygons(), roverPolygon)) {
                    return true;
                }
            }
        }

        for (GameObjectWithPosition o : objects) {
            if (o != object) {
                if (roverPolygon != null && object instanceof GCCPlayer) {
                    List<Polygon> otherRoverPolygon = ((GCCCollidableObject)o).getCollisionPolygons();
                    if (polygonsOverlap(roverPolygon, otherRoverPolygon)) {
                        return true;
                    }
                }
            }
        }

        return super.checkForCollision(object, objects);
    }
}
