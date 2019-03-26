package com.cs361d.flashpoint.networking;

public enum DriverResponse {
    ACCEPT,
    THROW_ROW_DIE,
    THROW_COLUMN_DIE;

    public String toString() {
        return super.toString().replace("_"," ");
    }

    public static DriverResponse fromString(String text) {
        for (DriverResponse c : DriverResponse.values()) {
            if (c.toString().equalsIgnoreCase(text)) {
                return c;
            }
        }
        throw new IllegalArgumentException("The string " + text + " does not exist");
    }
}
