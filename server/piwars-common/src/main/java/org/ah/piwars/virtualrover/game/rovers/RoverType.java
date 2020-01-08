package org.ah.piwars.virtualrover.game.rovers;

import org.ah.piwars.virtualrover.game.PiWarsGameTypeObject;
import org.ah.themvsus.engine.common.game.GameObjectType;

public enum RoverType {
    GCCM16(0, "GCC Rover M16") {
        @Override public RoverControls createRoverControls() { return new FourSteeringWheelsRoverControls(); }
        @Override public GameObjectType getGameObjectType() { return PiWarsGameTypeObject.GCCRoverM16; }
    },

    GCCM18(0, "GCC Rover M18") {
        @Override public RoverControls createRoverControls() { return new FourSteeringWheelsRoverControls(); }
        @Override public GameObjectType getGameObjectType() { return PiWarsGameTypeObject.GCCRoverM18; }
    },

    CBIS(1, "CBiS-Education") {
        @Override public RoverControls createRoverControls() { return new TankRoverControls(); }
        @Override public GameObjectType getGameObjectType() { return PiWarsGameTypeObject.CBISRover; }
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
        return RoverType.GCCM16;
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
