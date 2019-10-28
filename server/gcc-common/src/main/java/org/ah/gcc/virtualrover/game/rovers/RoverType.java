package org.ah.gcc.virtualrover.game.rovers;

public enum RoverType {
    GCC(0, "GCC Rover", new GCCRoverDefinition()),
    CBIS(1, "CBiS-Education", new CBISRoverDefinition())
    ;

    private int id;
    private String name;
    private RoverDefinition roverDefinition;

    RoverType(int id, String nom, RoverDefinition roverDefinition) {
        this.id = id;
        this.name = nom;
        this.roverDefinition = roverDefinition;
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

    public RoverDefinition getRoverDefinition() {
        return roverDefinition;
    }
}
