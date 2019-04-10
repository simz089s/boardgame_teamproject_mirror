package com.cs361d.flashpoint.model.FireFighterSpecialities;

public enum FireFighterAdvanceSpecialties {
    PARAMEDIC("paramedic"),
    FIRE_CAPTAIN("fire captain"),
    IMAGING_TECHNICIAN("imaging technician"),
    CAFS_FIREFIGHTER("cafs firefighter"),
    HAZMAT_TECHNICIAN("hazmat technician"),
    GENERALIST("generalist"),
    RESCUE_SPECIALIST("rescue specialist"),
    RESCUE_DOG("rescue dog"),
    VETERAN("veteran"),
    DRIVER("driver"),
    STRUCTURAL_ENGINEER("structural engineer"),
    PYROMANCER("PYROMANCER"),
    NO_SPECIALTY("NO SPECIALTY");

    private String text;
    FireFighterAdvanceSpecialties(String text) {
        this.text = text;
    }
    public String toText() {
        return this.text;
    }

    @Override public String toString() {
        return super.toString().replace("_"," ");
    }

    public static FireFighterAdvanceSpecialties fromString(String text) {
        for (FireFighterAdvanceSpecialties b : FireFighterAdvanceSpecialties.values()) {
            if (b.text.equalsIgnoreCase(text) || b.toString().equalsIgnoreCase(text)) {
                return b;
            }
        }
        throw new IllegalArgumentException("The string " + text + "corresponds to no enum" );
    }
}
