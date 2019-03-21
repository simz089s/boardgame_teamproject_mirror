package com.cs361d.flashpoint.networking;

public enum ServerCommands
{
    //CHATWAIT("chatwait"),
    //CHATGAME("chatgame"),
    //ADDMSG("addmsg"),
    GAMESTATE("gamestate"),
    //SAVE("save"),
    SEND_NEWLY_CREATED_BOARD("SEND_NEWLY_CREATED_BOARD"),
    ASSIGN_FIREFIGHTER("ASSIGN_FIREFIGHTER"),
    //ASK_TO_GET_ASSIGN_FIREFIGHTER("ASK_TO_GET_ASSIGN_FIREFIGHTER"),
    DISCONNECTSERVER("DISCONNECT_SERVER"),
    //DISCONNECTCLIENT("DISCONNECT_CLIENT"),
    //JOIN("join"),
    SETBOARDSCREEN("SETBOARDSCREEN");
    //EXITGAME("exitgame");

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
