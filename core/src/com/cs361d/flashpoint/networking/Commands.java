package com.cs361d.flashpoint.networking;

public enum Commands {
    CHATWAIT("chatwait"),
    CHATGAME("chatgame"),
    ADDMSG("addmsg"),
    GAMESTATE("gamestate");
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
        throw new IllegalArgumentException("The string " + text + "does not exist");
    }
}
