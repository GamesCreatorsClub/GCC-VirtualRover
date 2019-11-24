package org.ah.gcc.virtualrover.game.rovers;

import org.ah.gcc.virtualrover.game.GCCGameTypeObject;
import org.ah.themvsus.engine.common.game.GameObjectType;

public enum RoverType {
    GCC(0, "GCC Rover") {
        @Override public RoverControls createRoverControls() { return new FourSteeringWheelsRoverControls(); }
        @Override public GameObjectType getGameObjectType() { return GCCGameTypeObject.GCCRover; }
    },

    CBIS(1, "CBiS-Education") {
        @Override public RoverControls createRoverControls() { return new TankRoverControls(); }
        @Override public GameObjectType getGameObjectType() { return GCCGameTypeObject.CBISRover; }
    };

    private int id;
    private String name;

    RoverType(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public static RoverType getById(int id) {
        for (RoverType e : values()) {
            if (e.getId() == id) {
                return e;
            }
        }
        return RoverType.GCC;
    }

    public RoverType getNext() {
        return getById((getId() + 1) % values().length);
    }

    public RoverType getPrevious() {
        return getById((getId() - 1) % values().length);
    }

    public String getName() {
        return name;
    }

    public abstract RoverControls createRoverControls();

    public abstract GameObjectType getGameObjectType();
}
