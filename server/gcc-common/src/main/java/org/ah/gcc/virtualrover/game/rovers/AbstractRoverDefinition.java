package org.ah.gcc.virtualrover.game.rovers;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;

import java.util.List;

public abstract class AbstractRoverDefinition implements RoverDefinition {

    private static final float BALLOONS_RADIUS = 40;
    private static final float SHARP_POINT_LENGTH = 130;

    protected RoverControls roverControls;
    protected List<Polygon> polygons;
    protected Vector2 attachmentPosition;
    protected Vector2[] balloons = new Vector2[3];

    protected Vector2 temp = new Vector2();
    protected Circle[] ballonsTempCircle = new Circle[3];

    public AbstractRoverDefinition() {
        balloons[0] = new Vector2(-25f, -25f);
        balloons[1] = new Vector2(25f, 0f);
        balloons[2] = new Vector2(-25f, 25f);
        for (int i = 0; i < ballonsTempCircle.length; i++) {
            ballonsTempCircle[i] = new Circle(0,  0, BALLOONS_RADIUS);
        }
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

    @Override
    public Vector2 getSharpPoint(float x, float y, float angle) {
        temp.set(attachmentPosition);
        temp.add(SHARP_POINT_LENGTH, 0);
        temp.rotate(angle);
        temp.add(x, y);
        return temp;
    }

    @Override
    public Circle getBalloon(int balloonNo, float x, float y, float angle) {
        temp.set(balloons[balloonNo]);
        temp.add(attachmentPosition);
        temp.rotate(angle);
        temp.add(x, y);
        ballonsTempCircle[balloonNo].setPosition(temp);
        return ballonsTempCircle[balloonNo];
    }
}
