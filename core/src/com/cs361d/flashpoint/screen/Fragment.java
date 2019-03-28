package com.cs361d.flashpoint.screen;

public enum Fragment {
    CHEATSHEET("cheat_sheep"),
    CHOOSESPECIALTY("choose_specialty"),
    STATS("stats"),
    EMPTY("empty"),
    CALL_FOR_ACTION("CALL_FOR_ACTION"),
    CHAT("chat");

    private String text;

    public String toString() {
        return this.text;
    }

    public static Fragment fromString(String text) {
        for (Fragment f : Fragment.values()) {
            if (f.text.equalsIgnoreCase(text)) {
                return f;
            }
        }
        throw new IllegalArgumentException("The String " + text + " does not correspond to any Fragments ENUM");
    }

    Fragment(String text){
        this.text = text;
    }
}
