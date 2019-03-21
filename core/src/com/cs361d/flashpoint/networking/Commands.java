package com.cs361d.flashpoint.networking;

public enum Commands {
    CHATWAIT("chatwait"),
    CHATGAME("chatgame"),
    ADDMSG("addmsg"),
    GAMESTATE("gamestate"),
    SAVE("save"),
    SEND_NEWLY_CREATED_BOARD("SEND_NEWLY_CREATED_BOARD"),
    ASSIGN_FIREFIGHTER("ASSIGN_FIREFIGHTER"),
    ASK_TO_GET_ASSIGN_FIREFIGHTER("ASK_TO_GET_ASSIGN_FIREFIGHTER"),
    DISCONNECTSERVER("DISCONNECT_SERVER"),
    DISCONNECTCLIENT("DISCONNECT_CLIENT"),
    JOIN("join"),
    SETBOARDSCREEN("SETBOARDSCREEN"),
    EXITGAME("exitgame");

    private String text;

    Commands(String text) {
        this.text = text;
    }

    public static Commands fromString(String text) {
        for (Commands c : Commands.values()) {
            if (c.text.equalsIgnoreCase(text)) {
                return c;
            }
        }
        throw new IllegalArgumentException("The string " + text + " does not exist");
    }
}
