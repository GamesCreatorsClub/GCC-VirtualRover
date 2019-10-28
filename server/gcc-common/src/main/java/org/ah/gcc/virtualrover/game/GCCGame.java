package org.ah.gcc.virtualrover.game;


import com.badlogic.gdx.math.Vector3;

import org.ah.themvsus.engine.common.game.Game;
import org.ah.themvsus.engine.common.game.GameObjectFactory;
import org.ah.themvsus.engine.common.game.GameObjectWithPosition;
import org.ah.themvsus.engine.common.game.Player;

public class GCCGame extends Game {

    public GCCGame() {
        super();
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

        Vector3 objectPosition = object.getPosition();
        float x = objectPosition.x;
        float y = objectPosition.y;
        if (x + 80 > 1000 || x - 80 < -1000) {
            return true;
        }
        if (y + 80 > 1000 || y - 80 < -1000) {
            return true;
        }

        for (GameObjectWithPosition o : objects) {
            if (o != object) {
                Vector3 otherPos = o.getPosition();
                float ox = otherPos.x;
                float oy = otherPos.y;

                float distancesquared = (x - ox) * (x - ox) + (y - oy) * (y - oy);

                if (distancesquared < (80 * 80)) {
                    return true;
                }
            }
        }

        return super.checkForCollision(object, objects);
    }
}
