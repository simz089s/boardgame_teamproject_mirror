package com.cs361d.flashpoint.model.BoardElements;

import java.util.HashMap;

public class FireFighter {

  private static final HashMap<FireFighterColor, FireFighter> FIREFIGHTERS = new HashMap<FireFighterColor,FireFighter>();
  private final FireFighterColor color;
  private static final int MAX_ACTION_POINTS = 8;
  private int actionPoints;
  private Tile currentTile;

  private FireFighter(FireFighterColor color, int actionPoints) {
    if (actionPoints > MAX_ACTION_POINTS) {
      throw new IllegalArgumentException("Action points cannot exceed 8 was: " + actionPoints);
    }
    this.color = color;
    this.actionPoints = actionPoints;
  }
  public static FireFighter createFireFighter(FireFighterColor color) {
    return createFireFighter(color, 0, 4);
  }

  public static FireFighter createFireFighter(FireFighterColor color, int numVictimsSaved, int actionPoints) {
    FireFighter f;
    if (FIREFIGHTERS.containsKey(color)) {
      f = FIREFIGHTERS.get(color);
      f.actionPoints = actionPoints;
    }
    else {
      f = new FireFighter(color, actionPoints);
    }
    return f;
  }

  public void setTile(Tile t) {
    this.currentTile = t;
  }

  public Tile getTile() {
    return currentTile;
  }

  public FireFighterColor getColor() {
    return color;
  }

  public int getActionPointsLeft() {
    return actionPoints;
  }

  public boolean removeActionPoints(int a) {
    if (a < 1 || this.actionPoints < a) {
      return false;
    } else {
      this.actionPoints -= a;
      return true;
    }
  }

  public void resetActionPoints() {
    this.actionPoints += 4;
    if (this.actionPoints > MAX_ACTION_POINTS) {
      this.actionPoints = MAX_ACTION_POINTS;
    }
  }

  public boolean moveAP(Direction d) {
    Tile newTile = currentTile.getAdjacentTile(d);
    if (newTile.hasFire()) {
      if (actionPoints >= 2)
      {
        actionPoints -= 2;
        return true;
      }
    } else if (actionPoints >= 1){
      actionPoints--;
      return true;
    }
    return false;
  }

  public boolean chopAP() {
    if (actionPoints < 1) {
      return false;
    }
    actionPoints--;
    return true;
  }

  public boolean extinguishAP() {
    if (actionPoints < 1) {
      return false;
    }
    actionPoints--;
    return true;
  }

  public boolean moveWithVictimAP() {
    if (actionPoints < 2) {
      return false;
    }
    actionPoints -= 2;
    return true;
  }

  public boolean openCloseDoorAP() {
    if (actionPoints < 1) {
      return false;
    }
    actionPoints--;
    return true;
  }

  public static void reset() {
    FIREFIGHTERS.clear();
  }
}
