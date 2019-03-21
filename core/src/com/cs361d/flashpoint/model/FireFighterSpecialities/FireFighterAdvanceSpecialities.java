package com.cs361d.flashpoint.model.FireFighterSpecialities;

public enum FireFighterAdvanceSpecialities {
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
