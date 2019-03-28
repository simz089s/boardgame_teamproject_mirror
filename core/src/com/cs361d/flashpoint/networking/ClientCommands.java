package com.cs361d.flashpoint.networking;

public enum ClientCommands //Commands sent to client
{
    REFRESH_BOARD_SCREEN("REFRESH_BOARD_SCREEN"),
    ASSIGN_FIREFIGHTER("ASSIGN_FIREFIGHTER"),
    EXIT_GAME("EXIT_GAME"),
    SET_BOARD_SCREEN("SET_BOARD_SCREEN"),
    SEND_CHAT_MESSAGES("SEND_CHAT_MESSAGES"),
    ADD_CHAT_MESSAGE ("ADD_CHAT_MESSAGE"),
    SHOW_MESSAGE_ON_SCREEN("SHOW_MESSAGE_ON_SCREEN"),
    SET_GAME_STATE("SET_GAME_STATE"),
    ASK_TO_ACCEPT_MOVE("ASK_TO_ACCEPT_MOVE"),
    ASK_WISH_ABOUT_KNOWCK_DOWN("ASK_WISH_ABOUT_KNOWCK_DOWN"),
    ASK_DRIVE_WITH_ENGINE("ASK_DRIVE_WITH_ENGINE"),
    GAME_HAS_ENDED("GAME_HAS_ENDED"),
    ASK_DRIVER_MSG("ASK_DRIVER_MSG");

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
