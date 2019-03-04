package com.cs361d.flashpoint.manager;

import com.cs361d.flashpoint.model.BoardElements.CarrierStatus;

public enum MapKind {
    ORIGINAL1("original1"),
    ORIGINAL2("orignal2"),
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
        return null;
    }
}
