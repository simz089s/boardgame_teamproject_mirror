package com.cs361d.flashpoint.manager;

public enum Difficulty {
  FAMILY("family"),
  RECRUIT("recruit"),
  VETERAN("veteran"),
  HEROIC("heroic");

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
    throw new IllegalArgumentException("The String " + text + " does not correspond to any Direction ENUM");
  }
}
