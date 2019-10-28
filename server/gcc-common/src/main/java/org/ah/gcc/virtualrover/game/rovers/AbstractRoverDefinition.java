package org.ah.gcc.virtualrover.game.rovers;

import com.badlogic.gdx.math.Polygon;

import java.util.List;

public abstract class AbstractRoverDefinition implements RoverDefinition {

    protected RoverControls roverControls;
    protected List<Polygon> polygons;

    public AbstractRoverDefinition() {
    }

    @Override
    public RoverControls getRoverControls() {
        return roverControls;
    }

    @Override
    public List<Polygon> getPolygons(float x, float y, float angle) {
        return updatePolygons(polygons, x, y, angle);
    }

    protected List<Polygon> updatePolygons(List<Polygon> polygons, float x, float y, float angle) {
        for (Polygon p : polygons) {
            p.setPosition(x, y);
            p.setRotation(angle);
        }
        return polygons;
    }
}
