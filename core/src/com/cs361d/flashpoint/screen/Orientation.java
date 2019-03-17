package com.cs361d.flashpoint.screen;

public enum Orientation {
    HORIZONTAL("horizontal"),
    VERTICAL("vertical");

    private String text;

    public String toString() {
        return this.text;
    }

    public static Orientation fromString(String text) {
        for (Orientation f : Orientation.values()) {
            if (f.text.equalsIgnoreCase(text)) {
                return f;
            }
        }
        throw new IllegalArgumentException("The String " + text + " does not correspond to any Orientation ENUM");
    }

    Orientation(String text){
        this.text = text;
    }
}
