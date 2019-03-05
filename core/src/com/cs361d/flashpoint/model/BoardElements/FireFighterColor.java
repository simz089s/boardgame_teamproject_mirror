package com.cs361d.flashpoint.model.BoardElements;

public enum FireFighterColor {
  BLUE("blue"),
  GREEN("green"),
  ORANGE("orange"),
  RED("red"),
  WHITE("white"),
  YELLOW("yellow");

  private String text;
  FireFighterColor(String text) {
    this.text = text;
  }

  public static FireFighterColor fromString(String text) {
      for (FireFighterColor b : FireFighterColor.values()) {
        if (b.text.equalsIgnoreCase(text)) {
          return b;
        }
      }
      return null;
    }
  }
