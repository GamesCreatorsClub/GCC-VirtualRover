package org.ah.gcc.virtualrover.game.rovers;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;

import java.util.List;

public interface RoverDefinition {

    RoverControls getRoverControls();

    List<Polygon> getPolygons(float x, float y, float angle);

    Vector2 getSharpPoint(float x, float y, float angle);

    Circle getBalloon(int balloonNo, float x, float y, float angle);
}
