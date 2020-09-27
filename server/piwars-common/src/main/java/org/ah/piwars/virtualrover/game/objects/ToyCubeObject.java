package org.ah.piwars.virtualrover.game.objects;

import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Shape2D;

import org.ah.piwars.virtualrover.game.PiWarsCollidableObject;
import org.ah.piwars.virtualrover.game.PiWarsGameTypeObject;
import org.ah.themvsus.engine.common.game.GameObjectFactory;
import org.ah.themvsus.engine.common.game.GameObjectType;
import org.ah.themvsus.engine.common.game.MovingGameObjectWithPositionAndOrientation;
import org.ah.themvsus.engine.common.transfer.Serializer;

import java.util.ArrayList;
import java.util.List;

public class ToyCubeObject extends MovingGameObjectWithPositionAndOrientation implements PiWarsCollidableObject {


    public static enum ToyCubeColour {
        GREEN, RED, BLUE
    }

    public static int KUBE_SIDE_LENGTH = 50;

    public static ToyCubeColour[] KUBE_COLOUR_VALUES = ToyCubeColour.values();

    private ToyCubeColour kubeColour = ToyCubeColour.GREEN;

    private List<Shape2D> collisionPolygons = new ArrayList<Shape2D>();

    private Polygon kubePolygon;

    public ToyCubeObject(GameObjectFactory factory, int id) {
        super(factory, id);
        kubePolygon = new Polygon(makeKubePolygonVertices());
        collisionPolygons.add(kubePolygon);
    }

    public ToyCubeColour getColour() {
        return kubeColour;
    }

    public void setColour(ToyCubeColour colour) {
        this.changed = this.changed || this.kubeColour != colour;
        this.kubeColour = colour;
    }

    @Override
    public GameObjectType getType() { return PiWarsGameTypeObject.ToyCubeObject; }

    public Polygon getPolygon() {
        kubePolygon.setPosition(position.x, position.y);
        return kubePolygon;
    }

    @Override
    public void serialize(boolean full, Serializer serializer) {
        super.serialize(full, serializer);
        if (full) {
            serializer.serializeUnsignedByte(kubeColour.ordinal());
        }
    }

    @Override
    public void deserialize(boolean full, Serializer serializer) {
        super.deserialize(full, serializer);
        if (full) {
            int colour = serializer.deserializeUnsignedByte();
            if (colour < 0 || colour >= KUBE_COLOUR_VALUES.length) {
                colour = 0;
            }
            this.kubeColour = KUBE_COLOUR_VALUES[colour];
        }
    }

    @Override
    public int size(boolean full) {
        return super.size(full) + (full ? 1 : 0);
    }

    @Override
    public List<Shape2D> getCollisionPolygons() {
        getPolygon(); // update circle
        return collisionPolygons;
    }

    private float[] makeKubePolygonVertices() {
        float half = KUBE_SIDE_LENGTH / 2.0f;
        return new float[] {
                    half, half, -half, half,
                    -half, -half, half, -half,
                };
    }

}
