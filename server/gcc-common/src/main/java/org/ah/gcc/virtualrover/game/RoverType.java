package org.ah.gcc.virtualrover.game;

public enum RoverType {
    GCC(0, "GCC Rover"), CBIS(1, "CBiS-Education");

    private int id;
    private String name;

    RoverType(int id, String nom) {
        this.id = id;
        this.name = nom;
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
}
