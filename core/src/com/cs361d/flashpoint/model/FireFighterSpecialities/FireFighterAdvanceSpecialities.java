package com.cs361d.flashpoint.model.FireFighterSpecialities;

public enum FireFighterAdvanceSpecialities {
    PARAMEDIC("paramedic"),
    FIRE_CAPTAIN("fire_captain"),
    IMAGING_TECHNICIAN("imaging_technician"),
    CAFS_FIREFIGHTER("cafs_firefighter"),
    HAZMAT_TECHNICIAN("hazmat_technician"),
    GENERALIST("generalist"),
    RESCUE_SPECIALIST("rescue_specialist"),
    RESCUE_DOG("rescue_dog"),
    VETERAN("veteran"),
    DRIVER("driver"),
    NO_SPECIALITY("NO_SPECIALITY");

    private String text;
    FireFighterAdvanceSpecialities(String text) {
        this.text = text;
    }
    public String toText() {
        return this.text;
    }

    public static FireFighterAdvanceSpecialities fromString(String text) {
        for (FireFighterAdvanceSpecialities b : FireFighterAdvanceSpecialities.values()) {
            if (b.text.equalsIgnoreCase(text)) {
                return b;
            }
        }
        throw new IllegalArgumentException("The string " + text + "corresponds to no enum" );
    }
}
