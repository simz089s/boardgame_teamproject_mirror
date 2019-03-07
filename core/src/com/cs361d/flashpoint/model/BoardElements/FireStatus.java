package com.cs361d.flashpoint.model.BoardElements;

public enum FireStatus {
    FIRE("fire"),
    SMOKE("smoke"),
    EMPTY("empty");

    private String text;

    FireStatus(String text){
        this.text = text;
    }

    public String toString() {
        return this.text;
    }

    public static FireStatus fromString(String text) {
        for (FireStatus f : FireStatus.values()) {
            if (f.text.equalsIgnoreCase(text)) {
                return f;
            }
        }
        throw new IllegalArgumentException("The String " + text + " does not correspond to any Direction ENUM");
    }
}
