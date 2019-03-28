package com.cs361d.flashpoint.manager;

import com.cs361d.flashpoint.model.BoardElements.FireFighter;
import com.cs361d.flashpoint.model.BoardElements.FireFighterColor;
import com.cs361d.flashpoint.networking.Server;

public class User {
  private FireFighterColor myColor;
  String name = "defaultName";
  private static User instance = new User();

  public static User getInstance() {
    return instance;
  }

  public void assignFireFighter(FireFighterColor c) {
    myColor = c;
  }

  public boolean isMyTurn() {
    if (myColor == null) {
      return false;
    } else {
      return myColor == FireFighterTurnManager.getInstance().getCurrentFireFighter().getColor();
    }
  }

  public FireFighterColor getColor() {
    return myColor;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    if (name != null && !name.equals("")) this.name = name;
  }
}
