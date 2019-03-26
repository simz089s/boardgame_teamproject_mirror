package com.cs361d.flashpoint.networking;

public enum ServerCommands
{
    //CHATWAIT("chatwait"),
//    CHATGAME("CHAT_GAME"),
    GAMESTATE("gamestate"),
    SAVE("SAVE"),
    SEND_NEWLY_CREATED_BOARD("SEND_NEWLY_CREATED_BOARD"),
//    ASSIGN_FIREFIGHTER("ASSIGN_FIREFIGHTER"),
    ASK_TO_GET_ASSIGN_FIREFIGHTER("ASK_TO_GET_ASSIGN_FIREFIGHTER"),
    JOIN("join"),
    LOAD_GAME("LOAD_GAME"),
//    SETBOARDSCREEN("SETBOARDSCREEN"),
    EXITGAME("exitgame"),
    ADD_CHAT_MESSAGE("ADD_CHAT_MESSAGE"),
    GET_CHAT_MESSAGES("GET_CHAT_MESSAGES");

    private String text;

    ServerCommands(String text) {
        this.text = text;
    }

    public static ServerCommands fromString(String text) {
        for (ServerCommands c : ServerCommands.values()) {
            if (c.text.equalsIgnoreCase(text)) {
                return c;
            }
        }
        throw new IllegalArgumentException("The string " + text + " does not exist");
    }
}
