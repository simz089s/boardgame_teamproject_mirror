package com.cs361d.flashpoint.screen;

public enum GameSetupActions
{
    PLACE_FIRETRUCK("PLACE_FIRETRUCK"),
    CHOOSE_INIT_POS("CHOOSE_INIT_POS"),
    CHOOSE_INIT_SPECIALTY("CHOOSE_INIT_SPECIALTY"),
    NO_SETUP_ACTION("NO_SETUP_ACTION");

    private String text;

    GameSetupActions(String text) {
        this.text = text;
    }

    public static GameSetupActions fromString(String text) {
        for (GameSetupActions c : GameSetupActions.values()) {
            if (c.text.equalsIgnoreCase(text)) {
                return c;
            }
        }
        throw new IllegalArgumentException("The string " + text + " does not exist");
    }
}
