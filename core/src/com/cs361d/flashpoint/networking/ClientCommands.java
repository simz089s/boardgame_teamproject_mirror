package com.cs361d.flashpoint.networking;

public enum ClientCommands //Commands sent to client
{
    GAMESTATE("gamestate"),
//    SAVE("save"),
    //SEND_NEWLY_CREATED_BOARD("SEND_NEWLY_CREATED_BOARD"),
    ASSIGN_FIREFIGHTER("ASSIGN_FIREFIGHTER"),
//    ASK_TO_GET_ASSIGN_FIREFIGHTER("ASK_TO_GET_ASSIGN_FIREFIGHTER"),
    //DISCONNECTSERVER("DISCONNECT_SERVER"),
//    DISCONNECTCLIENT("DISCONNECT_CLIENT"),
//    JOIN("join"),
    SETBOARDSCREEN("SETBOARDSCREEN"),
    EXITGAME("EXIT_GAME"),
    SEND_CHAT_MESSAGES("SEND_MSG");

    private String text;

    ClientCommands(String text) {
        this.text = text;
    }

    public static ClientCommands fromString(String text) {
        for (ClientCommands c : ClientCommands.values()) {
            if (c.text.equalsIgnoreCase(text)) {
                return c;
            }
        }
        throw new IllegalArgumentException("The string " + text + " does not exist");
    }
}
