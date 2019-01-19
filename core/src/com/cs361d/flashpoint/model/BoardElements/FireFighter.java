package com.cs361d.flashpoint.model.BoardElements;

import com.cs361d.flashpoint.model.FireFighterRoles.Card;

public class FireFighter {

  private FireFighterColor color;
  private Card role;
  private static final int MAX_ACTION_POINTS = 8;
  private int actionPoints;
  private int numVictimsSaved;

  public FireFighter(FireFighterColor color) {
    this.color = color;
  }

  public FireFighter(Card pCard, FireFighterColor color, int numVictimsSaved, int actionPoints) {
    if (actionPoints > MAX_ACTION_POINTS) {
      throw new IllegalArgumentException("Action points cannot exceed 8 was: " + actionPoints);
    }
    this.role = pCard;
    this.color = color;
    this.numVictimsSaved = numVictimsSaved;
    this.actionPoints = actionPoints;
  }

  public int getNumVictimsSaved() {
    return numVictimsSaved;
  }

  public void addNumVictimsSaved() {
    this.numVictimsSaved += numVictimsSaved;
  }

  public FireFighterColor getColor() {
    return color;
  }

  public Card getRole() {
    return role;
  }

  public void setRole(Card role) {
    this.role = role;
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
}
