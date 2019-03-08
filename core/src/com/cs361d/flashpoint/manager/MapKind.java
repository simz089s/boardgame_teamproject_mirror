package com.cs361d.flashpoint.manager;

import com.cs361d.flashpoint.model.BoardElements.CarrierStatus;

public enum MapKind {
    MAP1("map1"),
    MAP2("map2"),
    RANDOM("random");

    MapKind(String text) {
        this.text = text;
    }
    private String text;

    public String getText() {
        return this.text;
    }

    public static MapKind fromString(String text) {
        for (MapKind b : MapKind.values()) {
            if (b.text.equalsIgnoreCase(text)) {
                return b;
            }
        }
        throw new IllegalArgumentException("The String " + text + " does not correspond to any Direction ENUM");
    }
}
