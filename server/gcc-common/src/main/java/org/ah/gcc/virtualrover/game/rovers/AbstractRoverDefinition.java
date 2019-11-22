package org.ah.gcc.virtualrover.game.rovers;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractRoverDefinition implements RoverDefinition {

    public static final float BALLOONS_RADIUS = 35;
    private static final float SHARP_POINT_LENGTH = 130;

    protected RoverControls roverControls;
    protected List<Polygon> polygons;
    protected Vector2 attachmentPosition;
    protected Vector2[] balloons = new Vector2[3];

    protected Vector2 temp = new Vector2();

    public AbstractRoverDefinition() {
        balloons[0] = new Vector2(0f, -45f);
        balloons[1] = new Vector2(75f, 0f);
        balloons[2] = new Vector2(0f, 45f);
    }

    @Override
    public RoverControls getRoverControls() {
        return roverControls;
    }

    @Override
    public List<Polygon> getPolygonsCopy() {
        List<Polygon> polygons = new ArrayList<Polygon>();
        for (Polygon originalPolygon : this.polygons) {
            Polygon newPolygon = new Polygon(copyVertices(originalPolygon.getVertices()));
            polygons.add(newPolygon);
        }
        return polygons;
    }

    private static float[] copyVertices(float[] vertices) {
        float[] newVertices = new float[vertices.length];
        System.arraycopy(vertices, 0, newVertices, 0, vertices.length);
        return newVertices;
    }

    @Override
    public List<Polygon> updatePolygons(List<Polygon> polygons, float x, float y, float angle) {
        for (Polygon p : polygons) {
            p.setPosition(x, y);
            p.setRotation(angle);
        }
        return polygons;
    }

    @Override
    public Vector2 getSharpPoint(Vector2 point, float x, float y, float angle) {
        point.set(attachmentPosition);
        point.add(SHARP_POINT_LENGTH, 0);
        point.rotate(angle);
        point.add(x, y);
        return point;
    }

    @Override
    public Circle getBalloon(Circle circle, int balloonNo, float x, float y, float angle) {
        temp.set(balloons[balloonNo]);
        temp.add(attachmentPosition);
        temp.rotate(angle);
        temp.add(x, y);
        circle.setPosition(temp);
        return circle;
    }
}
