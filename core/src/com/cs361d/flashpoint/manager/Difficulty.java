package com.cs361d.flashpoint.manager;

public enum Difficulty {
  FAMILLY("familly"),
  RECRUIT("recruit"),
  VETERAN("veteran"),
  HEROIC("heroid");

  private String text;

  Difficulty(String text) {
    this.text = text;
  }

  public String getText() {
    return this.text;
  }

  public static Difficulty fromString(String text) {
    for (Difficulty b : Difficulty.values()) {
      if (b.text.equalsIgnoreCase(text)) {
        return b;
      }
    }
    return null;
  }
}
