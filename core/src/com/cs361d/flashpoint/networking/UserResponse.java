package com.cs361d.flashpoint.networking;

import java.util.ArrayList;
import java.util.List;

public enum UserResponse {
    ACCEPT,
    REJECT,
    THROW_ROW_DIE,
    THROW_COLUMN_DIE;

    public String toString() {
        return super.toString().replace("_"," ");
    }

    public static UserResponse fromString(String text) {
        for (UserResponse c : UserResponse.values()) {
            if (c.toString().equalsIgnoreCase(text)) {
                return c;
            }
        }
        throw new IllegalArgumentException("The string " + text + " does not exist");
    }

    public static List<UserResponse> driverResponse() {
        List<UserResponse> responses = new ArrayList<UserResponse>();
        responses.add(ACCEPT);
        responses.add(THROW_ROW_DIE);
        responses.add(THROW_COLUMN_DIE);
        return responses;
    }
}
