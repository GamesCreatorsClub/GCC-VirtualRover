package org.ah.gcc.virtualrover.game;


import com.badlogic.gdx.math.Polygon;

import org.ah.themvsus.engine.common.game.Game;
import org.ah.themvsus.engine.common.game.GameObjectFactory;
import org.ah.themvsus.engine.common.game.GameObjectWithPosition;
import org.ah.themvsus.engine.common.game.Player;

import java.util.List;

import static org.ah.gcc.virtualrover.engine.utils.PolygonUtils.polygonFromBox;
import static org.ah.gcc.virtualrover.engine.utils.PolygonUtils.polygonsOverlap;

import static java.util.Arrays.asList;

public class GCCGame extends Game {

    private String mapId;

    private List<Polygon> piNoonPolygons = asList(
            polygonFromBox(-1000, -1001,  1000, -1000),
            polygonFromBox(-1001, -1000, -1000,  1000),
            polygonFromBox(-1000,  1000,  1000,  1001),
            polygonFromBox( 1000, -1000,  1001,  1000));

    public GCCGame(String mapId) {
        super();
        this.mapId = mapId;
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

        if ("PiNoon".equals(mapId)) {
            if (roverPolygon != null) {
                if (polygonsOverlap(piNoonPolygons, roverPolygon)) {
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
