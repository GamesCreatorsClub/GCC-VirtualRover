package org.ah.gcc.virtualrover.game.challenge;

import com.badlogic.gdx.math.Polygon;

import java.util.List;

import static org.ah.gcc.virtualrover.engine.utils.PolygonUtils.polygonFromBox;

import static java.util.Arrays.asList;

public class PiNoonChallenge extends AbstractChallenge {

    private List<Polygon> piNoonPolygons = asList(
            polygonFromBox(-1000, -1001,  1000, -1000),
            polygonFromBox(-1001, -1000, -1000,  1000),
            polygonFromBox(-1000,  1000,  1000,  1001),
            polygonFromBox( 1000, -1000,  1001,  1000));

    @Override
    public List<Polygon> getCollisionPolygons() {
        return piNoonPolygons;
    }
}
