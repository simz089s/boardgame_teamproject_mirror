package com.cs361d.flashpoint.model.BoardElements;

public enum CarrierStatus {
    EMPTY("empty"),
    AMBULANCE("ambulance"),
    FIRETRUCK("firetruck");

    CarrierStatus(String text) {
        this.text = text;
    }
    private String text;

    public String getText() {
        return this.text;
    }

    public static CarrierStatus fromString(String text) {
        for (CarrierStatus b : CarrierStatus.values()) {
            if (b.text.equalsIgnoreCase(text)) {
                return b;
            }
        }
        return null;
    }
}
