package com.cs361d.flashpoint.networking;

public enum ServerCommands
{
    SAVE("SAVE"),
    SEND_NEWLY_CREATED_BOARD("SEND_NEWLY_CREATED_BOARD"),
    JOIN("join"),
    LOAD_GAME("LOAD_GAME"),
    EXIT_GAME("exit_game"),
    ADD_CHAT_MESSAGE("ADD_CHAT_MESSAGE"),
    GET_CHAT_MESSAGES("GET_CHAT_MESSAGES"),
    NULL_COMMAND("NULL_COMMAND"),
    CHOOSE_INITIAL_POSITION("CHOOSE_INITIAL_POSITION"),
    SET_INITIAL_SPECIALTY("SET_INITIAL_SPECIALTY"),
    CREATE_GAME("CREATE_GAME"),
    SET_AMBULANCE("SET_AMBULANCE"),
    ACCEPT_MOVE_BY_CAPTAIN("ACCEPT_MOVE_BY_CAPTAIN"),
    SET_FIRETRUCK("SET_FIRETRUCK"),
    REPLY_KNOCKED_DOWN_CHOICE("REPLY_KNOCKED_DOWN_CHOICE"),
    REPLY_MOVE_WITH_VEHICLE("REPLY_MOVE_WITH_VEHICLE"),
    ASK_DRIVER_MSG("ASK_DRIVER_MSG"),
    GET_SAVED_GAMES("GET_SAVED_GAMES");

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
        return NULL_COMMAND;
    }
}
