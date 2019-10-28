package org.ah.gcc.virtualrover.game.rovers;

import com.badlogic.gdx.math.Polygon;

import java.util.List;

public interface RoverDefinition {

    RoverControls getRoverControls();

    List<Polygon> getPolygons(float x, float y, float angle);
}
